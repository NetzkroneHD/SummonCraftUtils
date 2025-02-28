package de.netzkronehd.hitboxutils.database.cache.packet.teamchat;

import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class TeamChatReloadPacket extends HitBoxPacket {

    public TeamChatReloadPacket(String source) {
        super(source);
    }
}
