package de.netzkronehd.hitboxutils.bungee.manager;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.sound.GlobalSound;
import de.netzkronehd.hitboxutils.utils.Utils;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static de.netzkronehd.hitboxutils.message.HexColor.translateHexCodesInComponents;

@Getter
public class SoundManager extends Manager {

    private final Map<UUID, Long> cooldown;
    private String joinMessage;

    public SoundManager(HitBoxUtils hitBox) {
        super(hitBox);
        cooldown = new HashMap<>();
    }

    @Override
    public void onLoad() {
        createFile();
        readFile();
    }

    @Override
    public void createFile() {
        if (!file.exists()) {
            cfg.set("join-message", "%player%&7 has entered HitBox-Network.");
            save();
        }
    }

    @Override
    public void readFile() {
        cooldown.clear();
        joinMessage = cfg.getString("join-message", "%player%&7 has entered HitBox-Network.");
    }

    public TextComponent getJoinMessage(HitBoxPlayer hp) {
        return translateHexCodesInComponents(joinMessage.replace("%player%", hp.getPrefixAndName()));
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


}
