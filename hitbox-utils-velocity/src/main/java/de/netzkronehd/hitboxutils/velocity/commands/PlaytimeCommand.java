package de.netzkronehd.hitboxutils.velocity.commands;

import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;

public class PlaytimeCommand extends HitBoxCommand {

    public PlaytimeCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "playtime", "onlinetime");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if (args.length == 1 && hasPermission(hp, "other")) {
            final HitBoxPlayer ht = hitBoxUtils.getPlayer(args[0]);
            if (ht == null) {
                hp.sendMessage(Messages.PLAYER_OFFLINE);
                return;
            }
            hp.sendMessageColored("§e" + ht.getPrefixAndName() + "§7s tiempo de juego§8:§e " + ht.getFormattedPlaytime());

        } else {
            //Your playtime
            hp.sendMessage("Tu tiempo de juego§8:§e " + hp.getFormattedPlaytime());
        }
    }
}
