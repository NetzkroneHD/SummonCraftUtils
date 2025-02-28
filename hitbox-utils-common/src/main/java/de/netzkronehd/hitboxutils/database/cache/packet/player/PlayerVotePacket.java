package de.netzkronehd.hitboxutils.database.cache.packet.player;

import de.netzkronehd.hitboxutils.database.cache.model.VoteModel;
import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class PlayerVotePacket extends HitBoxPacket {

    private final VoteModel vote;

    public PlayerVotePacket(String source, VoteModel vote) {
        super(source);
        this.vote = vote;
    }
}
