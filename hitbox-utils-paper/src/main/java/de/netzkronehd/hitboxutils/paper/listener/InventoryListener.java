package de.netzkronehd.hitboxutils.paper.listener;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.inventory.HitBoxClickInventory;
import de.netzkronehd.hitboxutils.paper.inventory.PlayerClickInventory;
import de.netzkronehd.hitboxutils.paper.manager.InventoryManager;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

@RequiredArgsConstructor
public class InventoryListener implements Listener {

    private final HitBoxUtils hitBoxUtils;

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof final Player p)) return;
        final HitBoxPlayer ep = hitBoxUtils.getPlayer(p.getUniqueId());
        if (ep == null) return;

        final PlayerClickInventory playerClickInventory = ep.getInventories().get(e.getView().getTitle());
        if (playerClickInventory != null) {
            if (playerClickInventory.isCancelInRealInv()) {
                if (e.getRawSlot() < e.getInventory().getSize()) {
                    if (playerClickInventory.isAutoCancel()) e.setCancelled(true);
                }
            } else {
                if (playerClickInventory.isAutoCancel()) e.setCancelled(true);
            }
            playerClickInventory.onClick(e, ep);
            return;
        }


        final HitBoxClickInventory clickInventory = InventoryManager.INVENTORIES.get(e.getView().getTitle());
        if (clickInventory == null) return;
        if (clickInventory.isCancelInRealInv()) {
            if (e.getRawSlot() < e.getInventory().getSize()) {
                if (clickInventory.isAutoCancel()) e.setCancelled(true);
            }
        } else {
            if (clickInventory.isAutoCancel()) e.setCancelled(true);
        }
        clickInventory.onClick(e, ep);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof final Player p)) return;

        final HitBoxPlayer ep = hitBoxUtils.getPlayer(p.getUniqueId());
        if (ep == null) return;

        final PlayerClickInventory playerClickInventory = ep.getInventories().get(e.getView().getTitle());
        if (playerClickInventory != null) {
            playerClickInventory.onClose(e, ep);
            return;
        }

        final HitBoxClickInventory clickInventory = InventoryManager.INVENTORIES.get(e.getView().getTitle());
        if (clickInventory == null) return;
        clickInventory.onClose(e, ep);

    }

}
