package de.netzkronehd.hitboxutils.paper.command;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import net.kyori.adventure.text.Component;

import java.util.Collections;
import java.util.List;

import static de.netzkronehd.hitboxutils.message.Placeholder.placeholder;
import static de.netzkronehd.translation.Message.formatColoredValue;

public class ServerMuteCommand extends HitBoxCommand {

    public ServerMuteCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "servermute");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if(!hasCommandPermission(hp)) return;
        if(args.length != 1) {
            sendHelp(hp);
            return;
        }
        args[0] = args[0].toLowerCase();
        if(args[0].startsWith("e")) {
            if (hitBoxUtils.getServerMuteManager().isEnabled()) {
                hp.sendMessage("The server-mute is already enabled.");
                return;
            }
            hitBoxUtils.getServerMuteManager().setEnabled(true);
            final Component msg = formatColoredValue(placeholder(hitBoxUtils.getServerMuteManager().getEnableMessage(), "PLAYER", hp.getPrefixAndNameRaw()).build());
            hitBoxUtils.getPlayers().forEach(player -> player.sendMessage(msg));
        } else if(args[0].startsWith("d")) {
            if (!hitBoxUtils.getServerMuteManager().isEnabled()) {
                hp.sendMessage("The server-mute is already disabled.");
                return;
            }
            hitBoxUtils.getServerMuteManager().setEnabled(false);
            final Component msg = formatColoredValue(placeholder(hitBoxUtils.getServerMuteManager().getDisableMessage(), "PLAYER", hp.getPrefixAndNameRaw()).build());
            hitBoxUtils.getPlayers().forEach(player -> player.sendMessage(msg));
        } else sendHelp(hp);


    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendMessage("The server-mute is§8: "+(hitBoxUtils.getServerMuteManager().isEnabled() ? "§aEnabled" : "§cDisabled"));
        hp.sendUsage("servermute enable§8/§edisable");
    }

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if(hasPermission(hp)) {
            if (args.length == 1) {
                return List.of("enable", "disable");
            }
        }
        return Collections.emptyList();
    }
}
