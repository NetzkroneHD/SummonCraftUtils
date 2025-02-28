package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.custom.CustomRestartCancelPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.custom.CustomRestartPacket;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("DuplicatedCode")
public class CustomRestartCommand extends HitBoxCommand {

    public CustomRestartCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "customrestart", "crestart");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if (!hasCommandPermission(hp)) return;
        if (args.length != 2) {
            sendHelp(hp);
            return;
        }

        if (args[1].equalsIgnoreCase("cancel")) {
            hitBoxUtils.getRedisManager().sendPacket(new CustomRestartCancelPacket(hitBoxUtils.getRedisManager().getServerName(), args[0], true));
            hp.sendMessage("The restart of§e "+args[0]+"§7 was cancelled.");
            return;
        }

        try {
            int seconds = Integer.parseInt(args[1]);
            if (seconds <= 0) {
                hp.sendMessage("The countdown cant be 0 or smaller.");
                return;
            }

            if (seconds > 15 && seconds % 10 == 0) {
                seconds++;
            }

            hitBoxUtils.getRedisManager().sendPacket(new CustomRestartPacket(hitBoxUtils.getRedisManager().getServerName(), args[0], seconds));
            hp.sendMessage("Restarting§e "+args[0]+"§7 in§e "+seconds+" Seconds§7.");

        } catch (NumberFormatException e) {
            sendHelp(hp);
        }

    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("customrestart§8 <§eServer§8> <§eTimeInSeconds§8>");
    }

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if (hasPermission(hp)) {
            if (args.length == 1) {
                args[0] = args[0].toLowerCase();
                final List<String> list = hitBoxUtils.getProxy().getServers().values().stream()
                        .map(ServerInfo::getName)
                        .filter(name -> name.toLowerCase().startsWith(args[0]))
                        .collect(Collectors.toList());

                list.add(hitBoxUtils.getRedisManager().getServerName());

                return list;
            } else if (args.length == 2) {
                args[1] = args[1].toLowerCase();
                final List<String> tabs = new ArrayList<>();

                if ("cancel".startsWith(args[1])) tabs.add("cancel");
                if ("120".startsWith(args[1])) tabs.add("120");
                if ("60".startsWith(args[1])) tabs.add("60");
                if ("30".startsWith(args[1])) tabs.add("30");
                if ("15".startsWith(args[1])) tabs.add("15");

                return tabs;
            }
        }
        return Collections.emptyList();
    }
}
