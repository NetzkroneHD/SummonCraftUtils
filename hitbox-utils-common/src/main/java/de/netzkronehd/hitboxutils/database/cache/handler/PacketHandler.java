package de.netzkronehd.hitboxutils.database.cache.handler;

import de.netzkronehd.hitboxutils.database.cache.Synchronizer;
import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.PacketContainer;
import lombok.Getter;

import static de.netzkronehd.hitboxutils.database.cache.RedisClient.RedisKeys.PACKET_PREFIX;


@Getter
public class PacketHandler extends CustomMessageHandler {

    private final Synchronizer synchronizer;

    public PacketHandler(Synchronizer synchronizer) {
        super(PACKET_PREFIX);
        this.synchronizer = synchronizer;
    }

    @Override
    public void processMessage(String message) {
        final PacketContainer container = PacketContainer.fromMessage(message);
        final HitBoxPacket packet = container.getPacket();
        synchronizer.packedReceived(packet);
    }
}
