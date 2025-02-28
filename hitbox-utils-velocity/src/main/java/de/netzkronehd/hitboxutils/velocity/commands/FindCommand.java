package de.netzkronehd.hitboxutils.velocity.commands;

import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Collections;
import java.util.List;

import static de.netzkronehd.hitboxutils.velocity.message.MessageColor.formatColoredValue;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.event.HoverEvent.showText;

public class FindCommand extends HitBoxCommand {

    public FindCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "find", "hbfind");
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if (!(hp.isStaff() || hasCommandPermission(hp))) return;
        if (args.length != 1) {
            sendHelp(hp);
            return;
        }

        final HitBoxPlayer ht = hitBoxUtils.getPlayer(args[0]);
        if (ht == null) {
            hp.sendMessage(Messages.PLAYER_OFFLINE);
            return;
        }
        hp.sendMessage(
                formatColoredValue(Messages.PREFIX.toString())
                        .append(formatColoredValue(ht.getPrefixAndName()))
                        .append(formatColoredValue("§7 is currently online on§e "))
                        .append(text(ht.getServerName())
                                .color(NamedTextColor.YELLOW)
                                .clickEvent(runCommand("/join "+ht.getName()))
                                .hoverEvent(showText(formatColoredValue("§7Join on the server§e "+ht.getServerName()+"§7."))))
                        .append(formatColoredValue("§7.")));

    }

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if (hasPermission(hp)) {
            if (args.length == 1) {
                return getPlayerTabComplete(args[0]);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("find§8 <§ePlayer§8>");
    }

}
