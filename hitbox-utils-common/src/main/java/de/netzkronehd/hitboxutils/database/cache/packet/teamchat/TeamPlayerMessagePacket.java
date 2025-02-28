package de.netzkronehd.hitboxutils.database.cache.packet.teamchat;

import de.netzkronehd.hitboxutils.database.cache.model.TeamUserModel;
import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class TeamPlayerMessagePacket extends HitBoxPacket {

    private final TeamUserModel teamUser;
    private final String message;
    private final long timestamp;

    public TeamPlayerMessagePacket(String source, TeamUserModel teamUser, String message, long timestamp) {
        super(source);
        this.teamUser = teamUser;
        this.message = message;
        this.timestamp = timestamp;
    }

}
