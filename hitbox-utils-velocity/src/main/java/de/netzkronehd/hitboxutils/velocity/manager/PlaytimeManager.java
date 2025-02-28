package de.netzkronehd.hitboxutils.velocity.manager;

import de.netzkronehd.hitboxutils.player.PlayerPlaytime;
import de.netzkronehd.hitboxutils.utils.Utils;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import lombok.*;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

@Getter
public class PlaytimeManager extends Manager {

    private PlaytimeManagerConfig config;


    public PlaytimeManager(HitBoxUtils hitBox) {
        super(hitBox);
        config = new PlaytimeManagerConfig();
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
        config = getConfigJson(PlaytimeManagerConfig.class);
    }

    public String getFormattedPlaytime(PlayerPlaytime playerPlaytime) {
        return Utils.getRemainingTimeInHours(playerPlaytime.getPlaytime());
    }

    public List<PlayerPlaytime> getTopPlaytime() {
        try {
            return hitBox.getDatabaseManager().getDatabase().listPlaytimeDesc(config.maxPlaytimeTop);
        } catch (SQLException e) {
            log(Level.SEVERE, "Error while loading playtime top list: " + e);
            return Collections.emptyList();
        }
    }

    public PlayerPlaytime getPlaytime(UUID uuid) {
        try {
            return hitBox.getDatabaseManager().getDatabase().getPlaytime(uuid);
        } catch (SQLException e) {
            log(Level.SEVERE, "Error while loading playtime: " + e);
            return PlayerPlaytime.builder()
                    .uuid(uuid)
                    .playtime(0)
                    .timeJoined(-1)
                    .build();
        }
    }

    public void setPlaytime(UUID uuid, long playtime, long timeJoined) {
        try {
            hitBox.getDatabaseManager().getDatabase().setPlaytime(uuid, playtime, timeJoined);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addPlaytime(UUID uuid, long playtime, long timeJoined) {
        final long time = getPlaytime(uuid).getPlaytime() + playtime;
        setPlaytime(uuid, time, timeJoined);
    }

    public boolean exists(UUID uuid) {
        try {
            return hitBox.getDatabaseManager().getDatabase().existsPlaytime(uuid);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createPlaytime(UUID uuid, long playtime, long timeJoined) {
        try {
            hitBox.getDatabaseManager().getDatabase().createPlaytime(uuid, playtime, timeJoined);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlaytimeManagerConfig extends ManagerConfig {

        private int maxPlaytimeTop;

        @Override
        public PlaytimeManagerConfig loadDefaults() {
            maxPlaytimeTop = 5;
            return this;
        }
    }

}
