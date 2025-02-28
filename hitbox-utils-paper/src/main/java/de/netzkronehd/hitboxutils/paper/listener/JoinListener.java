package de.netzkronehd.hitboxutils.paper.listener;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class JoinListener implements Listener {

    private final HitBoxUtils hitBoxUtils;

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent e) {
        hitBoxUtils.getPlayerCache().remove(e.getPlayer().getUniqueId());
        final HitBoxPlayer hp = new HitBoxPlayer(hitBoxUtils, e.getPlayer());
        hitBoxUtils.getPlayerCache().put(e.getPlayer().getUniqueId(), hp);

        hitBoxUtils.runAsync(hp::loadData);

        hitBoxUtils.getPlayerListInventoryManager().updatePlayerList();

        final Long time = hitBoxUtils.getPlayerListInventoryManager().getFrozenPlayers().get(hp.getUniqueId());
        if (!Utils.isOver(time)) {
            hp.freeze(null);
        }
        hitBoxUtils.getPlayerManager().updatePlayerCount();

        if (hp.isStaff()) {
            return;
        }

        for (HitBoxPlayer vanished : hitBoxUtils.getVanishManager().getVanishedPlayers().values()) {
            hp.getPlayer().hidePlayer(hitBoxUtils, vanished.getPlayer());
        }


    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(e.getPlayer());
        if (hp == null) return;
        if (hp.isSpectating()) {
            hp.disableSpectatorMode();
        }
        if (hp.isFrozen()) {
            hitBoxUtils.getPlayerListInventoryManager().getFrozenPlayers().put(hp.getUniqueId(), System.currentTimeMillis()+TimeUnit.MINUTES.toMillis(5));
        }

        hitBoxUtils.getPlayerCache().remove(e.getPlayer().getUniqueId());
        hitBoxUtils.getPlayerListInventoryManager().updatePlayerList();

        if (hp.getStaffSettings() != null && hp.isStaff()) {
            hitBoxUtils.runAsync(() -> hitBoxUtils.getStaffSettingManager().saveSettings(hp.getStaffSettings()));
        }
        hitBoxUtils.getPlayerManager().updatePlayerCount();

        hitBoxUtils.getMineDetectionManager().removeDetection(hp.getUniqueId());

    }

}
