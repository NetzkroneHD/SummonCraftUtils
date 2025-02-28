package de.netzkronehd.hitboxutils.punishmentscore.inventories;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface PunishmentInventory {
    void handleClick(InventoryClickEvent inventoryClickEvent);

    String getPunishedPlayer();
}
