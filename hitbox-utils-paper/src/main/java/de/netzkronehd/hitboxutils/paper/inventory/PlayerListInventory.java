package de.netzkronehd.hitboxutils.paper.inventory;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import static de.netzkronehd.hitboxutils.paper.api.ItemBuilder.*;
import static de.netzkronehd.hitboxutils.paper.utils.Items.PlayerList.OPEN_SETTINGS;

public class PlayerListInventory extends HitBoxClickInventory {

    private final HitBoxUtils hitBoxUtils;
    private final int page;

    public PlayerListInventory(HitBoxUtils hitBoxUtils, int page) {
        super("§7Players -§e " + (page + 1), 9 * 6);
        this.hitBoxUtils = hitBoxUtils;
        this.page = page;
    }

    @Override
    public void onClick(InventoryClickEvent e, HitBoxPlayer hp) {
        if (GLASS.isSimilar(e.getCurrentItem())) return;
        if (BACKWARD.isSimilar(e.getCurrentItem())) {
            final PlayerListInventory inv = hitBoxUtils.getPlayerListInventoryManager().getPlayerListInventories().get(page - 1);
            if (inv == null) return;
            hp.openInventory(inv);
            hp.setPlayerListPage(page - 1);
        } else if (FORWARD.isSimilar(e.getCurrentItem())) {
            final PlayerListInventory inv = hitBoxUtils.getPlayerListInventoryManager().getPlayerListInventories().get(page + 1);
            if (inv == null) return;
            hp.openInventory(inv);
            hp.setPlayerListPage(page + 1);
        } else if (OPEN_SETTINGS.item().isSimilar(e.getCurrentItem())) {
            hp.openInventory(hp.getStaffSettingsInventory());
            hp.setPlayerListPage(page);
        } else {
            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName()) {
                final HitBoxPlayer tp = hitBoxUtils.getPlayerByPrefixName(e.getCurrentItem().getItemMeta().getDisplayName());
                if (tp == null) return;
                hp.setPlayerListPage(page);
                final SelectedPlayerInventory inv = hitBoxUtils.getPlayerListInventoryManager().getPlayerInventory(tp);
                hp.openInventory(inv);
            }
        }
    }

    @Override
    public void onClose(InventoryCloseEvent e, HitBoxPlayer hp) {

    }

    @Override
    public void clear() {
        for (int i = 0; i <= 35; i++) {
            inventory.setItem(i, null);
        }
    }

    public int getPage() {
        return page;
    }
}
