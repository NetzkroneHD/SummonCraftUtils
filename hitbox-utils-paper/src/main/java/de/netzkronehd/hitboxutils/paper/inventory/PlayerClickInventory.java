package de.netzkronehd.hitboxutils.paper.inventory;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public abstract class PlayerClickInventory extends HitBoxInventory {

    protected final HitBoxPlayer player;
    protected final HitBoxUtils hitBoxUtils;

    public PlayerClickInventory(HitBoxPlayer player, String name, Inventory inventory) {
        super(name, inventory);
        this.player = player;
        this.hitBoxUtils = player.getHitBoxUtils();
        player.getInventories().put(name, this);
    }

    public PlayerClickInventory(HitBoxPlayer player, String name, InventoryType type) {
        super(name, type);
        this.player = player;
        this.hitBoxUtils = player.getHitBoxUtils();
        player.getInventories().put(name, this);
    }

    public PlayerClickInventory(HitBoxPlayer player, String name, int size) {
        super(name, size);
        this.player = player;
        this.hitBoxUtils = player.getHitBoxUtils();
        player.getInventories().put(name, this);
    }

    public void dispose() {
        clear();
        player.getInventories().remove(name);
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