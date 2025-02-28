package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.message.Messages;

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
