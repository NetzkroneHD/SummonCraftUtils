package de.netzkronehd.hitboxutils.paper.manager;


import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.utils.Utils;
import org.javatuples.KeyValue;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerManager extends Manager {

    private final File players = new File("plugins/HitBox", "playercount.txt");

    public PlayerManager(HitBoxUtils hitBox) {
        super(hitBox);
    }

    @Override
    public void onLoad() {
        createFile();

    }

    @Override
    public void createFile() {
        if (!players.exists()) {
            try {
                players.createNewFile();
            } catch (IOException e) {
                log("Could not create file '"+players.getAbsolutePath()+"': "+e);
            }
        }
    }

    public void updatePlayerCount() {
        setPlayerCount(hitBox.getPlayers().size());
    }

    public void setPlayerCount(int count) {
        try {
            Utils.writeDataInFile(players, String.valueOf(count), false);
        } catch (IOException e) {
            log("Could not write player count: "+e);
        }
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

    public String getName(UUID uuid) {
        try {
            return hitBox.getDatabaseManager().getDatabase().getName(uuid);
        } catch (SQLException e) {
            return null;
        }
    }

    public UUID getUuid(String name) {
        try {
            return hitBox.getDatabaseManager().getDatabase().getUuid(name.toLowerCase());
        } catch (SQLException e) {
            return null;
        }
    }

    public KeyValue<UUID, String> getUuidAndName(String name) {
        try {
            return hitBox.getDatabaseManager().getDatabase().getUuidAndName(name.toLowerCase());
        } catch (SQLException e) {
            return null;
        }
    }

}
