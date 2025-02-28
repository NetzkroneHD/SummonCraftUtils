package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.utils.Utils;
import org.javatuples.KeyValue;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class LastJoinCommand extends HitBoxCommand {

    public LastJoinCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "lastjoin");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if(!hasCommandPermission(hp)) return;
        if (args.length != 1) {
            sendHelp(hp);
            return;
        }
        final HitBoxPlayer ht = hitBoxUtils.getPlayer(args[0]);
        if (ht != null) {
            hitBoxUtils.runAsync(() -> sendTime(hp, ht.getPrefixAndName(), hitBoxUtils.getPlayerManager().getLastJoin(ht.getUniqueId())));
        } else {
            hitBoxUtils.runAsync(() -> {
                final KeyValue<UUID, String> uuidAndName = hitBoxUtils.getPlayerManager().getUuidAndName(args[0]);
                if (uuidAndName == null) {
                    hp.sendMessage("Could not find that player in the database.");
                    return;
                }
                final Long lastJoin = hitBoxUtils.getPlayerManager().getLastJoin(uuidAndName.getKey());

                sendTime(hp, "§e"+uuidAndName.getValue(), lastJoin);

            });
        }
    }

    private void sendTime(HitBoxPlayer hp, String name, Long time) {
        hp.sendMessageColored(name+"§7 joined the last time§8:§e "+(time != null ? Utils.DATE_FORMAT.format(new Date(time)):"§cDid not join yet."));
    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("lastjoin§8 <§ePlayer§8>");
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
}
