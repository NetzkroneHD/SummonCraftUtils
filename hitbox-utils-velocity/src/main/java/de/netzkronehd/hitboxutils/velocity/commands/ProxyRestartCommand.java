package de.netzkronehd.hitboxutils.velocity.commands;

import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static de.netzkronehd.hitboxutils.utils.Utils.getRemainingTimeInMinutes;
import static de.netzkronehd.hitboxutils.velocity.message.MessageColor.formatColoredValue;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.title.Title.Times.times;

public class ProxyRestartCommand extends HitBoxCommand {

    private Timer timer;
    private int countdown;

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
            if (timer == null) {
                hp.sendMessage("There is no countdown running.");
                return;
            }
            timer.cancel();
            timer = null;
            sendTitle("§a§lProxy restart was cancelled");
            return;
        }

        try {
            final int seconds = Integer.parseInt(args[0]);
            if (timer != null) {
                hp.sendMessage("There is already a restart countdown.");
                return;
            }

            if (seconds <= 0) {
                hp.sendMessage("The countdown cant be 0 or smaller.");
                return;
            }

            countdown = seconds;
            if (countdown > 15 && countdown % 10 == 0) {
                countdown++;
            }

            sendTitle("§c§lPor favor, abandone el área de PvP");

            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {

                    if (countdown > 15 && countdown % 10 == 0) {
                        sendTitle("§e&lReinicio de proxy en "+ getRemainingTimeInMinutes(countdown));
                    } else if (countdown <= 15 && countdown > 0) {
                        sendTitle("§e&lReinicio de proxy en "+ getRemainingTimeInMinutes(countdown));
                    } else if (countdown <= 0) {
                        sendTitle("§e§lReiniciando...");
                        hitBoxUtils.getProxyServer().shutdown(formatColoredValue("§eEl servidor proxy será reiniciado..."));
                        return;
                    }
                    countdown--;
                }
            }, 0, TimeUnit.SECONDS.toMillis(1));

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

    private void sendTitle(String subtile) {
        final Component text = formatColoredValue(subtile);

        final Title title = Title.title(empty(), text, times(Duration.ofSeconds(1), Duration.ofSeconds(4), Duration.ofSeconds(1)));
        hitBoxUtils.getProxyServer().showTitle(title);
        hitBoxUtils.getProxyServer().sendMessage(text);

    }

}
