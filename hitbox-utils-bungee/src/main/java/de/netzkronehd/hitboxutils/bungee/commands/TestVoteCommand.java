package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.database.cache.model.VoteModel;

public class TestVoteCommand extends HitBoxCommand {

    public TestVoteCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "testvote", "votetest");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if(!hasCommandPermission(hp)) return;
        final VoteModel voteModel = new VoteModel(
                "ServiceName",
                hp.getName(),
                hp.getIp(),
                String.valueOf(System.currentTimeMillis()),
                new byte[0]
        );
        final boolean sent = hitBoxUtils.getVoteManager().voteReceived(voteModel);
        if(sent) {
            hp.sendMessage("§7Test vote packet sent. ('§8"+voteModel+"§7')");
        } else {
            hp.sendMessage("§cAn error occurred while sending the test vote packet.");
        }
    }
}
