package de.netzkronehd.hitboxutils.database.cache.packet.litebans;

import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@EqualsAndHashCode
@ToString
public class LitebansPunishPacket extends HitBoxPacket {

    private final UUID commandExecutor;
    private final UUID uuid;
    private final String command;

    public LitebansPunishPacket(String source, UUID commandExecutor, UUID uuid, String command) {
        super(source);
        this.commandExecutor = commandExecutor;
        this.uuid = uuid;
        this.command = command;
    }
}
