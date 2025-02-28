package de.netzkronehd.hitboxutils.paper.inventory;

import de.netzkronehd.hitboxutils.paper.api.ItemBuilder;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.paper.utils.Items;
import de.netzkronehd.hitboxutils.paper.utils.SpigotUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class SelectSoundInventory extends PlayerClickInventory {

    private SelectedSoundInventory dragonInventory, beaconInventory, thunderInventory, bellInventory;


    public SelectSoundInventory(HitBoxPlayer player) {
        super(player, "§eJoin Sound", 9 * 5);

        fillBorders(ItemBuilder.GLASS);

        Items.SelectSound.SELECT_SOUND_ITEMS.forEach(sortedItem -> setItem(sortedItem.slot(), sortedItem.item()));
        setItem(4, new ItemBuilder(Material.PLAYER_HEAD).setName(player.getPrefixAndName()).build());
        setItem(36, ItemBuilder.BACKWARD);

    }

    @Override
    public void onClick(InventoryClickEvent e, HitBoxPlayer hp) {
        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getType() == Material.AIR) return;
        if (Items.SelectSound.DRAGON_SOUND.item().isSimilar(e.getCurrentItem())) {
            if (dragonInventory == null) {
                dragonInventory = new SelectedSoundInventory(player, this, SpigotUtils.getDisplayName(e.getCurrentItem()), null);
            }
            hp.openInventory(dragonInventory);


        } else if (Items.SelectSound.BEACON_SOUND.item().isSimilar(e.getCurrentItem())) {
            if (beaconInventory == null) {
                beaconInventory = new SelectedSoundInventory(player, this, SpigotUtils.getDisplayName(e.getCurrentItem()), null);
            }
            hp.openInventory(beaconInventory);


        } else if (Items.SelectSound.THUNDER_SOUND.item().isSimilar(e.getCurrentItem())) {
            if (thunderInventory == null) {
                thunderInventory = new SelectedSoundInventory(player, this, SpigotUtils.getDisplayName(e.getCurrentItem()), null);
            }
            hp.openInventory(thunderInventory);

        } else if (Items.SelectSound.BELL_SOUND.item().isSimilar(e.getCurrentItem())) {
            if (bellInventory == null) {
                bellInventory = new SelectedSoundInventory(player, this, SpigotUtils.getDisplayName(e.getCurrentItem()), null);
            }
            hp.openInventory(bellInventory);

        } else if (ItemBuilder.BACKWARD.isSimilar(e.getCurrentItem())) {
            hp.getPlayer().closeInventory();
        }

    }

    @Override
    public void onClose(InventoryCloseEvent e, HitBoxPlayer hp) {

    }
}
