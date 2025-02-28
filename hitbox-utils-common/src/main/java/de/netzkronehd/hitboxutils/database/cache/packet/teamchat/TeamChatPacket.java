package de.netzkronehd.hitboxutils.database.cache.packet.teamchat;

import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class TeamChatPacket extends HitBoxPacket {

    private final String msg;

    public TeamChatPacket(String source, String msg) {
        super(source);
        this.msg = msg;
    }

}
