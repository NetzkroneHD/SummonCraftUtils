package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;

import java.util.List;

public class SupportCommand extends HitBoxCommand {

    public SupportCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "support");
    }


    /**
     * /support join
     * admin
     * /support add
     * /support remove
     * /support clear
     * /support queue
     */
    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if(args.length == 0) {
            sendHelp(hp);
            return;
        }
    }

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        return super.onTab(hp, args);
    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("support§8 <§eargs§8>");
        hp.sendArrow("§e");
    }
}
