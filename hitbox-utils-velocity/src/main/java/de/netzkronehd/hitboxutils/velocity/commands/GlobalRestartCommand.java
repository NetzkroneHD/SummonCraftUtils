package de.netzkronehd.hitboxutils.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.RestartCancelPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.RestartPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.proxy.ProxyRestartCancelPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.proxy.ProxyRestartPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.spigot.SpigotRestartCancelPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.spigot.SpigotRestartPacket;
import de.netzkronehd.hitboxutils.utils.Constants;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.netzkronehd.hitboxutils.velocity.message.MessageColor.formatColoredValue;

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
                hitBoxUtils.getRedisManager().sendPacket(new RestartCancelPacket(hitBoxUtils.getRedisManager().getConfig().getServerName(), true));
            } else if (args[0].equalsIgnoreCase("spigot")) {
                hitBoxUtils.getRedisManager().sendPacket(new SpigotRestartCancelPacket(hitBoxUtils.getRedisManager().getConfig().getServerName(), true));
            } else if (args[0].equalsIgnoreCase("proxy")) {
                hitBoxUtils.getRedisManager().sendPacket(new ProxyRestartCancelPacket(hitBoxUtils.getRedisManager().getConfig().getServerName(), true));
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
                hitBoxUtils.getRedisManager().sendPacket(new RestartPacket(hitBoxUtils.getRedisManager().getConfig().getServerName(), seconds));
            } else if (args[0].equalsIgnoreCase("spigot")) {
                hitBoxUtils.getRedisManager().sendPacket(new SpigotRestartPacket(hitBoxUtils.getRedisManager().getConfig().getServerName(),seconds));
            } else if (args[0].equalsIgnoreCase("proxy")) {
                hitBoxUtils.getRedisManager().sendPacket(new ProxyRestartPacket(hitBoxUtils.getRedisManager().getConfig().getServerName(),seconds));
            } else hp.sendUsage("globalrestart§8 <§espigot/proxy/all§8> <§eTimeInSeconds§8>");


        } catch (NumberFormatException ex) {
            hp.sendMessage("Please use an integer.");
        }

    }

    @Override
    public void onExecute(CommandSource sender, String[] args) {
        if (!sender.hasPermission(Constants.PERMISSION_PREFIX+"globalrestart")) return;
        if (args.length != 2) {
            sender.sendMessage(formatColoredValue("§e/globalrestart§8 <§espigot/proxy/all§8> <§eTimeInSeconds§8>"));
            return;
        }

        if (args[1].equalsIgnoreCase("cancel")) {
            if (args[0].equalsIgnoreCase("all")) {
                hitBoxUtils.getRedisManager().sendPacket(new RestartCancelPacket(hitBoxUtils.getRedisManager().getConfig().getServerName(), true));
            } else if (args[0].equalsIgnoreCase("spigot")) {
                hitBoxUtils.getRedisManager().sendPacket(new SpigotRestartCancelPacket(hitBoxUtils.getRedisManager().getConfig().getServerName(), true));
            } else if (args[0].equalsIgnoreCase("proxy")) {
                hitBoxUtils.getRedisManager().sendPacket(new ProxyRestartCancelPacket(hitBoxUtils.getRedisManager().getConfig().getServerName(), true));
            } else sender.sendMessage(formatColoredValue("§e/globalrestart§8 <§espigot/proxy/all§8> <§eTimeInSeconds/cancel§8>"));
            return;
        }

        try {
            int seconds = Integer.parseInt(args[1]);
            if (hitBoxUtils.getRestartManager().isRunning()) {
                sender.sendMessage(formatColoredValue("There is already a restart countdown."));
                return;
            }
            if (seconds <= 0) {
                sender.sendMessage(formatColoredValue("The countdown cant be 0 or smaller."));
                return;
            }
            if (seconds > 15 && seconds % 10 == 0) {
                seconds++;
            }

            if (args[0].equalsIgnoreCase("all")) {
                hitBoxUtils.getRedisManager().sendPacket(new RestartPacket(hitBoxUtils.getRedisManager().getConfig().getServerName(), seconds));
            } else if (args[0].equalsIgnoreCase("spigot")) {
                hitBoxUtils.getRedisManager().sendPacket(new SpigotRestartPacket(hitBoxUtils.getRedisManager().getConfig().getServerName(), seconds));
            } else if (args[0].equalsIgnoreCase("proxy")) {
                hitBoxUtils.getRedisManager().sendPacket(new ProxyRestartPacket(hitBoxUtils.getRedisManager().getConfig().getServerName(), seconds));
            } else sender.sendMessage(formatColoredValue("§e/globalrestart§8 <§espigot/proxy/all§8> <§eTimeInSeconds§8>"));

        } catch (NumberFormatException ex) {
            sender.sendMessage(formatColoredValue("Please use an integer."));
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
    public List<String> onTab(CommandSource sender, String[] args) {
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
