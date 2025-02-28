package de.netzkronehd.hitboxutils.paper.listener;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

@AllArgsConstructor
public class FreezeListener implements Listener {

    private final HitBoxUtils hitBoxUtils;

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(e.getPlayer());
        if(hp == null) return;
        if(!hp.isFrozen()) return;

        e.setCancelled(true);
        hp.sendMessage("No puedes usar ningún comando mientras estás congelado.");

    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        final HitBoxPlayer ep = hitBoxUtils.getPlayer(e.getPlayer().getUniqueId());
        if(ep == null) return;
        if(!ep.isFrozen()) return;

        e.setTo(e.getFrom());
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if(!(e.getDamager() instanceof final Player p)) return;

        final HitBoxPlayer ep = hitBoxUtils.getPlayer(p.getUniqueId());
        if(ep == null) return;
        if(!ep.isFrozen()) return;

        e.setCancelled(true);
    }

     @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(!(e.getWhoClicked() instanceof final Player p)) return;
        final HitBoxPlayer ep = hitBoxUtils.getPlayer(p.getUniqueId());
        if (ep == null) return;
        if(!ep.isFrozen()) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        final HitBoxPlayer ep = hitBoxUtils.getPlayer(e.getPlayer().getUniqueId());
        if(ep == null) return;
        if(!ep.isFrozen()) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        final HitBoxPlayer ep = hitBoxUtils.getPlayer(e.getPlayer().getUniqueId());
        if(ep == null) return;
        if(!ep.isFrozen()) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        final HitBoxPlayer ep = hitBoxUtils.getPlayer(e.getPlayer().getUniqueId());
        if(ep == null) return;
        if(!ep.isFrozen()) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        final HitBoxPlayer ep = hitBoxUtils.getPlayer(e.getPlayer().getUniqueId());
        if(ep == null) return;
        if(!ep.isFrozen()) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e) {
        final HitBoxPlayer ep = hitBoxUtils.getPlayer(e.getPlayer().getUniqueId());
        if(ep == null) return;
        if(!ep.isFrozen()) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onHeld(PlayerItemHeldEvent e) {
        final HitBoxPlayer ep = hitBoxUtils.getPlayer(e.getPlayer().getUniqueId());
        if(ep == null) return;
        if(!ep.isFrozen()) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onPickUp(EntityPickupItemEvent e) {
        if(!(e.getEntity() instanceof final Player p)) return;
        final HitBoxPlayer ep = hitBoxUtils.getPlayer(p.getUniqueId());
        if(ep == null) return;
        if(!ep.isFrozen()) return;

        e.setCancelled(true);
    }

}
