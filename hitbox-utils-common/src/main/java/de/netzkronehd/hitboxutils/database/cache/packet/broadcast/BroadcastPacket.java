package de.netzkronehd.hitboxutils.database.cache.packet.broadcast;

import de.netzkronehd.hitboxutils.database.cache.model.BroadcastType;
import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import de.netzkronehd.hitboxutils.sound.GlobalSound;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class BroadcastPacket extends HitBoxPacket {

    private final String server;
    private final String message;
    private final BroadcastType type;
    private final GlobalSound sound;

    public BroadcastPacket(String source, String server, String message, BroadcastType type, GlobalSound sound) {
        super(source);
        this.server = server;
        this.message = message;
        this.type = type;
        this.sound = sound;
    }
}
