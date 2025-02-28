package de.netzkronehd.hitboxutils.database.cache.packet.restart;

import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class RestartCancelPacket extends HitBoxPacket {

    private final boolean cancel;

    public RestartCancelPacket(String source, boolean cancel) {
        super(source);
        this.cancel = cancel;
    }
}
