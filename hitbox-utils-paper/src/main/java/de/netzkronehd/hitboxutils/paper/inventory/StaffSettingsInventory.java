package de.netzkronehd.hitboxutils.paper.inventory;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.api.ItemBuilder;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.paper.utils.Items;
import de.netzkronehd.hitboxutils.paper.utils.SpigotUtils;
import de.netzkronehd.hitboxutils.utils.Constants;
import de.netzkronehd.hitboxutils.utils.Utils;
import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import static de.netzkronehd.hitboxutils.paper.utils.Items.StaffSettings.*;

public class StaffSettingsInventory extends PlayerClickInventory {

    private final HitBoxUtils elmoBox;
    private final HitBoxPlayer player;

    public StaffSettingsInventory(HitBoxPlayer hp) {
        super(hp, "§cStaff-Settings ", 9 * 5);
        this.elmoBox = hp.getHitBoxUtils();
        this.player = hp;
        fill(ItemBuilder.GLASS);
        setItem(36, ItemBuilder.BACKWARD);
        setItem(RESET.slot(), RESET.item());
        update();
    }

    @Override
    public void onClick(InventoryClickEvent e, HitBoxPlayer hp) {
        if (ItemBuilder.BACKWARD.isSimilar(e.getCurrentItem())) {
            hp.openInventory(elmoBox.getPlayerListInventoryManager().getPlayerListInventories().get((hp.getPlayerListPage() == null ? 0 : hp.getPlayerListPage())));

        } else if (isClicked(ITEM_PICK_UP, e)) {
            hp.getStaffSettings().setPickUpItems(!hp.getStaffSettings().isPickUpItems());
            update();

        } else if (isClicked(SNEAK_SPECTATOR, e)) {
            hp.getStaffSettings().setDoubleSneakSpectator(!hp.getStaffSettings().isDoubleSneakSpectator());
            update();

        } else if (isClicked(AUTO_ENABLE, e)) {
            hp.getStaffSettings().setAutoEnable(!hp.getStaffSettings().isAutoEnable());
            update();

        } else if (isClicked(AUTO_VANISH, e)) {
            hp.getStaffSettings().setAutoVanish(!hp.getStaffSettings().isAutoVanish());
            update();
        } else if (isClicked(FILTER_BROADCAST, e)) {
            hp.getStaffSettings().setFilterBroadcast(!hp.getStaffSettings().isFilterBroadcast());
            update();
            sendSettingsUpdate(hp);

        } else if (isClicked(MINE_BROADCAST, e)) {
            hp.getStaffSettings().setMineBroadcast(!hp.getStaffSettings().isMineBroadcast());
            update();
            sendSettingsUpdate(hp);

        } else if (RESET.item().isSimilar(e.getCurrentItem())) {
            if (hp.getStaffSettings() != null) {
                hp.getStaffSettings().reset();
                update();
                hp.playSound(Sound.ENTITY_CHICKEN_EGG);
                SpigotUtils.sendOutgoingPluginMessage(hitBoxUtils,
                    Constants.PluginMessage.BUNGEE_CORD,
                    hp.getPlayer(),
                    Constants.PluginMessage.PLUGIN_MESSAGE_CHANNEL,
                    Constants.PluginMessage.STAFF_SETTINGS,
                    Utils.GSON.toJson(hp.getStaffSettings())
            );
            } else hp.sendMessage("Could not find any settings please try later again.");
        }
    }

    @Override
    public void onClose(InventoryCloseEvent e, HitBoxPlayer hp) {
        elmoBox.runAsync(() -> elmoBox.getStaffSettingManager().saveSettings(hp.getStaffSettings()));
    }

    public void update() {
        Items.StaffSettings.SETTINGS_ITEMS.forEach(sortedItem -> setItem(sortedItem.slot(), sortedItem.item()));

        if (player.getStaffSettings() != null) {
            setSettingItem(ITEM_PICK_UP, player.getStaffSettings().isPickUpItems());
            setSettingItem(SNEAK_SPECTATOR, player.getStaffSettings().isDoubleSneakSpectator());
            setSettingItem(AUTO_VANISH, player.getStaffSettings().isAutoVanish());
            setSettingItem(AUTO_ENABLE, player.getStaffSettings().isAutoEnable());
            setSettingItem(FILTER_BROADCAST, player.getStaffSettings().isFilterBroadcast());
            setSettingItem(MINE_BROADCAST, player.getStaffSettings().isMineBroadcast());
        } else {
            setSettingItem(ITEM_PICK_UP, null);
            setSettingItem(SNEAK_SPECTATOR, null);
            setSettingItem(AUTO_VANISH, null);
            setSettingItem(AUTO_ENABLE, null);
            setSettingItem(FILTER_BROADCAST, null);
            setSettingItem(MINE_BROADCAST, null);
        }
    }

    private void setSettingItem(Items.SortedItem sortedItem, Boolean setting) {
        if (setting == null) {
            setItem(sortedItem.slot()+9, Items.LOADING);
            return;
        }
        setItem(sortedItem.slot()+9, setting ? ENABLED.item(): DISABLED.item());
    }

    private boolean isClicked(Items.SortedItem sortedItem, InventoryClickEvent e) {
        if (sortedItem.item().isSimilar(e.getCurrentItem())) return true;
        return (sortedItem.slot()+9 == e.getRawSlot());
    }

    private void sendSettingsUpdate(HitBoxPlayer hp) {
        SpigotUtils.sendOutgoingPluginMessage(hitBoxUtils,
                Constants.PluginMessage.BUNGEE_CORD,
                hp.getPlayer(),
                Constants.PluginMessage.PLUGIN_MESSAGE_CHANNEL,
                Constants.PluginMessage.STAFF_SETTINGS,
                Utils.GSON.toJson(hp.getStaffSettings())
        );
    }


}
