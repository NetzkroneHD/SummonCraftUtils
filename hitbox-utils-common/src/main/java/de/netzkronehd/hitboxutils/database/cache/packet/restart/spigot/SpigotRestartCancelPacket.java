package de.netzkronehd.hitboxutils.database.cache.packet.restart.spigot;

import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class SpigotRestartCancelPacket extends HitBoxPacket {

    private final boolean cancel;

    public SpigotRestartCancelPacket(String source, boolean cancel) {
        super(source);
        this.cancel = cancel;
    }

}
