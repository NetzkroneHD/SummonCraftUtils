package de.netzkronehd.hitboxutils.database.cache.packet.sound;

import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import de.netzkronehd.hitboxutils.sound.GlobalSound;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode
public class GlobalSoundPacket extends HitBoxPacket {

    private final UUID uuid;
    private final GlobalSound globalSound;
    private final float volume;
    private final float pitch;

    public GlobalSoundPacket(String source, UUID uuid, GlobalSound globalSound, float volume, float pitch) {
        super(source);
        this.uuid = uuid;
        this.globalSound = globalSound;
        this.volume = volume;
        this.pitch = pitch;
    }
}
