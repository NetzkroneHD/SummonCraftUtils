package de.netzkronehd.hitboxutils.paper.command;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;

public class ClearItemsCommand extends HitBoxCommand {

    public ClearItemsCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "clearitems", "clearitem");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if(!hasCommandPermission(hp)) return;
        if(args.length == 1) {
            try {
                final int time = Integer.parseInt(args[0]);
                hitBoxUtils.getClearItemManager().setCountdown(time);
                hp.sendMessage("Successfully set the Clear-Items-Countdown to§e "+time+"§7 seconds.");
            } catch (NumberFormatException ex) {
                hp.sendMessage("Please use a number.");
            }
        } else sendHelp(hp);
    }


    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("clearitems§8 <§etime§8>");
    }
}
