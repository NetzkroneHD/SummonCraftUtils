package de.netzkronehd.hitboxutils.velocity.commands;

import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static de.netzkronehd.hitboxutils.velocity.message.MessageColor.formatColoredValue;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.event.HoverEvent.showText;

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
                final Component reason;
                if (args.length > 1) {
                    reason = formatColoredValue(getArgsAsText(args, args.length));
                } else reason = Component.text("");

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
                hitBoxUtils.getAltsManager().getConfig().setAllowed(true);
                hp.sendMessage("Players are now§a allowed§7 join with Alt-Accounts.");

            } else if (args[0].equalsIgnoreCase("disable")) {
                hitBoxUtils.getAltsManager().getConfig().setAllowed(false);
                hp.sendMessage("Players are now§c not allowed§7 join with Alt-Accounts.");

            } else if (args[0].equalsIgnoreCase("find")) {
                final Map<HitBoxPlayer, Set<HitBoxPlayer>> altAccounts = hitBoxUtils.getAltsManager().getAltAccounts();
                int alts = 0;

                hp.sendLine();
                for (Map.Entry<HitBoxPlayer, Set<HitBoxPlayer>> entry : altAccounts.entrySet()) {
                    if (entry.getValue().isEmpty()) continue;
                    final ComponentBuilder<TextComponent, TextComponent.Builder> componentBuilder = Component.empty().toBuilder();
                    componentBuilder.append(formatColoredValue("§8"+Messages.ARROW_RIGHT+"§7 "+entry.getKey().getName()+"§8:§7 "));
                    for (HitBoxPlayer altPlayer : entry.getValue()) {
                        final Component altComponent = formatColoredValue("§7" + altPlayer.getName() + "§8, ")
                                .color(TextColor.color(Color.GRAY.getRed(), Color.GRAY.getGreen(), Color.GRAY.getBlue()))
                                .hoverEvent(showText(formatColoredValue("§7Join§e " + altPlayer.getName())))
                                .clickEvent(runCommand("/join " + altPlayer.getName()));
                        componentBuilder.append(altComponent);
                        alts++;
                    }
                    hp.sendMessage(componentBuilder.build());
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

    private Component getKickedMessage(HitBoxPlayer hp) {
        return formatColoredValue(Messages.PREFIX+hp.getPrefixAndName()+"§7 has been kicked.");
    }

}
