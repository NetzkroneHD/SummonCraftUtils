package de.netzkronehd.hitboxutils.bungee.manager;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.vote.VoteHandler;
import de.netzkronehd.hitboxutils.database.cache.model.VoteModel;

public class VoteManager extends Manager {

    public VoteManager(HitBoxUtils hitBox) {
        super(hitBox);
    }

    @Override
    public void onLoad() {

    }

    public boolean voteReceived(VoteModel voteModel) {
        try {
            final VoteHandler voteHandler = new VoteHandler(hitBox);
            voteHandler.voteReceived(voteModel);
            return true;
        } catch (Throwable throwable) {
            hitBox.getLogger().severe("An error occurred while processing vote packet '"+voteModel+"': "+throwable);
        }
        return false;
    }
}
