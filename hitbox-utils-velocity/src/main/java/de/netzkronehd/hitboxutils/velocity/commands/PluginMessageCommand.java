package de.netzkronehd.hitboxutils.velocity.commands;

import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.velocity.utils.VeloUtils;

public class PluginMessageCommand extends HitBoxCommand {

    public PluginMessageCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "pluginmessage");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if(!hasCommandPermission(hp)) return;
        if (args.length <= 1) {
            hp.sendUsage("pluginmessage§8 <§eChannel§8> <§eMessage§8>");
            return;
        }
        final String text = getArgsAsText(args, 1);
        final String[] pluginMessage = new String[args.length-1];
        System.arraycopy(args, 1, pluginMessage, 0, pluginMessage.length);

        hp.getPlayer().getCurrentServer().ifPresentOrElse(serverConnection -> {
            VeloUtils.sendPluginMessage(serverConnection, () -> args[0], pluginMessage);
            hp.sendMessage("Sent Plugin message channel§8:§7 '§e"+args[0]+"§7' message§8:§7 '§e"+text+"§7'");
        }, () -> hp.sendMessage("You are currently not online on a server?"));

    }
}
