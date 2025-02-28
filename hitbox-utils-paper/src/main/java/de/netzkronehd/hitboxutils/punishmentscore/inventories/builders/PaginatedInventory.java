package de.netzkronehd.hitboxutils.punishmentscore.inventories.builders;

import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class PaginatedInventory extends InventoryBuilder {
    protected ItemStack[][] inventories;

    protected int pages;

    private int currPage;

    public PaginatedInventory(String configuration) {
        super(configuration);
        this.currPage = 0;
    }

    protected void setPaginationItems() {
        for (int i = 0; i < this.inventories.length; i++) {
            if (i == 0) {
                int slot = this.inventoryConfiguration.getInt("nextPage.slot");
                ItemStack nextPageItem = buildPaginationItem("nextPage");
                this.inventories[i][slot] = nextPageItem;
            } else if (i == this.inventories.length - 1) {
                int slot = this.inventoryConfiguration.getInt("backPage.slot");
                ItemStack backPageItem = buildPaginationItem("backPage");
                this.inventories[i][slot] = backPageItem;
            } else {
                int slot_next = this.inventoryConfiguration.getInt("nextPage.slot");
                ItemStack nextPageItem = buildPaginationItem("nextPage");
                this.inventories[i][slot_next] = nextPageItem;
                int slot_back = this.inventoryConfiguration.getInt("backPage.slot");
                ItemStack backPageItem = buildPaginationItem("backPage");
                this.inventories[i][slot_back] = backPageItem;
            }
        }
    }

    private ItemStack buildPaginationItem(String action) {
        String displayName = this.inventoryConfiguration.getString(action + ".item.displayName");
        String itemName = this.inventoryConfiguration.getString(action + ".item.material");
        Material m = Material.matchMaterial(itemName);
        int durability = this.inventoryConfiguration.getInt(action + ".item.damage", 0);
        List<String> lore = this.inventoryConfiguration.getStringList(action + ".item.lore");
        ItemStack is = getItem(m, durability, displayName, lore);
        NBT.modify(is, nbti -> {
            nbti.setString("key", action);
            nbti.setBoolean("isConfirmation", Boolean.valueOf(false));
        });
        return is;
    }

    public ItemStack[] getNextPage() {
        if (this.currPage + 1 < this.inventories.length)
            this.currPage++;
        int page = this.currPage;
        return this.inventories[page];
    }

    public ItemStack[] getBackPage() {
        if (this.currPage - 1 >= 0)
            this.currPage--;
        int page = this.currPage;
        return this.inventories[page];
    }

    public Inventory getFirstInventory() {
        assert this.inventories != null;
        assert this.inventoryName != null;
        Inventory inv = Bukkit.createInventory(null, (this.inventories[0]).length, this.inventoryName);
        inv.setContents(this.inventories[0]);
        return inv;
    }
}
