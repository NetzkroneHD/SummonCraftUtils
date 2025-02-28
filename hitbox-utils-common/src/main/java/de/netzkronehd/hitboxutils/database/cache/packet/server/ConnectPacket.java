package de.netzkronehd.hitboxutils.database.cache.packet.server;

import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode
public class ConnectPacket extends HitBoxPacket {

    private final UUID uuid;
    private final String server;

    public ConnectPacket(String source, UUID uuid, String server) {
        super(source);
        this.uuid = uuid;
        this.server = server;
    }

}
