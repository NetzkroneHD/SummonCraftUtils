package de.netzkronehd.hitboxutils.database.cache.packet.restart.custom;

import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class CustomRestartPacket extends HitBoxPacket {

    private final String server;
    private final int countdown;

    public CustomRestartPacket(String source, String server, int countdown) {
        super(source);
        this.server = server;
        this.countdown = countdown;
    }
}
