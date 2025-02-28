package de.netzkronehd.hitboxutils.paper.listener;

import de.netzkronehd.hitboxutils.database.cache.model.FilterResultModel;
import de.netzkronehd.hitboxutils.database.cache.packet.filterbroadcast.FilterBroadcastPacket;
import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.paper.utils.SpigotUtils;
import de.netzkronehd.hitboxutils.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@RequiredArgsConstructor
public class ChatListener implements Listener {

    private final HitBoxUtils hitBoxUtils;


    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(e.getPlayer());
        if (hp == null) return;

        if (hitBoxUtils.getServerMuteManager().isEnabled()) {
            if (!hasBypass(hp)) {
                e.setCancelled(true);
            }
            hp.sendMessage(hitBoxUtils.getServerMuteManager().getBlockMessage());
            return;
        }
        if (hp.isStaff()) return;
        if (!Utils.isOver(hp.getLastMessageTime())) {
            e.setCancelled(true);
            hp.sendMessage("Por favor, espera un momento.");
            return;
        }
        final long time = System.currentTimeMillis();
        hp.setLastMessageTime(time + hitBoxUtils.getChatFilterManager().getMessageTimeout());

        final FilterResultModel filterResultModel = hitBoxUtils.getChatFilterManager().filterMessage(e.getMessage(), hp.getLastMessage());
        if (filterResultModel != null) {
            if (filterResultModel.toSimilar()) {
                e.setCancelled(true);
                hp.sendMessage("Por favor, no escribas el mismo mensaje.");
                return;
            }

            if (filterResultModel.exceedsUpperCaseLimit()) {
                e.setCancelled(true);
                hp.sendMessage("Por favor, desactiva el bloqueo de mayúsculas (BLOQ MAYUS).");
                return;
            }
            if (filterResultModel.isBanned()) {
                hitBoxUtils.getRedisManager().sendPacket(new FilterBroadcastPacket(
                        hitBoxUtils.getRedisManager().getServerName(),
                        SpigotUtils.mapUserModel(hp),
                        filterResultModel
                ));

                if (hitBoxUtils.getChatFilterManager().isBlockMessages()) {
                    e.setCancelled(true);
                    hp.sendMessage(hitBoxUtils.getChatFilterManager().getBlockMessage().replace("%WORD%", filterResultModel.bannedWord()));
                    return;
                }
            }
        }
        hp.setLastMessage(e.getMessage());
    }

    private boolean hasBypass(HitBoxPlayer hp) {
        return hp.isTeamChatAllowed() || hp.hasPermission("servermute.bypass");
    }

}
