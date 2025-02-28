package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.database.cache.model.BroadcastType;
import de.netzkronehd.hitboxutils.database.cache.packet.broadcast.BroadcastPacket;
import de.netzkronehd.hitboxutils.sound.GlobalSound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.netzkronehd.hitboxutils.bungee.manager.BroadcastManager.GLOBAL_SERVER;


public class BroadcastCommand extends HitBoxCommand {

    public BroadcastCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "broadcast", "bc");
        for (BroadcastType type : BroadcastType.values()) {
            subcommands.add(type.name().toLowerCase());
        }
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if (!hasCommandPermission(hp)) return;
        if (args.length <= 2) {
            sendHelp(hp);
            return;
        }
        final String server;
        if(args[1].equalsIgnoreCase(GLOBAL_SERVER)) {
            server = GLOBAL_SERVER;
        } else server = args[1];

        BroadcastType.of(args[0].toUpperCase()).ifPresentOrElse(
                broadcastType -> hitBoxUtils.getBroadcastManager().sendBroadcast(new BroadcastPacket(hitBoxUtils.getRedisManager().getServerName(), server, getArgsAsText(args, 2), broadcastType, GlobalSound.UI_TOAST_CHALLENGE_COMPLETE)),
                () -> sendHelp(hp)
        );
    }

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if (hasPermission(hp)) {
            if (args.length == 1) {
                return getSubCommandTab(args[0].toLowerCase());
            } else if (args.length == 2) {
                final List<String> tabs = new ArrayList<>(hitBoxUtils.getProxy().getServers().size()+1);
                args[1] = args[1].toLowerCase();
                if(GLOBAL_SERVER.toLowerCase().startsWith(args[1])) tabs.add(GLOBAL_SERVER);
                hitBoxUtils.getProxy().getServers().values().forEach(serverInfo -> {
                    if(!serverInfo.getName().toLowerCase().startsWith(args[1])) return;
                    tabs.add(serverInfo.getName());
                });
                return tabs;
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("broadcast§8 <§eType§8>§8 <§eServer/global§8> <§eMessage§8>");
    }
}
