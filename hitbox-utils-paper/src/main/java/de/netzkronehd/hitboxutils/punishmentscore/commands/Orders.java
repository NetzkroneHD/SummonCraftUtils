package de.netzkronehd.hitboxutils.punishmentscore.commands;

import de.netzkronehd.hitboxutils.punishmentscore.PunishmentsCore;
import de.netzkronehd.hitboxutils.punishmentscore.api.database.models.Order;
import de.netzkronehd.hitboxutils.punishmentscore.inventories.types.OrdersGUI;
import de.netzkronehd.translation.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

import static de.netzkronehd.translation.Message.formatColoredValue;

public class Orders implements CommandExecutor {

    private final PunishmentsCore plugin;

    public Orders(PunishmentsCore plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof final Player p)) {
            sender.sendMessage("Only players can use this command");
            return true;
        }
        if (!p.hasPermission("punishmentscore.orders")) {
            final String msg = this.plugin.getMessages().getString("Messages.no_permission");
            p.sendMessage(formatColoredValue(msg));
            return true;
        }
        if (!this.plugin.getHitBoxUtils().getConfig().getBoolean("Database.use")) {
            final String msg = this.plugin.getMessages().getString("Messages.database_disabled");
            p.sendMessage(formatColoredValue(msg));
            return true;
        }
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin.getHitBoxUtils(), () -> {
            final List<Order> orders = this.plugin.getDbManager().getOrders();
            if (orders.isEmpty()) {
                final String msg = this.plugin.getMessages().getString("Messages.no_orders");
                p.sendMessage(formatColoredValue(msg));
                return;
            }
            final OrdersGUI inventory = new OrdersGUI(orders);
            inventory.build();
            final Inventory inv = inventory.getFirstInventory();
            this.plugin.getInventoryManager().openInventory(p, inventory);
            Bukkit.getScheduler().callSyncMethod(this.plugin.getHitBoxUtils(), () -> {
                p.openInventory(inv);
                return null;
            });
        });
        return true;
    }
}
