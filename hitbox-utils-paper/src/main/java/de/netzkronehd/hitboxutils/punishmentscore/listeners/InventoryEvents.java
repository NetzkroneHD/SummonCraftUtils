package de.netzkronehd.hitboxutils.punishmentscore.listeners;

import de.netzkronehd.hitboxutils.punishmentscore.PunishmentsCore;
import de.netzkronehd.hitboxutils.punishmentscore.inventories.PunishmentInventory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryEvents implements Listener {
    private final PunishmentsCore plugin;

    public InventoryEvents(PunishmentsCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!(event.getWhoClicked() instanceof final Player p)) {
            return;
        }
        if (this.plugin.getInventoryManager().hasInventory(p)) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().getType().name().contains("AIR")) {
                return;
            }
            final PunishmentInventory pi = this.plugin.getInventoryManager().getCurrentInventory(p);
            try {
                pi.handleClick(event);
            } catch (Exception e) {
                e.printStackTrace();
                String msg = this.plugin.getMessages().getString("Messages.error");
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof final Player p)) {
            return;
        }
        if (this.plugin.getInventoryManager().hasInventory(p)) {
            this.plugin.getInventoryManager().closeInventory(p);
        }
    }
}
