package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.RestartCancelPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.RestartPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.proxy.ProxyRestartCancelPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.proxy.ProxyRestartPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.spigot.SpigotRestartCancelPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.spigot.SpigotRestartPacket;
import de.netzkronehd.hitboxutils.utils.Constants;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("DuplicatedCode")
public class GlobalRestartCommand extends HitBoxCommand {

    public GlobalRestartCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "globalrestart");
        this.subcommands.addAll(Arrays.asList("spigot", "proxy", "all"));
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if (!hasCommandPermission(hp)) return;
        if (args.length != 2) {
            hp.sendUsage("globalrestart§8 <§espigot/proxy/all§8> <§eTimeInSeconds§8>");
            return;
        }

        if (args[1].equalsIgnoreCase("cancel")) {
            if (args[0].equalsIgnoreCase("all")) {
                hitBoxUtils.getRedisManager().sendPacket(new RestartCancelPacket(hitBoxUtils.getRedisManager().getServerName(), true));
            } else if (args[0].equalsIgnoreCase("spigot")) {
                hitBoxUtils.getRedisManager().sendPacket(new SpigotRestartCancelPacket(hitBoxUtils.getRedisManager().getServerName(), true));
            } else if (args[0].equalsIgnoreCase("proxy")) {
                hitBoxUtils.getRedisManager().sendPacket(new ProxyRestartCancelPacket(hitBoxUtils.getRedisManager().getServerName(), true));
            } else hp.sendUsage("globalrestart§8 <§espigot/proxy/all§8> <§eTimeInSeconds§8>");
            return;
        }

        try {
            int seconds = Integer.parseInt(args[1]);
            if (hitBoxUtils.getRestartManager().isRunning()) {
                hp.sendMessage("There is already a restart countdown.");
                return;
            }

            if (seconds <= 0) {
                hp.sendMessage("The countdown cant be 0 or smaller.");
                return;
            }

            if (seconds > 15 && seconds % 10 == 0) {
                seconds++;
            }

            if (args[0].equalsIgnoreCase("all")) {
                hitBoxUtils.getRedisManager().sendPacket(new RestartPacket(hitBoxUtils.getRedisManager().getServerName(), seconds));
            } else if (args[0].equalsIgnoreCase("spigot")) {
                hitBoxUtils.getRedisManager().sendPacket(new SpigotRestartPacket(hitBoxUtils.getRedisManager().getServerName(), seconds));
            } else if (args[0].equalsIgnoreCase("proxy")) {
                hitBoxUtils.getRedisManager().sendPacket(new ProxyRestartPacket(hitBoxUtils.getRedisManager().getServerName(), seconds));
            } else hp.sendUsage("globalrestart§8 <§espigot/proxy/all§8> <§eTimeInSeconds§8>");


        } catch (NumberFormatException ex) {
            hp.sendMessage("Please use an integer.");
        }

    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(Constants.PERMISSION_PREFIX+"globalrestart")) return;
        if (args.length != 2) {
            sender.sendMessage(new TextComponent("§e/globalrestart§8 <§espigot/proxy/all§8> <§eTimeInSeconds§8>"));
            return;
        }

        if (args[1].equalsIgnoreCase("cancel")) {
            if (args[0].equalsIgnoreCase("all")) {
                hitBoxUtils.getRedisManager().sendPacket(new RestartCancelPacket(hitBoxUtils.getRedisManager().getServerName(), true));
            } else if (args[0].equalsIgnoreCase("spigot")) {
                hitBoxUtils.getRedisManager().sendPacket(new SpigotRestartCancelPacket(hitBoxUtils.getRedisManager().getServerName(), true));
            } else if (args[0].equalsIgnoreCase("proxy")) {
                hitBoxUtils.getRedisManager().sendPacket(new ProxyRestartCancelPacket(hitBoxUtils.getRedisManager().getServerName(), true));
            } else sender.sendMessage(new TextComponent("§e/globalrestart§8 <§espigot/proxy/all§8> <§eTimeInSeconds/cancel§8>"));
            return;
        }

        try {
            int seconds = Integer.parseInt(args[1]);
            if (hitBoxUtils.getRestartManager().isRunning()) {
                sender.sendMessage(new TextComponent("There is already a restart countdown."));
                return;
            }
            if (seconds <= 0) {
                sender.sendMessage(new TextComponent("The countdown cant be 0 or smaller."));
                return;
            }
            if (seconds > 15 && seconds % 10 == 0) {
                seconds++;
            }

            if (args[0].equalsIgnoreCase("all")) {
                hitBoxUtils.getRedisManager().sendPacket(new RestartPacket(hitBoxUtils.getRedisManager().getServerName(), seconds));
            } else if (args[0].equalsIgnoreCase("spigot")) {
                hitBoxUtils.getRedisManager().sendPacket(new SpigotRestartPacket(hitBoxUtils.getRedisManager().getServerName(), seconds));
            } else if (args[0].equalsIgnoreCase("proxy")) {
                hitBoxUtils.getRedisManager().sendPacket(new ProxyRestartPacket(hitBoxUtils.getRedisManager().getServerName(), seconds));
            } else sender.sendMessage(new TextComponent("§e/globalrestart§8 <§espigot/proxy/all§8> <§eTimeInSeconds§8>"));

        } catch (NumberFormatException ex) {
            sender.sendMessage(new TextComponent("Please use an integer."));
        }
    }

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if (hasPermission(hp)) {
            if (args.length == 1) {
                args[0] = args[0].toLowerCase();
                return getSubCommandTab(args[0].toLowerCase());
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

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        if (sender.hasPermission(Constants.PERMISSION_PREFIX+"globalrestart")) {
            if (args.length == 1) {
                args[0] = args[0].toLowerCase();
                return getSubCommandTab(args[0].toLowerCase());
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
