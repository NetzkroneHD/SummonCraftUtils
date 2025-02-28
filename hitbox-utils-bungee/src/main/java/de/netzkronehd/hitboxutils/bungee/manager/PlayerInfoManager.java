package de.netzkronehd.hitboxutils.bungee.manager;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.player.PlayerInfo;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class PlayerInfoManager extends Manager {

    public PlayerInfoManager(HitBoxUtils hitBox) {
        super(hitBox);
    }

    @Override
    public void onLoad() {

    }

    public void setPlayerInfo(PlayerInfo playerInfo) {
        try {
            hitBox.getDatabaseManager().getDatabase().setPlayerInfo(playerInfo.getUuid(), playerInfo);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<PlayerInfo> getPlayerInfo(UUID uuid) {
        try {
            return hitBox.getDatabaseManager().getDatabase().getPlayerInfo(uuid);
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

}
