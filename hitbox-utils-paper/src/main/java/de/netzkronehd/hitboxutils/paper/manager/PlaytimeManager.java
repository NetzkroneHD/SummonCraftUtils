package de.netzkronehd.hitboxutils.paper.manager;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.player.PlayerPlaytime;
import de.netzkronehd.hitboxutils.utils.Utils;

import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class PlaytimeManager extends Manager {

    public PlaytimeManager(HitBoxUtils hitBox) {
        super(hitBox);
    }

    @Override
    public void onLoad() {

    }

    public String getFormattedPlaytime(PlayerPlaytime playerPlaytime) {
        final long currentPlaytime = playerPlaytime.getPlaytime();
        return Utils.getRemainingTimeInHours(currentPlaytime);
    }

    public PlayerPlaytime getPlaytime(UUID uuid) {
        try {
            return hitBox.getDatabaseManager().getDatabase().getPlaytime(uuid);
        } catch (SQLException e) {
            log(Level.SEVERE, "Error while loading playtime: " + e);
            return PlayerPlaytime.builder()
                    .uuid(uuid)
                    .playtime(0)
                    .timeJoined(System.currentTimeMillis())
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


}
