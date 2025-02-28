package de.netzkronehd.hitboxutils.paper.command;


import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.manager.Manager;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminCommand extends HitBoxCommand {

    public AdminCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "adminpaper", "adminp", "hitboxpaper", "hitboxp");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if (!hasCommandPermission(hp)) return;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("rl")) {
                hp.sendMessage("Reloading Managers...");
                final long before = System.currentTimeMillis();
                hitBoxUtils.runAsync(() -> {
                    Manager.reloadManagers(
                            manager -> hp.sendMessage("Reloading§e " + manager.getClass().getSimpleName() + "§7..."), response -> {
                                if (response.success()) {
                                    hp.sendMessage("§e" + response.manager().getClass().getSimpleName() + "§7 reloaded after " + response.time() + "ms.");
                                } else {
                                    hp.sendMessage("Reloading of§e " + response.manager().getClass().getSimpleName() + "§c failed§7. (" + response.exception() + ")");
                                }
                            });
                    hp.sendMessage("Managers reloaded after§e " + (System.currentTimeMillis() - before) + "ms§7.");
                });

            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("rl")) {
            hp.sendMessage("Reloading Managers...");
            final Manager manager = Manager.getManagers().get(args[1]);
            if (manager == null) {
                hp.sendMessage("Could not find the manager§e " + args[1] + "§7.");
                return;
            }
            final long before = System.currentTimeMillis();
            hitBoxUtils.runAsync(() -> {
                try {
                    hp.sendMessage("§e" + manager.getClass().getSimpleName() + "§7 reloaded after " + (System.currentTimeMillis() - before) + "ms.");
                } catch (Exception ex) {
                    hp.sendMessage("Reloading of§e " + manager.getClass().getSimpleName() + "§c failed§7. (" + ex + ")");
                }
            });

        } else sendHelp(hp);
    }

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if (hasPermission(hp)) {
            if (args.length == 1) {
                if ("rl".startsWith(args[0].toLowerCase())) return Collections.singletonList("rl");
            } else if (args.length == 2 && args[0].equalsIgnoreCase("rl")) {
                final List<String> tabs = new ArrayList<>();
                args[1] = args[1].toLowerCase();

                for (Manager manager : Manager.getManagers().values()) {
                    final String name = manager.getClass().getSimpleName().toLowerCase();
                    if (name.startsWith(args[1])) {
                        tabs.add(name);
                    }
                }
                return tabs;
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("hitboxadmin rl");
    }
}
