package de.netzkronehd.hitboxutils.database.cache.packet.teamchat;

import de.netzkronehd.hitboxutils.database.cache.model.TeamUserModel;
import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import lombok.Getter;

@Getter
public class TeamPlayerJoinPacket extends HitBoxPacket {

    private final TeamUserModel teamUser;

    public TeamPlayerJoinPacket(String source, TeamUserModel teamUser) {
        super(source);
        this.teamUser = teamUser;
    }
}
