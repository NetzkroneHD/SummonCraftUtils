package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;

public class WhereCommand extends HitBoxCommand {

    public WhereCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "whereami");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        hp.sendMessage("Proxy§8:§e "+hitBoxUtils.getRedisManager().getServerName());
        hp.sendMessage("Server§8:§e "+hp.getServerName());
    }
}
