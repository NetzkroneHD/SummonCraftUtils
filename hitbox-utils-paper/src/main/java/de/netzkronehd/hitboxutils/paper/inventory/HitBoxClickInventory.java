package de.netzkronehd.hitboxutils.paper.inventory;

import de.netzkronehd.hitboxutils.paper.manager.InventoryManager;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public abstract class HitBoxClickInventory extends HitBoxInventory {

    public HitBoxClickInventory(String name, Inventory inventory) {
        super(name, inventory);
        InventoryManager.INVENTORIES.put(name, this);
    }

    public HitBoxClickInventory(String name, InventoryType type) {
        super(name, type);
        InventoryManager.INVENTORIES.put(name, this);
    }

    public HitBoxClickInventory(String name, int size) {
        super(name, size);
        InventoryManager.INVENTORIES.put(name, this);
    }

    public boolean isCancelInRealInv() {
        return true;
    }

    public boolean isAutoCancel() {
        return true;
    }

    public abstract void onClick(InventoryClickEvent e, HitBoxPlayer hp);

    public abstract void onClose(InventoryCloseEvent e, HitBoxPlayer hp);

}
