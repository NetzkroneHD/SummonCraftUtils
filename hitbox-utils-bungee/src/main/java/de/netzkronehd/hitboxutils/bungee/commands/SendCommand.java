package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.message.Messages;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SendCommand extends HitBoxCommand {

    public SendCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "send");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if(!hasCommandPermission(hp)) return;
        if (args.length == 2) {
            final ServerInfo serverInfo = hitBoxUtils.getProxy().getServerInfo(args[1]);
            if (serverInfo == null) {
                hp.sendMessage("That server does not exists.");
                return;
            }
            if (args[0].equalsIgnoreCase("all")) {
                for (HitBoxPlayer player : hitBoxUtils.getPlayers()) {
                    player.getPlayer().connect(serverInfo);
                }
                hp.sendMessage("Sent§e "+hitBoxUtils.getPlayers().size()+"§7 players on§b "+serverInfo.getName()+"§7.");

            } else if (args[0].equalsIgnoreCase("current")) {
                int sent = 0;
                for (HitBoxPlayer player : hitBoxUtils.getPlayers().stream().filter(hitBoxPlayer -> hitBoxPlayer.getServerName().equalsIgnoreCase(serverInfo.getName())).toList()) {
                    player.getPlayer().connect(serverInfo);
                    sent++;
                }
                hp.sendMessage("Sent§e "+sent+"§7 players on§b "+serverInfo.getName()+"§7.");
            } else {
                final HitBoxPlayer ht = hitBoxUtils.getPlayer(args[0]);
                if (ht == null) {
                    hp.sendMessage(Messages.PLAYER_OFFLINE);
                    return;
                }
                ht.getPlayer().connect(serverInfo);
                hp.sendMessageColored("Sent "+ht.getPrefixAndName()+"§7 on§b "+serverInfo.getName()+"§7.");
            }
        } else sendHelp(hp);
    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("send§8 <§eall/current/player§8> <§eServer§8>");
    }

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if (hasPermission(hp)) {
            if (args.length == 1) {
                args[0] = args[0].toLowerCase();
                final List<String> tabs = new ArrayList<>(hitBoxUtils.getPlayers().size()+2);
                if ("all".startsWith(args[0])) tabs.add("all");
                if ("current".startsWith(args[0])) tabs.add("current");
                tabs.addAll(getPlayerTabComplete(args[0]));
                return tabs;
            } else if (args.length == 2) {
                args[1] = args[1].toLowerCase();
                return hitBoxUtils.getProxy().getServers().values().stream()
                        .map(ServerInfo::getName)
                        .filter(name -> name.toLowerCase().startsWith(args[1]))
                        .toList();
            }
        }
        return Collections.emptyList();
    }
}
