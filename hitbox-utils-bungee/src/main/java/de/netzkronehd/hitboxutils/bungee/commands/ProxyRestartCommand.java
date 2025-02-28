package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.netzkronehd.hitboxutils.utils.Constants.PERMISSION_PREFIX;

@SuppressWarnings("DuplicatedCode")
public class ProxyRestartCommand extends HitBoxCommand {

    public ProxyRestartCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "proxyrestart");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if (!hasCommandPermission(hp)) return;
        if (args.length != 1) {
            hp.sendUsage("proxyrestart§8 <§eTimeInSeconds§8>");
            return;
        }

        if (args[0].equalsIgnoreCase("cancel")) {
            if (!hitBoxUtils.getRestartManager().isRunning()) {
                hp.sendMessage("There is no countdown running.");
                return;
            }
            hitBoxUtils.getRestartManager().cancel();
            return;
        }

        try {
            int seconds = Integer.parseInt(args[0]);
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

            hitBoxUtils.getRestartManager().startCountdown(seconds);


        } catch (NumberFormatException ex) {
            hp.sendMessage("Please use an integer.");
        }

    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION_PREFIX+"proxyrestart")) return;
        if (args.length != 1) {
            sender.sendMessage(new TextComponent("proxyrestart§8 <§eTimeInSeconds§8>"));
            return;
        }

        if (args[0].equalsIgnoreCase("cancel")) {
            if (!hitBoxUtils.getRestartManager().isRunning()) {
                sender.sendMessage(new TextComponent("There is no countdown running."));
                return;
            }
            hitBoxUtils.getRestartManager().cancel();
            return;
        }

        try {
            int seconds = Integer.parseInt(args[0]);
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
            this.hitBoxUtils.getRestartManager().startCountdown(seconds);

        } catch (NumberFormatException ex) {
            sender.sendMessage(new TextComponent("Please use an integer."));
        }
    }

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if (hasPermission(hp)) {
            if (args.length == 1) {
                args[0] = args[0].toLowerCase();
                final List<String> tabs = new ArrayList<>();

                if ("cancel".startsWith(args[0])) tabs.add("cancel");
                if ("120".startsWith(args[0])) tabs.add("120");
                if ("60".startsWith(args[0])) tabs.add("60");
                if ("30".startsWith(args[0])) tabs.add("30");
                if ("15".startsWith(args[0])) tabs.add("15");

                return tabs;
            }
        }
        return Collections.emptyList();
    }

}
