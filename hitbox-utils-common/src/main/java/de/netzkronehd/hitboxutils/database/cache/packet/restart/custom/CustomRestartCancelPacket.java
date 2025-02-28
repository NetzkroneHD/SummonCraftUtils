package de.netzkronehd.hitboxutils.database.cache.packet.restart.custom;

import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class CustomRestartCancelPacket extends HitBoxPacket {

    private final String server;
    private final boolean cancel;

    public CustomRestartCancelPacket(String source, String server, boolean cancel) {
        super(source);
        this.server = server;
        this.cancel = cancel;
    }


}
