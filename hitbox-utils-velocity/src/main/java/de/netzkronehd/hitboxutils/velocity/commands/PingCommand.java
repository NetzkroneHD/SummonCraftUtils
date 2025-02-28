package de.netzkronehd.hitboxutils.velocity.commands;

import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;

public class PingCommand extends HitBoxCommand {

    public PingCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "ping", "hbping");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if (args.length == 1) {
            final HitBoxPlayer tp = hitBoxUtils.getPlayer(args[0]);
            if (tp == null) {
                hp.sendMessage(Messages.PLAYER_OFFLINE);
                return;
            }
            hp.sendMessageColored("Ping of "+tp.getPrefixAndName()+"§7 is§e "+tp.getPingText()+"ms§7.");
        } else {
            hp.sendMessage("Your ping is§e "+hp.getPingText()+"ms§7.");
        }

    }

}
