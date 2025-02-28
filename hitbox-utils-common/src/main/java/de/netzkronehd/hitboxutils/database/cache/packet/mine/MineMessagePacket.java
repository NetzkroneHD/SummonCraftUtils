package de.netzkronehd.hitboxutils.database.cache.packet.mine;

import de.netzkronehd.hitboxutils.database.cache.model.MineModel;
import de.netzkronehd.hitboxutils.database.cache.model.UserModel;
import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class MineMessagePacket extends HitBoxPacket {

    private final UserModel user;
    private final MineModel mine;

    public MineMessagePacket(String source, UserModel user, MineModel mine) {
        super(source);
        this.user = user;
        this.mine = mine;
    }
}
