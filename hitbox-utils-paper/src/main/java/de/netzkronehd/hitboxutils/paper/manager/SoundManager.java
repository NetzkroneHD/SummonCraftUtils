package de.netzkronehd.hitboxutils.paper.manager;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import org.bukkit.Sound;

import java.sql.SQLException;
import java.util.UUID;

public class SoundManager extends Manager {

    public SoundManager(HitBoxUtils hitBox) {
        super(hitBox);
    }

    @Override
    public void onLoad() {

    }

    public void setSound(UUID uuid, Sound sound) {
        try {
            if (hitBox.getDatabaseManager().getDatabase().existsPlayerSound(uuid)) {
                hitBox.getDatabaseManager().getDatabase().setPlayerSound(uuid, sound.name());
            } else hitBox.getDatabaseManager().getDatabase().createPlayerSound(uuid, sound.name());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Sound getSound(UUID uuid) {
        try {
            final String sound = hitBox.getDatabaseManager().getDatabase().getPlayerSound(uuid);
            if (sound == null) return null;
            return Sound.valueOf(sound.toUpperCase());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
