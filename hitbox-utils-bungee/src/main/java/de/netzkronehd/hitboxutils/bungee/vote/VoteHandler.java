package de.netzkronehd.hitboxutils.bungee.vote;

import com.vexsoftware.votifier.bungee.NuVotifier;
import com.vexsoftware.votifier.bungee.events.VotifierEvent;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.net.VotifierSession;
import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.database.cache.model.VoteModel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VoteHandler {

    private final HitBoxUtils hitBox;

    public void voteReceived(VoteModel voteModel) {
        final Vote vote = new Vote(
                voteModel.serviceName(),
                voteModel.username(),
                voteModel.address(),
                voteModel.timeStamp(),
                voteModel.additionalData()
        );

        hitBox.getProxy().getPluginManager().callEvent(new VotifierEvent(vote));
        hitBox.getProxy().getPluginManager().getPlugins().stream()
                .filter(NuVotifier.class::isInstance)
                .findFirst().ifPresentOrElse(plugin -> {
                    hitBox.getLogger().info("NuVotifier found. Calling onVoteReceived.");
                    final NuVotifier nuVotifier = (NuVotifier) plugin;
                    nuVotifier.onVoteReceived(vote, VotifierSession.ProtocolVersion.TWO, vote.getAddress());
                }, () -> hitBox.getLogger().warning("NuVotifier not found. Ignoring vote packet."));
    }

}
