package de.netzkronehd.hitboxutils.punishmentscore.inventories.types;

import de.netzkronehd.hitboxutils.punishmentscore.PunishmentsCore;
import de.netzkronehd.hitboxutils.punishmentscore.inventories.PunishmentInventory;
import de.netzkronehd.hitboxutils.punishmentscore.inventories.builders.InventoryBuilder;
import de.netzkronehd.translation.Message;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static de.netzkronehd.translation.Message.formatColoredValue;

public class ConfirmationGUI extends InventoryBuilder implements PunishmentInventory {
    private final PunishmentsCore plugin;
    private final int confirmation_slot;
    private final int deny_slot;
    private final ItemStack confirmationItem;
    private final PunishmentGUI gui;
    private final int size;

    public ConfirmationGUI(ItemStack confirmationItem, PunishmentGUI gui) {
        super("confirmation.yml");
        this.plugin = PunishmentsCore.getInstance();
        this.size = this.inventoryConfiguration.getInt("size", 54);
        this.confirmation_slot = this.inventoryConfiguration.getInt("accept.slot", 12);
        this.deny_slot = this.inventoryConfiguration.getInt("deny.slot", 14);
        this.confirmationItem = confirmationItem;
        this.gui = gui;
        this.inventoryName = this.inventoryName.replaceAll("%player%", this.gui.getPunishedPlayer());
    }

    @Override
    public void build() {
        final String materialName = this.inventoryConfiguration.getString("deny.item.material", "STONE");
        final Material m = Material.matchMaterial(materialName);
        final int durability = this.inventoryConfiguration.getInt("deny.item.damage", 0);
        final String displayName = this.inventoryConfiguration.getString("deny.item.displayName", "Cancel");
        final List<String> lore = this.inventoryConfiguration.getStringList("deny.item.lore");
        final ItemStack denyItem = getItem(m, durability, displayName, lore);
        this.inventories[this.deny_slot] = denyItem;
        this.inventories[this.confirmation_slot] = this.confirmationItem;
    }

    @Override
    public Inventory getFirstInventory() {
        final Inventory inv = Bukkit.createInventory(null, this.size, formatColoredValue(this.inventoryName));
        inv.setContents(this.inventories);
        return inv;
    }

    @Override
    public String getPunishedPlayer() {
        return this.gui.getPunishedPlayer();
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        final NBTItem nbti = new NBTItem(event.getCurrentItem());
        final Player p = (Player) event.getWhoClicked();
        if (event.getSlot() == this.deny_slot) {
            this.plugin.getInventoryManager().openInventory(p, this.gui);
            this.plugin.getInventoryManager().skipCloseAdd(p);
            p.openInventory(this.gui.getFirstInventory());
            return;
        }
        final String permission = nbti.getString("perm");
        final String key = nbti.getString("key");
        final String level = nbti.getString("level");
        if (!permission.isEmpty() && !p.hasPermission(permission)) {
            final boolean db = this.plugin.getHitBoxUtils().getConfig().getBoolean("Database.use");
            String msg = this.plugin.getMessages().getString("Messages.no_permission_staff");
            if (db) {
                final String punishment = this.gui.getPunishment(key);
                final String name = this.gui.getName(key, level);
                final List<String> commands = this.gui.getCommands(key, level);
                final String reason = this.gui.getReason(key);
                final List<String> parsedCommands = parseCommands(p, this.gui.getPunishedPlayer(), reason, commands);
                final int order = this.plugin.getDbManager().createOrder(p, this.gui.getPunishedPlayer(), name, punishment, parsedCommands);
                if (order == -1) {
                    msg = this.plugin.getMessages().getString("Messages.order_error");
                } else {
                    final String msg2 = this.plugin.getMessages().getString("Messages.creating_order");
                    msg = msg2.replaceAll("%order%", String.valueOf(order));
                }
            }
            p.closeInventory();
            p.sendMessage(formatColoredValue(msg));
            return;
        }
        final List<String> commands2 = this.gui.getCommands(key, level);
        final String reason2 = this.gui.getReason(key);
        final List<String> parsedCommands2 = parseCommands(p, this.gui.getPunishedPlayer(), reason2, commands2);
        executeCommands(p, parsedCommands2);
        p.closeInventory();
        final Player punished = Bukkit.getServer().getPlayer(this.gui.getPunishedPlayer());
        if (this.plugin.getHitBoxUtils().getConfig().getBoolean("notifications") && punished == null) {
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin.getHitBoxUtils(), () -> {
                this.plugin.getDbManager().createNotification(this.gui.getPunishedPlayer());
            });
        }
    }
}
