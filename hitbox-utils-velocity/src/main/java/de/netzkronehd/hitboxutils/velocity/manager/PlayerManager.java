package de.netzkronehd.hitboxutils.velocity.manager;

import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import org.javatuples.KeyValue;

import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

public class PlayerManager extends Manager {

    public PlayerManager(HitBoxUtils hitBox) {
        super(hitBox);
    }

    @Override
    public void onLoad() {

    }

    public Collection<UUID> getPlayersByIp(String ip) {
        return hitBox.getBanSystemApi().getUsersByIP(ip);
    }

    public void setName(UUID uuid, String name, long lastJoin) {
        try {
            if (hitBox.getDatabaseManager().getDatabase().existsName(uuid)) {
                hitBox.getDatabaseManager().getDatabase().setName(uuid, name, lastJoin);
            } else hitBox.getDatabaseManager().getDatabase().createPlayer(uuid, name, lastJoin);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Long getLastJoin(UUID uuid) {
        try {
            return hitBox.getDatabaseManager().getDatabase().getLastJoin(uuid);
        } catch (SQLException e) {
            return null;
        }
    }

    public KeyValue<UUID, String> getUuidAndName(String name) {
        try {
            return hitBox.getDatabaseManager().getDatabase().getUuidAndName(name);
        } catch (SQLException e) {
            return null;
        }
    }

    public String getName(UUID uuid) {
        try {
            return hitBox.getDatabaseManager().getDatabase().getName(uuid);
        } catch (SQLException e) {
            return null;
        }
    }

    public UUID getUuid(String name) {
        try {
            return hitBox.getDatabaseManager().getDatabase().getUuid(name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
