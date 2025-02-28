package de.netzkronehd.hitboxutils.punishmentscore.commands;

import de.netzkronehd.hitboxutils.punishmentscore.PunishmentsCore;
import de.netzkronehd.hitboxutils.punishmentscore.inventories.types.PunishmentGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Map;

import static de.netzkronehd.translation.Message.formatColoredValue;

public class Punish implements CommandExecutor {
    private final PunishmentsCore plugin;

    public Punish(PunishmentsCore plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            final String msg = this.plugin.getMessages().getString("Messages.usage");
            sender.sendMessage(formatColoredValue(msg));
            return true;
        }
        if (args[0].equals("reload")) {
            if (!sender.hasPermission("punishmentscore.reload") && (sender instanceof Player)) {
                String msg = this.plugin.getMessages().getString("Messages.no_permission");
                sender.sendMessage(formatColoredValue(msg));
                return true;
            }
            final String msg = this.plugin.getMessages().getString("Messages.reload");
            sender.sendMessage(formatColoredValue(msg));
            this.plugin.getInventoryManager().closeAllInventories();
            this.plugin.reloadFiles();
            return true;
        }
        if (!(sender instanceof final Player player)) {
            sender.sendMessage("Only players can use this command");
            return true;
        }
        if (!sender.hasPermission("punishmentscore.use")) {
            final String msg = this.plugin.getMessages().getString("Messages.no_permission");
            sender.sendMessage(formatColoredValue(msg));
            return true;
        }
        final String userToPunish = args[0];
        if (player.getPlayerListName().equals(userToPunish)) {
            final String msg = this.plugin.getMessages().getString("Messages.same_player");
            sender.sendMessage(formatColoredValue(msg));
            return true;
        }
        if (args.length == 1 || (args.length == 2 && !args[1].equals("force"))) {
            if (this.plugin.getInventoryManager().isMenuOpen(userToPunish)) {
                final String msg = this.plugin.getMessages().getString("Messages.use_force");
                sender.sendMessage(formatColoredValue(msg));
                return true;
            }
        } else if (!sender.hasPermission("punishmentscore.force")) {
            final String msg = this.plugin.getMessages().getString("Messages.no_permission");
            sender.sendMessage(formatColoredValue(msg));
            return true;
        }
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin.getHitBoxUtils(), () -> {
            final Map<String, Integer> punishmentsList = plugin.getLitebansAPI().getAllPunishments(userToPunish);
            if (punishmentsList == null) {
                final String msg = this.plugin.getMessages().getString("Messages.error");
                sender.sendMessage(formatColoredValue(msg));
                return;
            }
            final Player punished = Bukkit.getServer().getPlayer(args[0]);
            if (punished == null) {
                final String msg = this.plugin.getMessages().getString("Messages.offline_player");
                sender.sendMessage(formatColoredValue(msg));
            }
            if (punishmentsList.isEmpty()) {
                String msg10 = this.plugin.getMessages().getString("Messages.no_punishments");
                sender.sendMessage(formatColoredValue(msg10));
            }
            final PunishmentGUI pg = new PunishmentGUI(punishmentsList, userToPunish);
            pg.build();
            this.plugin.getInventoryManager().openInventory(player, pg);
            Bukkit.getScheduler().callSyncMethod(this.plugin.getHitBoxUtils(), () -> {
                final Inventory inv = pg.getFirstInventory();
                player.openInventory(inv);
                return null;
            });
        });
        return true;
    }
}
