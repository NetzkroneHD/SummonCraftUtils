package de.netzkronehd.hitboxutils.database.cache.packet.restart.proxy;

import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class ProxyRestartCancelPacket extends HitBoxPacket {

    private final boolean cancel;

    public ProxyRestartCancelPacket(String source, boolean cancel) {
        super(source);
        this.cancel = cancel;
    }

}
