package de.netzkronehd.hitboxutils.paper.manager;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PunishmentManager extends Manager {


    public PunishmentManager(HitBoxUtils hitBox) {
        super(hitBox);
    }

    @Override
    public void onLoad() {
        createFile();
        readFile();
    }

    @Override
    public void createFile() {
        if (!file.exists()) {
            cfg.set("punishment-limit", TimeUnit.DAYS.toMillis(7));
            save();
        }
    }

    public boolean isLimit(long time, Punishments punishment) {
        if (punishment.getLimit() == -1) return false;
        return time >= punishment.getLimit();
    }

    public void createPunishment(UUID uuid, PunishmentType type, String reason) {
        try {
            hitBox.getDatabaseManager().getDatabase().createPunishment(uuid, type.name(), reason);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String calculatePunishment(UUID uuid, Punishments punishment) {
        int punishments;
        try {
            punishments = hitBox.getDatabaseManager().getDatabase().getPunishments(uuid, punishment.name()) + 1;
        } catch (SQLException e) {
            punishments = 1;
        }
        final long time = punishment.getTimeUnit().toMillis(punishment.getTime()) * punishments;
        if (isLimit(time, punishment)) return TimeUnit.MILLISECONDS.toHours(punishment.getLimit()) + "h";

        return (punishment.getTime() * punishments) + punishment.getTimeFormat();
    }

    @AllArgsConstructor
    @Getter
    public enum PunishmentType {
        BAN("tempban"),
        MUTE("tempmute");

        private String command;

        public void setCommand(String command) {
            this.command = command;
        }
    }

    @Getter
    @AllArgsConstructor
    public enum Punishments {

        CHEATING("Cheating", TimeUnit.HOURS, 9, "h", TimeUnit.DAYS.toMillis(7), PunishmentType.BAN),
        KILLAURA("Killaura", TimeUnit.DAYS, 1, "d", TimeUnit.DAYS.toMillis(7), PunishmentType.BAN),
        AUTO_TOTEM("Auto-Totem", TimeUnit.HOURS, 5, "h", TimeUnit.DAYS.toMillis(7), PunishmentType.BAN),
        SPEED("Speed", TimeUnit.HOURS, 3, "h", TimeUnit.DAYS.toMillis(7), PunishmentType.BAN),
        X_RAY("X-Ray", TimeUnit.HOURS, 3, "h", TimeUnit.DAYS.toMillis(7), PunishmentType.BAN),
        AUTO_CRITICAL("Auto Critical", TimeUnit.DAYS, 1, "d", TimeUnit.DAYS.toMillis(7), PunishmentType.BAN),
        FLY("Fly", TimeUnit.DAYS, 1, "d", TimeUnit.DAYS.toMillis(7), PunishmentType.BAN),

        TOXIC_BEHAVIOR("Disrespectful-Behavior", TimeUnit.MINUTES, 10, "m", TimeUnit.MINUTES.toMillis(60), PunishmentType.MUTE),
        FLOOD("Flood", TimeUnit.MINUTES, 20, "m", TimeUnit.MINUTES.toMillis(80), PunishmentType.MUTE),
        SPAM("Spam", TimeUnit.MINUTES, 30, "m", TimeUnit.MINUTES.toMillis(90), PunishmentType.MUTE),
        SLIGHT_INSULT("Slight-Insult", TimeUnit.MINUTES, 10, "m", TimeUnit.MINUTES.toMillis(90), PunishmentType.MUTE),
        SERIOUS_INSULT("Serious-Insult", TimeUnit.HOURS, 2, "h", TimeUnit.DAYS.toMillis(1), PunishmentType.MUTE),
        THIRD_PARTY_ADVERTISING("Third-Party Advertising", TimeUnit.DAYS, 1, "d", TimeUnit.DAYS.toMillis(7), PunishmentType.MUTE),

        ;
        private final String name;
        private final TimeUnit timeUnit;
        private final long time;
        private final String timeFormat;
        private final long limit;
        private final PunishmentType type;

    }

}
