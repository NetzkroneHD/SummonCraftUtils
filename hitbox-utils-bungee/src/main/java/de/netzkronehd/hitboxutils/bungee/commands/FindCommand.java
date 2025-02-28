package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.message.Messages;

import java.util.Collections;
import java.util.List;

import static de.netzkronehd.hitboxutils.message.MessageBuilder.builder;
import static de.netzkronehd.hitboxutils.message.MessageBuilder.prefix;
import static net.md_5.bungee.api.ChatColor.YELLOW;

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

        hp.sendMessage(prefix()
                .append(ht.getPrefixAndName(), true)
                .append("§7 is currently online on§e ")
                .append(builder("§e"+ht.getServerName())
                        .color(YELLOW)
                        .runCommand("/join " + ht.getName())
                        .showText("§7Join on the server§e " + ht.getServerName() + "§7.")
                )
                .append("§7.")
                .build()
        );

    }

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if (hasPermission(hp) && args.length == 1) {
            return getPlayerTabComplete(args[0]);
        }

        return Collections.emptyList();
    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("find§8 <§ePlayer§8>");
    }

}
