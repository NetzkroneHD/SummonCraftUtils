package de.netzkronehd.hitboxutils.paper.command;

import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;

import java.util.Collections;
import java.util.List;

public class ScreenShareCommand extends HitBoxCommand {

    public ScreenShareCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "screenshare", "ss");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if (!(hp.isStaff() || hasCommandPermission(hp))) return;
        if (args.length == 1) {
            final HitBoxPlayer tp = hitBoxUtils.getPlayer(args[0]);
            if (tp == null) {
                hp.sendMessage(Messages.PLAYER_OFFLINE);
                return;
            }

            if (checkStaff(hp, tp)) return;
            if (tp.isFrozen()) {
                hp.sendMessage("That player is already frozen.");
                return;
            }
            tp.teleport(hp.getPlayer().getLocation());
            tp.freeze(hp);

        } else sendHelp(hp);
    }

    @Override
    public void sendHelp(HitBoxPlayer ep) {
        ep.sendUsage("ss§8 <§ePlayer§8>");
    }

    @Override
    public List<String> onTab(HitBoxPlayer ep, String[] args) {
        if (args.length == 1) {
            if (hasPermission(ep)) {
                return getPlayerTabComplete(args[0]);
            }
        }
        return Collections.emptyList();
    }

    private boolean checkStaff(HitBoxPlayer ep, HitBoxPlayer tp) {
        if (tp.isStaff()) {
            ep.sendMessage("That player is a staff member.");
            return true;
        }
        return false;
    }

}
