package de.netzkronehd.hitboxutils.velocity.commands;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;

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
            final RegisteredServer registeredServer = hitBoxUtils.getProxyServer().getServer(args[1]).orElse(null);
            if (registeredServer == null) {
                hp.sendMessage("That server does not exists.");
                return;
            }
            if (args[0].equalsIgnoreCase("all")) {
                for (HitBoxPlayer player : hitBoxUtils.getPlayers()) {
                    player.getPlayer().createConnectionRequest(registeredServer);
                }
                hp.sendMessage("Sent§e "+hitBoxUtils.getPlayers().size()+"§7 players on§b "+registeredServer.getServerInfo().getName()+"§7.");

            } else if (args[0].equalsIgnoreCase("current")) {
                int sent = 0;
                for (HitBoxPlayer player : hitBoxUtils.getPlayers().stream().filter(hitBoxPlayer -> hitBoxPlayer.getServerName().equalsIgnoreCase(registeredServer.getServerInfo().getName())).toList()) {
                    player.getPlayer().createConnectionRequest(registeredServer);
                    sent++;
                }
                hp.sendMessage("Sent§e "+sent+"§7 players on§b "+registeredServer.getServerInfo().getName()+"§7.");
            } else {
                final HitBoxPlayer ht = hitBoxUtils.getPlayer(args[0]);
                if (ht == null) {
                    hp.sendMessage(Messages.PLAYER_OFFLINE);
                    return;
                }
                ht.getPlayer().createConnectionRequest(registeredServer);
                hp.sendMessageColored("Sent "+ht.getPrefixAndName()+"§7 on§b "+registeredServer.getServerInfo().getName()+"§7.");
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
                return hitBoxUtils.getProxyServer().getAllServers().stream()
                        .map(registeredServer -> registeredServer.getServerInfo().getName())
                        .filter(name -> name.toLowerCase().startsWith(args[1]))
                        .toList();
            }
        }
        return Collections.emptyList();
    }
}
