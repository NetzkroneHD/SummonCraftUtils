package de.netzkronehd.hitboxutils.database.cache.packet.proxy;

import de.netzkronehd.hitboxutils.database.cache.model.TeamUserModel;
import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode
public class FoundProxyPlayerPacket extends HitBoxPacket {

    private final TeamUserModel teamUserModel;
    private final String proxy;
    private final UUID requester;

    public FoundProxyPlayerPacket(String source, TeamUserModel teamUserModel, String proxy, UUID requester) {
        super(source);
        this.teamUserModel = teamUserModel;
        this.proxy = proxy;
        this.requester = requester;
    }
}
