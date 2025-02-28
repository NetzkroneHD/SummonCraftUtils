package de.netzkronehd.hitboxutils.paper.command;

import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;

public class TeleportCommand extends HitBoxCommand {

    public TeleportCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "teleport", "tp");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if(!(hp.isStaff() || hasCommandPermission(hp))) return;

        if(args.length == 1) {
            final HitBoxPlayer tp = hitBoxUtils.getPlayer(args[0]);
            if(tp == null) {
                hp.sendMessage(Messages.PLAYER_OFFLINE);
                return;
            }

            hp.teleport(tp.getPlayer().getLocation());
            hp.sendMessage("You got teleported to§e "+tp.getPrefixAndName()+"§7.");
        } else if(args.length == 2) {
            final HitBoxPlayer tp = hitBoxUtils.getPlayer(args[0]);
            if(tp == null) {
                hp.sendMessage(Messages.PLAYER_OFFLINE);
                return;
            }
            final HitBoxPlayer op = hitBoxUtils.getPlayer(args[1]);
            if(op == null) {
                hp.sendMessage(Messages.PLAYER_OFFLINE);
                return;
            }
            tp.teleport(op.getPlayer().getLocation());
            hp.sendMessage("The player§e "+tp.getPrefixAndName()+"§7 got teleported to§b "+op.getPrefixAndName()+"§7.");
        } else if (args.length == 3) {
            try {
                final double x = Double.parseDouble(args[0].replace("~", String.valueOf(hp.getPlayer().getLocation().getBlockX())));
                final double y = Double.parseDouble(args[1].replace("~", String.valueOf(hp.getPlayer().getLocation().getBlockY())));
                final double z = Double.parseDouble(args[2].replace("~", String.valueOf(hp.getPlayer().getLocation().getBlockZ())));
                hp.teleport(new Location(hp.getPlayer().getWorld(), x, y, z));
            } catch (NumberFormatException ex) {
                hp.sendMessage("Please enter a number.");
            }
        } else sendHelp(hp);
    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("tp§8 <§ePlayer§8>");
        hp.sendUsage("tp§8 <§ePlayer§8> <§eTo§8>");
        hp.sendUsage("tp§8 <§ex§8> <§ey§8> <§ez§8>");
    }

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if (hasPermission(hp)) {
            if(args.length == 1) {
                return getPlayerTabComplete(args[0]);
            } else if(args.length == 2) {
                return getPlayerTabComplete(args[1]);
            }
        }
        return Collections.emptyList();
    }
}
