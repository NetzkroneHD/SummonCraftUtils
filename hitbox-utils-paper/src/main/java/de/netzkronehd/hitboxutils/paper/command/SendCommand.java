package de.netzkronehd.hitboxutils.paper.command;

import de.netzkronehd.hitboxutils.database.cache.packet.server.ConnectPacket;
import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;

public class SendCommand extends HitBoxCommand {

    public SendCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "sendpaper", "sendp");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if(!hasCommandPermission(hp)) return;
        if(args.length < 2) {
            hp.sendUsage("sendp§8 <§eplayer§8> <§eserver§8>");
            return;
        }
        if(args[0].equalsIgnoreCase("current")) {
            final String server = args[1];
            hitBoxUtils.getPlayers().forEach(player -> {
                hp.sendMessage("§7Sending §e" + player.getPrefixAndName() + " §7to §e" + server + "§7...");
                hitBoxUtils.getRedisManager().sendPacket(new ConnectPacket(hitBoxUtils.getRedisManager().getServerName(), player.getUniqueId(), server));
            });
            return;
        }
        final HitBoxPlayer target = hitBoxUtils.getPlayer(args[0]);
        if(target == null) {
            hp.sendOffline();
            return;
        }
        final String server = args[1];
        hp.sendMessage("§7Sending §e"+target.getPrefixAndName()+" §7to §e"+server+"§7...");
        hitBoxUtils.getRedisManager().sendPacket(new ConnectPacket(hitBoxUtils.getRedisManager().getServerName(), target.getUniqueId(), server));
    }
}
