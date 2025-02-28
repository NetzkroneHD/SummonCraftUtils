package de.netzkronehd.hitboxutils.paper.inventory;

import de.netzkronehd.hitboxutils.paper.api.ItemBuilder;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.paper.utils.Items;
import de.netzkronehd.hitboxutils.utils.Utils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.concurrent.TimeUnit;

@Getter
public class SelectedSoundInventory extends PlayerClickInventory {

    private final SelectSoundInventory selectSoundInventory;
    private final String name;
    private final Sound sound;

    public SelectedSoundInventory(HitBoxPlayer player, SelectSoundInventory selectSoundInventory, String name, Sound sound) {
        super(player, "§7Selected-Sound§8:§e " + name, 9 * 5);
        this.selectSoundInventory = selectSoundInventory;
        this.name = name;
        this.sound = sound;

        fillBorders(ItemBuilder.GLASS);

        Items.SelectedSound.SELECTED_SOUND_ITEMS.forEach(sortedItem -> setItem(sortedItem.slot(), sortedItem.item()));
        setItem(4, new ItemBuilder(Material.PLAYER_HEAD).setName(player.getPrefixAndName()).build());
        setItem(36, ItemBuilder.BACKWARD);

    }

    @Override
    public void onClick(InventoryClickEvent e, HitBoxPlayer hp) {
        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getType() == Material.AIR) return;
        if (Items.SelectedSound.SET_SOUND.item().isSimilar(e.getCurrentItem())) {
            if (!Utils.isOver(hp.getClickTime())) return;
            hp.setClickTime(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(2));
            hp.getPlayer().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            hp.sendMessage("You successfully selected the sound§e " + name + "§7.");
            hp.playSound(sound);


        } else if (Items.SelectedSound.TEST_SOUND.item().isSimilar(e.getCurrentItem())) {
            if (!Utils.isOver(hp.getClickTime())) return;
            hp.setClickTime(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(1));
            hp.playSound(sound);

        }

    }

    @Override
    public void onClose(InventoryCloseEvent e, HitBoxPlayer hp) {

    }
}
