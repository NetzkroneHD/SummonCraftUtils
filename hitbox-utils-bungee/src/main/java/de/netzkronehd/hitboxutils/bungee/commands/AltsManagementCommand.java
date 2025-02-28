package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.message.HexColor;
import de.netzkronehd.hitboxutils.message.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;

public class AltsManagementCommand extends HitBoxCommand {

    public AltsManagementCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "altsmanager", "altmanager");
        subcommands.add("kick");
        subcommands.add("enable");
        subcommands.add("disable");
        subcommands.add("find");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if (!hasCommandPermission(hp)) return;
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("kick")) {
                final TextComponent reason;
                if (args.length > 1) {
                    reason = HexColor.translateHexCodesInComponents(getArgsAsText(args, args.length));
                } else reason = new TextComponent("");

                final Map<HitBoxPlayer, Set<HitBoxPlayer>> altAccounts = hitBoxUtils.getAltsManager().getAltAccounts();
                int kicked = 0;
                for (Map.Entry<HitBoxPlayer, Set<HitBoxPlayer>> entry : altAccounts.entrySet()) {
                    if (entry.getValue().isEmpty()) continue;
                    if (!entry.getKey().getUniqueId().equals(hp.getUniqueId())) {
                        hp.sendMessage(getKickedMessage(entry.getKey()));
                        entry.getKey().getPlayer().disconnect(reason);
                        kicked++;
                    }
                    for (HitBoxPlayer hitBoxPlayer : entry.getValue()) {
                        if (hitBoxPlayer.getUniqueId().equals(hp.getUniqueId())) continue;
                        hp.sendMessage(getKickedMessage(hitBoxPlayer));
                        hitBoxPlayer.getPlayer().disconnect(reason);
                        kicked++;
                    }
                }
                hp.sendMessage("§e" + kicked + "§7 Players have been kicked.");

            } else if (args[0].equalsIgnoreCase("enable")) {
                hitBoxUtils.getAltsManager().setAllowed(true);
                hp.sendMessage("Players are now§a allowed§7 join with Alt-Accounts.");

            } else if (args[0].equalsIgnoreCase("disable")) {
                hitBoxUtils.getAltsManager().setAllowed(false);
                hp.sendMessage("Players are now§c not allowed§7 join with Alt-Accounts.");

            } else if (args[0].equalsIgnoreCase("find")) {
                final Map<HitBoxPlayer, Set<HitBoxPlayer>> altAccounts = hitBoxUtils.getAltsManager().getAltAccounts();
                int alts = 0;

                hp.sendLine();
                for (Map.Entry<HitBoxPlayer, Set<HitBoxPlayer>> entry : altAccounts.entrySet()) {
                    if (entry.getValue().isEmpty()) continue;

                    Component playerComponent = text("§8" + Messages.ARROW_RIGHT + "§7 " + entry.getKey().getName())
                            .append(text("§8:§7 "));
                    for (HitBoxPlayer altPlayer : entry.getValue()) {
                        playerComponent = text("§7" + altPlayer.getName() + "§8, ")
                                .color(NamedTextColor.GRAY)
                                .hoverEvent(text("§7Join§e " + altPlayer.getName()))
                                .clickEvent(runCommand("/join " + altPlayer.getName()));
                        alts++;
                    }
                    hp.sendMessage(playerComponent);
                }
                hp.sendLine();
                hp.sendMessage("§e" + alts + "§7 Alt-Accounts have been found§7.");

            } else sendHelp(hp);
        } else sendHelp(hp);

    }

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if (hasPermission(hp)) {
            if (args.length == 1) {
                return getSubCommandTab(args[0].toLowerCase());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("altmanager find");
        hp.sendUsage("altmanager enable§8/§edisable");
        hp.sendUsage("altmanager kick§8 <§eReason§8>");


    }

    private TextComponent getKickedMessage(HitBoxPlayer hp) {
        final TextComponent tc = new TextComponent(Messages.PREFIX.toString());
        tc.addExtra(HexColor.translateHexCodesInComponents(hp.getPrefixAndName()));
        tc.addExtra("§7 has been kicked.");
        return tc;
    }

}
