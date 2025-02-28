package de.netzkronehd.hitboxutils.paper.listener;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

@RequiredArgsConstructor
public class MineDetectionListener implements Listener {

    private final HitBoxUtils hitBoxUtils;


    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent e) {
        if(e.isCancelled()) return;
        if(!hitBoxUtils.getMineDetectionManager().isEnabled()) return;

        final HitBoxPlayer hp = hitBoxUtils.getPlayer(e.getPlayer().getUniqueId());
        if(hp == null) return;
        if(hp.isStaff() || hp.hasPermission("minedetection.bypass")) return;
        hitBoxUtils.getMineDetectionManager().addDetection(hp, e.getBlock().getType());

    }

}
