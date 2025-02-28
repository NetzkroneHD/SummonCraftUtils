package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.database.cache.packet.proxy.FindProxyPlayerPacket;

import java.util.Collections;
import java.util.List;

public class FindProxyCommand extends HitBoxCommand {

    public FindProxyCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "findproxy");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if(!hasCommandPermission(hp)) return;
        if (args.length != 1) {
            hp.sendUsage("findproxy§8 <§ePlayer§8>");
            return;
        }

        final HitBoxPlayer ht = hitBoxUtils.getPlayer(args[0]);
        if (ht != null) {
            hp.sendMessageColored(ht.getPrefixAndName()+"§7 is online on proxy§e "+hitBoxUtils.getRedisManager().getServerName()+"§7.");
        } else {
            hp.sendMessage("Searching for the player on different proxies. If there is no response, the player cannot be found...");
            hitBoxUtils.getRedisManager().sendPacket(new FindProxyPlayerPacket(hitBoxUtils.getRedisManager().getServerName(), args[0], hp.getUniqueId()));
        }

    }

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if (hasPermission(hp)) {
            return getPlayerTabComplete(args[0]);
        }
        return Collections.emptyList();
    }
}
