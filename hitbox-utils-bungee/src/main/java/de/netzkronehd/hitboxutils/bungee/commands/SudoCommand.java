package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.message.Messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SudoCommand extends HitBoxCommand {

    public SudoCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "sudo", "hbsudo", "hsudo", "sudo");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if (!hasCommandPermission(hp)) return;
        if (args.length >= 3) {
            final HitBoxPlayer ht = hitBoxUtils.getPlayer(args[0]);
            if (ht == null) {
                hp.sendMessage(Messages.PLAYER_OFFLINE);
                return;
            }
            final String command = getArgsAsText(args, 2);
            if (args[1].equalsIgnoreCase("bungee")) {
                hitBoxUtils.getProxy().getPluginManager().dispatchCommand(ht.getPlayer(), command);
                hp.sendMessageColored(ht.getPrefixAndName() + "§7 executed the bungee-command§8 [§e/" + command + "§8]§7.");

            } else if (args[1].equalsIgnoreCase("spigot")) {
                try {
                    ht.getPlayer().chat("/" + command);
                    hp.sendMessageColored(ht.getPrefixAndName() + "§7 executed the spigot-command§8 [§e/" + command + "§8]§7.");
                } catch (UnsupportedOperationException ex) {
                    hp.sendMessage("Error§8:§c "+ex);
                }

            } else if (args[1].equalsIgnoreCase("chat")) {
                try {
                    ht.getPlayer().chat(command);
                    hp.sendMessageColored(ht.getPrefixAndName() + "§7 wrote the message§8 [§e" + command + "§8]§7.");
                } catch (UnsupportedOperationException ex) {
                    hp.sendMessage("Error§8:§c "+ex);
                }
            } else sendHelp(hp);
        } else sendHelp(hp);
    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("sudo§8 <§ePlayer§8> §eBungee§7/§eSpigot§7/§eChat§8 <§eCommand§8>");
    }

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if (hasPermission(hp)) {
            if (args.length == 1) {
                return getPlayerTabComplete(args[0]);
            } else if (args.length == 2) {
                final List<String> tabs = new ArrayList<>();
                args[1] = args[1].toLowerCase();
                if ("bungee".startsWith(args[1])) tabs.add("bungee");
                if ("spigot".startsWith(args[1])) tabs.add("spigot");
                if ("chat".startsWith(args[1])) tabs.add("chat");
                return tabs;
            } else if (args.length == 3 && (args[1].equalsIgnoreCase("bungee") || args[1].equalsIgnoreCase("spigot"))) {
                final List<String> tabs = new ArrayList<>();
                hitBoxUtils.getProxy().getPluginManager().getCommands().forEach(cmdEntry -> {
                    final String cmd = cmdEntry.getKey();
                    if (cmd.toLowerCase().startsWith(args[2])) {
                        tabs.add(cmd);
                    }
                });
                return tabs;
            }
        }
        return Collections.emptyList();
    }
}
