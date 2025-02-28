package de.netzkronehd.hitboxutils.velocity.manager;

import de.netzkronehd.hitboxutils.bansystemapi.PunishmentType;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.punishment.TimePunishment;
import lombok.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
public class PunishmentManager extends Manager {

    private final Map<String, TimePunishment> banTimes;
    private final Map<String, TimePunishment> muteTimes;

    private PunishmentManagerConfig config;

    public PunishmentManager(HitBoxUtils hitBox) {
        super(hitBox);
        banTimes = new HashMap<>();
        muteTimes = new HashMap<>();
        config = new PunishmentManagerConfig();
    }

    @Override
    public void onLoad() {
        createFile();
        readFile();
    }

    @Override
    public void createFile() {
        if (!file.exists()) {
            config.loadDefaults();
            save(config);
        }
    }

    @Override
    public void readFile() {
        banTimes.clear();
        muteTimes.clear();
        config = getConfigJson(PunishmentManagerConfig.class);

        banTimes.putAll(config.banTimes);
        muteTimes.putAll(config.muteTimes);

        log("Loaded punishments.");
        log("Banns: "+config.banTimes);
        log("Mutes: "+config.muteTimes);

    }

    public String calculateTime(UUID uuid, TimePunishment punishment, PunishmentType type) {
        int punishments;
        try {
            final Map<String, Integer> reasons = (type == PunishmentType.BAN ? hitBox.getBanSystemApi().listBanPunishments(uuid):hitBox.getBanSystemApi().listMutePunishments(uuid));
            Integer count = reasons.get(punishment.getReason().toLowerCase());
            if (count == null) count = 0;
            punishments = count + 1;
        } catch (SQLException e) {
            punishments = 1;
        }
        long time = punishment.getTime() * punishments;
        if (time > punishment.getLimit()) time = punishment.getLimit();
        return time + punishment.getTimeUnit();
    }

    public Map<String, TimePunishment> getPunishments(PunishmentType type) {
        if (type == PunishmentType.BAN) {
            return this.banTimes;
        } else if (type == PunishmentType.MUTE) {
            return this.muteTimes;
        } else return null;
    }

    public TimePunishment getPunishment(PunishmentType type, String id) {
        if (type == PunishmentType.BAN) {
            return this.banTimes.get(id);
        } else if (type == PunishmentType.MUTE) {
            return this.muteTimes.get(id);
        }
        return null;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PunishmentManagerConfig extends ManagerConfig {

        private Map<String, TimePunishment> banTimes;
        private Map<String, TimePunishment> muteTimes;

        @Override
        public PunishmentManagerConfig loadDefaults() {
            banTimes = new HashMap<>();
            muteTimes = new HashMap<>();

            put(PunishmentType.BAN, new TimePunishment("uso-de-hacks-[admitidos]", "Uso de Hacks [Admitidos]", 30, 30*6, "d"));
            put(PunishmentType.BAN, new TimePunishment("uso-de-hacks-[found-in-ss]", "Uso de Hacks [Found in SS]", 60, 60*6, "d"));
            put(PunishmentType.BAN, new TimePunishment("uso-de-hacks-[pruebas-validas]", "Uso de Hacks [Pruebas válidas]", 60, 60*6, "d"));
            put(PunishmentType.BAN, new TimePunishment("uso-de-hacks-[refuse-ss]", "Uso de Hacks [Refuse SS]", 60, 60*6, "d"));
            put(PunishmentType.BAN, new TimePunishment("hacerse-pasar-por-staff", "Hacerse pasar por Staff", 10, 10*6, "d"));
            put(PunishmentType.BAN, new TimePunishment("falta-de-respeto-al-servidor", "Falta de Respeto al Servidor", -1, -1, "d"));
            put(PunishmentType.BAN, new TimePunishment("estafas", "Estafas", 2, 14, "d"));
            put(PunishmentType.BAN, new TimePunishment("bug-abuse", "Bug Abuse", 1, 14, "d"));
            put(PunishmentType.BAN, new TimePunishment("farm-kill", "FarmKill", 3, 7, "d"));
            put(PunishmentType.BAN, new TimePunishment("grieffing-de-bases-outside", "Grieffing de bases (Outside)", 1, 7, "d"));
            put(PunishmentType.BAN, new TimePunishment("grieffing-de-bases-inside", "Grieffing de bases (Inside)", 3, 15, "d"));

            put(PunishmentType.MUTE, new TimePunishment("spam", "Spam", 15, TimeUnit.HOURS.toMinutes(12), "m"));
            put(PunishmentType.MUTE, new TimePunishment("flood", "Flood", 5, TimeUnit.DAYS.toMinutes(1), "m"));
            put(PunishmentType.MUTE, new TimePunishment("toxicidad-excesiva", "Toxicidad Excesiva", 1, TimeUnit.DAYS.toHours(2), "h"));
            put(PunishmentType.MUTE, new TimePunishment("toxicidad", "Toxicidad", 15, TimeUnit.HOURS.toMinutes(1), "m"));
            put(PunishmentType.MUTE, new TimePunishment("enlace-inapropiado", "Enlace inapropiado", 4, TimeUnit.DAYS.toHours(2), "h"));
            put(PunishmentType.MUTE, new TimePunishment("racismo", "Racismo", 12, TimeUnit.DAYS.toHours(5), "h"));
            return this;

        }

        private void put(PunishmentType type, TimePunishment punishment) {
            if(type == PunishmentType.BAN) {
                banTimes.put(punishment.getId(), punishment);
            } else if(type == PunishmentType.MUTE) {
                muteTimes.put(punishment.getId(), punishment);
            }
        }

    }

}
