package de.netzkronehd.hitboxutils.paper.inventory;

import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class SpectateInventory extends PlayerClickInventory {

    public SpectateInventory(HitBoxPlayer player, String name, Inventory inventory) {
        super(player, name, inventory);
    }

    @Override
    public void onClick(InventoryClickEvent e, HitBoxPlayer hp) {

    }

    @Override
    public void onClose(InventoryCloseEvent e, HitBoxPlayer hp) {
        dispose();
    }
}
