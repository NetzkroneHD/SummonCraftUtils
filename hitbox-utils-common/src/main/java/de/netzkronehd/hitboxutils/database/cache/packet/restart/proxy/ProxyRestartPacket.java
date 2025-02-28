package de.netzkronehd.hitboxutils.database.cache.packet.restart.proxy;

import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class ProxyRestartPacket extends HitBoxPacket {

    private final int countdown;

    public ProxyRestartPacket(String source, int countdown) {
        super(source);
        this.countdown = countdown;
    }


}
