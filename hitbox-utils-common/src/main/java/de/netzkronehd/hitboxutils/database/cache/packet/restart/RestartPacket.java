package de.netzkronehd.hitboxutils.database.cache.packet.restart;

import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class RestartPacket extends HitBoxPacket {

    private final int countdown;

    public RestartPacket(String source, int countdown) {
        super(source);
        this.countdown = countdown;
    }
}
