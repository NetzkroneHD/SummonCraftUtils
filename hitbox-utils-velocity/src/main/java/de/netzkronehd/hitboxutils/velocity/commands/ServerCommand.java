package de.netzkronehd.hitboxutils.velocity.commands;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;

import java.util.Collections;
import java.util.List;

import static de.netzkronehd.hitboxutils.velocity.message.MessageColor.formatColoredValue;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.event.HoverEvent.showText;

public class ServerCommand extends HitBoxCommand {

    public ServerCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "server");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if (args.length == 0) {
            final ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.empty().toBuilder();
            builder.append(formatColoredValue(Messages.PREFIX+"§7Server§8:§e "));

            for (RegisteredServer registeredServer : hitBoxUtils.getProxyServer().getAllServers()) {
                builder.append(formatColoredValue(registeredServer.getServerInfo().getName()+"§8, ")
                        .clickEvent(runCommand("/server "+registeredServer.getServerInfo().getName()))
                        .hoverEvent(showText(formatColoredValue("§7Join the server§e "+registeredServer.getServerInfo().getName()))));
            }
            hp.sendMessage(builder.build());
        } else if (args.length == 1) {
            hitBoxUtils.getProxyServer().getServer(args[0]).ifPresentOrElse(
                    registeredServer -> {
                        hp.sendMessage("Connecting to§e "+registeredServer.getServerInfo().getName()+"§7...");
                        hp.getPlayer().createConnectionRequest(registeredServer);
                    },
                    () -> hp.sendMessage("That server does not exists."));
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
            return hitBoxUtils.getProxyServer().getAllServers().stream()
                    .map(registeredServer -> registeredServer.getServerInfo().getName())
                    .filter(name -> name.toLowerCase().startsWith(args[0]))
                    .toList();
        }
        return Collections.emptyList();
    }
}
