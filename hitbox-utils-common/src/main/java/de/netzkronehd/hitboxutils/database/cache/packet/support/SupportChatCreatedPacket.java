package de.netzkronehd.hitboxutils.database.cache.packet.support;

import de.netzkronehd.hitboxutils.database.cache.model.SupportChatModel;
import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class SupportChatCreatedPacket extends HitBoxPacket {

    private final SupportChatModel supportChatModel;

    public SupportChatCreatedPacket(String source, SupportChatModel supportChatModel) {
        super(source);
        this.supportChatModel = supportChatModel;
    }
}
