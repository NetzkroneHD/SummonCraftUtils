package de.netzkronehd.hitboxutils.database.cache.packet.support;

import de.netzkronehd.hitboxutils.database.cache.model.SupportChatModel;
import de.netzkronehd.hitboxutils.database.cache.model.TeamUserModel;
import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class SupportChatMessagePacket extends HitBoxPacket {

    private final SupportChatModel supportChatModel;
    private final TeamUserModel user;
    private final String message;

    public SupportChatMessagePacket(String source, SupportChatModel supportChatModel, TeamUserModel user, String message) {
        super(source);
        this.supportChatModel = supportChatModel;
        this.user = user;
        this.message = message;
    }
}
