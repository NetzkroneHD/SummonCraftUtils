package de.netzkronehd.hitboxutils.paper.inventory;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class HitBoxInventory {

    protected final String name;
    protected final Inventory inventory;

    public HitBoxInventory(String name, Inventory inventory) {
        this.name = name;
        this.inventory = inventory;
    }

    public HitBoxInventory(String name, InventoryType type) {
        this.name = name;
        this.inventory = Bukkit.createInventory(null, type);
    }

    @SuppressWarnings("deprecation")
    public HitBoxInventory(String name, int size) {
        this.name = name;
        this.inventory = Bukkit.createInventory(null, size, name);
    }

    public String getName() {
        return name;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getSize() {
        return inventory.getSize();
    }

    public void clear() {
        inventory.clear();
    }

    public ItemStack getItem(int slot) {
        return inventory.getItem(slot);
    }

    public void addItem(ItemStack... item) {
        inventory.addItem(item);
    }

    public void setItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }

    public void setContent(ItemStack... items) {
        inventory.setContents(items);
    }

    public void fill(ItemStack item) {
        for (int i = 0; i < inventory.getSize(); i++) setItem(i, item);
    }

    public void fillBorders(ItemStack item) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (i <= 8 || i >= inventory.getSize() - 1 - 9 && i <= inventory.getSize() - 1 || i % 9 == 0) {
                if (i % 9 == 0) {
                    inventory.setItem(i, item);
                    if ((i + 8) < inventory.getSize()) {
                        inventory.setItem(i + 8, item);
                    }
                } else {
                    inventory.setItem(i, item);
                }

            }
        }
    }

    public void fillLine(ItemStack itemStack, int... lines) {
        for (int l : lines) {
            for (int i = l * 9; i < l * 9 + 9; i++) {
                setItem(i, itemStack);
            }
        }
    }


}
