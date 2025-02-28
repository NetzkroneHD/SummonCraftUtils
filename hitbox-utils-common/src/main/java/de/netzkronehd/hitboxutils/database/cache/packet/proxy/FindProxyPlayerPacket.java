package de.netzkronehd.hitboxutils.database.cache.packet.proxy;

import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode
public class FindProxyPlayerPacket extends HitBoxPacket {

    private final String playerName;
    private final UUID requester;

    public FindProxyPlayerPacket(String source, String playerName, UUID requester) {
        super(source);
        this.playerName = playerName;
        this.requester = requester;
    }

}
