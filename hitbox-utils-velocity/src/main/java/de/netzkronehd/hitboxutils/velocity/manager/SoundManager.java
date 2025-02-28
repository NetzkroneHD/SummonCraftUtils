package de.netzkronehd.hitboxutils.velocity.manager;

import de.netzkronehd.hitboxutils.sound.GlobalSound;
import de.netzkronehd.hitboxutils.utils.Utils;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;
import lombok.*;
import net.kyori.adventure.text.Component;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static de.netzkronehd.hitboxutils.velocity.message.MessageColor.formatColoredValue;

@Getter
public class SoundManager extends Manager {

    private final Map<UUID, Long> cooldown;

    private SoundManagerConfig config;

    public SoundManager(HitBoxUtils hitBox) {
        super(hitBox);
        cooldown = new HashMap<>();
        config = new SoundManagerConfig();
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
        cooldown.clear();
        config = getConfigJson(SoundManagerConfig.class);
    }

    public Component getJoinMessage(HitBoxPlayer hp) {
        return formatColoredValue(config.joinMessage.replace("%player%", hp.getPrefixAndName()));
    }

    public boolean isCooldownOver(UUID uuid) {
        return Utils.isOver(cooldown.get(uuid));
    }

    public void setCooldown(UUID uuid) {
        cooldown.put(uuid, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
    }

    public void setSound(UUID uuid, GlobalSound sound) {
        try {
            if (hitBox.getDatabaseManager().getDatabase().existsPlayerSound(uuid)) {
                hitBox.getDatabaseManager().getDatabase().setPlayerSound(uuid, sound.name());
            } else hitBox.getDatabaseManager().getDatabase().createPlayerSound(uuid, sound.name());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public GlobalSound getSound(UUID uuid) {
        try {
            final String sound = hitBox.getDatabaseManager().getDatabase().getPlayerSound(uuid);
            if (sound == null) return null;
            return GlobalSound.valueOf(sound.toUpperCase());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SoundManagerConfig extends ManagerConfig {

        private String joinMessage;

        @Override
        public SoundManagerConfig loadDefaults() {
            joinMessage = "%player%&7 has entered HitBox-Network.";
            return this;
        }
    }


}
