package de.netzkronehd.hitboxutils.paper.command;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerRestartCommand extends HitBoxCommand {


    public ServerRestartCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "serverrestart");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if (!hasCommandPermission(hp)) return;
        if (args.length != 1) {
            hp.sendUsage("serverrestart§8 <§eTimeInSeconds§8>");
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

            hitBoxUtils.getRestartManager().startCountdown(seconds, true);


        } catch (NumberFormatException ex) {
            hp.sendMessage("Please use an integer.");
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
