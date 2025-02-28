package de.netzkronehd.hitboxutils.punishmentscore.inventories.types;

import de.netzkronehd.hitboxutils.punishmentscore.PunishmentsCore;
import de.netzkronehd.hitboxutils.punishmentscore.api.database.models.Order;
import de.netzkronehd.hitboxutils.punishmentscore.inventories.PunishmentInventory;
import de.netzkronehd.hitboxutils.punishmentscore.inventories.builders.PaginatedInventory;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class OrdersGUI extends PaginatedInventory implements PunishmentInventory {

    private final PunishmentsCore plugin;
    private final List<Order> ordersList;

    public OrdersGUI(List<Order> ordersList) {
        super("orders.yml");
        this.plugin = PunishmentsCore.getInstance();
        this.ordersList = ordersList;
        this.pages = (int) Math.ceil(ordersList.size() / (this.size - 9));
        if (this.size == 9) {
            throw new IllegalArgumentException("Orders.yml size must be greater than 9");
        }
        this.inventories = new ItemStack[this.pages][this.size];
        if (this.pages > 1) {
            setPaginationItems();
        }
    }

    @Override
    public void build() {
        int slot = 0;
        int page = 0;
        for (Order o : this.ordersList) {
            final String materialName = this.inventoryConfiguration.getString("item.material");
            final int durability = this.inventoryConfiguration.getInt("item.damage");
            final int id = o.getId();
            final String name = this.inventoryConfiguration.getString("item.displayName");
            final String name2 = name.replaceAll("%id%", String.valueOf(id)).replaceAll("%player%", o.getUserPunished());
            final Material m = Material.matchMaterial(materialName);
            final ItemStack is = getItem(m, durability, name2, parseLore(o));
            if (slot == 45) {
                page++;
                slot = 0;
            }
            NBT.modify(is, nbti -> {
                nbti.setInteger("id", Integer.valueOf(id));
            });
            this.inventories[page][slot] = is;
            slot++;
        }
    }

    public List<String> parseLore(Order o) {
        final List<String> lore = new ArrayList<>();
        final List<String> format = this.inventoryConfiguration.getStringList("item.lore");
        for (String s : format) {
            final String line = s.replaceAll("%staff%", o.getUsername());
            lore.add(line.replaceAll("%punishment%", o.getPunishment())
                    .replaceAll("%player%", o.getUserPunished())
                    .replaceAll("%reason%", o.getReason())
                    .replaceAll("%date%", o.getDate().toString())
            );
        }
        return lore;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        final NBTItem nbti = new NBTItem(event.getCurrentItem());
        if (!(event.getWhoClicked() instanceof final Player p)) {
            return;
        }
        if (nbti.hasTag("key")) {
            final String key = nbti.getString("key");
            if (key.equals("backPage")) {
                final PunishmentGUI pg = (PunishmentGUI) this.plugin.getInventoryManager().getCurrentInventory(p);
                final ItemStack[] is = pg.getBackPage();
                event.getClickedInventory().setContents(is);
                p.updateInventory();
                return;
            }
            if (key.equals("nextPage")) {
                final PunishmentGUI pg2 = (PunishmentGUI) this.plugin.getInventoryManager().getCurrentInventory(p);
                final ItemStack[] is2 = pg2.getNextPage();
                event.getClickedInventory().setContents(is2);
                p.updateInventory();
                return;
            }
            return;
        }
        int id = nbti.getInteger("id");
        boolean bedrock = this.plugin.getHitBoxUtils().getConfig().getBoolean("bedrock");
        if ((event.getClick() == ClickType.DROP && bedrock) || event.getClick().isRightClick()) {
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin.getHitBoxUtils(), () -> {
                this.plugin.getDbManager().deleteOrder(id);
                Bukkit.getScheduler().callSyncMethod(this.plugin.getHitBoxUtils(), () -> {
                    String msg = this.plugin.getMessages().getString("Messages.order_deleted");
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                    p.closeInventory();
                    return null;
                });
            });
        } else if (event.getClick().isLeftClick()) {
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin.getHitBoxUtils(), () -> {
                List<String> commands = this.plugin.getDbManager().getCommandsFromOrder(id);
                if (!commands.isEmpty()) {
                    Bukkit.getScheduler().callSyncMethod(this.plugin.getHitBoxUtils(), () -> {
                        executeCommands(p, commands);
                        return null;
                    });
                }
                this.plugin.getDbManager().deleteOrder(id);
                Bukkit.getScheduler().callSyncMethod(this.plugin.getHitBoxUtils(), () -> {
                    p.closeInventory();
                    return null;
                });
            });
        }
    }

    @Override
    public String getPunishedPlayer() {
        return "";
    }
}
