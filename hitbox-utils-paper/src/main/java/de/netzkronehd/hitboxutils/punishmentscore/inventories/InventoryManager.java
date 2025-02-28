package de.netzkronehd.hitboxutils.punishmentscore.inventories;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryManager {
    private final Map<String, YamlConfiguration> inventoryConfiguration;
    private final Map<Player, PunishmentInventory> openedMenus = new HashMap();
    private final List<Player> skipClose = new ArrayList();

    public InventoryManager(Map<String, YamlConfiguration> inventoryConfiguration) {
        this.inventoryConfiguration = inventoryConfiguration;
    }

    public void openInventory(Player p, PunishmentInventory invs) {
        this.openedMenus.put(p, invs);
    }

    public void closeInventory(Player p) {
        if (this.skipClose.contains(p)) {
            this.skipClose.remove(p);
        } else {
            this.openedMenus.remove(p);
        }
    }

    public boolean hasInventory(Player p) {
        return this.openedMenus.containsKey(p);
    }

    public PunishmentInventory getCurrentInventory(Player p) {
        return this.openedMenus.get(p);
    }

    public boolean isMenuOpen(String name) {
        for (PunishmentInventory pi : this.openedMenus.values()) {
            if (pi.getPunishedPlayer().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void closeAllInventories() {
        for (Player p : this.openedMenus.keySet()) {
            p.closeInventory();
        }
        this.skipClose.clear();
        this.openedMenus.clear();
    }

    public YamlConfiguration getInventoryConfiguration(String name) {
        return this.inventoryConfiguration.getOrDefault(name, new YamlConfiguration());
    }

    public void skipCloseAdd(Player p) {
        this.skipClose.add(p);
    }
}
