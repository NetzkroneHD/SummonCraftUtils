package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.message.MessageBuilder;
import de.netzkronehd.hitboxutils.message.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;

import java.util.Collections;
import java.util.List;

import static de.netzkronehd.hitboxutils.message.MessageBuilder.*;

public class ServerCommand extends HitBoxCommand {

    public ServerCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "server");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if (args.length == 0) {
            final MessageBuilder servers = builder(Messages.PREFIX+"§7Server§8:§e ");
            for (ServerInfo info : hitBoxUtils.getProxy().getServers().values()) {
                servers.append(runCommandAndShowText(info.getName()+"§8, ", "/server "+info.getName(), "§7Join the server§e "+info.getName()).color(ChatColor.YELLOW));
            }
            servers.append("\n").append(prefix().append("§7You are currently on§e "+hp.getServerName()+"§7."));
            hp.sendMessage(servers.build());
        } else if (args.length == 1) {
            final ServerInfo serverInfo = hitBoxUtils.getProxy().getServerInfo(args[0]);
            if (serverInfo == null) {
                hp.sendMessage("That server does not exists.");
                return;
            }
            if (hp.getServerName().equalsIgnoreCase(serverInfo.getName())) {
                hp.sendMessage("You are already connected to§e " + hp.getServerName() + "§7.");
                return;
            }
            hp.sendMessage("Connecting to§e "+serverInfo.getName()+"§7...");
            hp.getPlayer().connect(serverInfo, ServerConnectEvent.Reason.COMMAND);

        } else sendHelp(hp);
    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("server §8<§eName§8>");
    }

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if (args.length == 1) {
            args[0] = args[0].toLowerCase();
            return hitBoxUtils.getProxy().getServers().values().stream()
                    .map(ServerInfo::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0]))
                    .toList();
        }
        return Collections.emptyList();
    }
}
