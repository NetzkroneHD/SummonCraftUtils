package de.netzkronehd.hitboxutils.database.cache.packet.server;

import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class ServerStoppedPacket extends HitBoxPacket {

    private final String server;

    public ServerStoppedPacket(String source, String server) {
        super(source);
        this.server = server;
    }
}
