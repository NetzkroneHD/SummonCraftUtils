package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.message.Messages;

import java.util.Collections;
import java.util.List;

public class JoinCommand extends HitBoxCommand {

    public JoinCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "join", "hbjoin");
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

        if (ht.getServerName().equalsIgnoreCase(hp.getServerName())) {
            hp.sendMessage("You are already connected to§e " + hp.getServerName() + "§7.");
            return;
        }

        hp.sendMessage("Connecting to§e " + ht.getServerName() + "§7...");
        hp.getPlayer().connect(ht.getPlayer().getServer().getInfo());
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
        hp.sendUsage("join§8 <§ePlayer§8>");
    }
}
