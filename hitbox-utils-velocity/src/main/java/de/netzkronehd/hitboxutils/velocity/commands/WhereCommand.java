package de.netzkronehd.hitboxutils.velocity.commands;


import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;

public class WhereCommand extends HitBoxCommand {

    public WhereCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "whereami");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        hp.sendMessage("Proxy§8:§e "+hitBoxUtils.getRedisManager().getConfig().getServerName());
        hp.sendMessage("Server§8:§e "+hp.getServerName());
    }
}
