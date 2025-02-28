package de.netzkronehd.hitboxutils.database.cache.packet.support;

import de.netzkronehd.hitboxutils.database.cache.model.SupportChatModel;
import de.netzkronehd.hitboxutils.database.cache.model.TeamUserModel;
import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class SupportChatLeftPacket extends HitBoxPacket {

    private final SupportChatModel supportChatModel;
    private final TeamUserModel user;

    public SupportChatLeftPacket(String source, SupportChatModel supportChatModel, TeamUserModel user) {
        super(source);
        this.supportChatModel = supportChatModel;
        this.user = user;
    }
}
