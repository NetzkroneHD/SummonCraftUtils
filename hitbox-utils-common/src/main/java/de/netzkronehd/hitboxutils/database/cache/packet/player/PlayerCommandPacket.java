package de.netzkronehd.hitboxutils.database.cache.packet.player;

import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode
public class PlayerCommandPacket extends HitBoxPacket {

    private final UUID uuid;
    private final String command;

    public PlayerCommandPacket(String source, UUID uuid, String command) {
        super(source);
        this.uuid = uuid;
        this.command = command;
    }

}

