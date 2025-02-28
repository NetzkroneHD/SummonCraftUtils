package de.netzkronehd.hitboxutils.database.cache.packet.teamchat;

import de.netzkronehd.hitboxutils.database.cache.model.TeamUserModel;
import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class TeamPlayerLeavePacket extends HitBoxPacket {

    private final TeamUserModel teamUser;

    public TeamPlayerLeavePacket(String source, TeamUserModel teamUser) {
        super(source);
        this.teamUser = teamUser;
    }
}
