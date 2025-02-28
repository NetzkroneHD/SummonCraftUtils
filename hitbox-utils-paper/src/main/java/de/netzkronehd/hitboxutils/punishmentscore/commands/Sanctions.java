package de.netzkronehd.hitboxutils.punishmentscore.commands;

import de.netzkronehd.hitboxutils.punishmentscore.PunishmentsCore;
import de.netzkronehd.hitboxutils.punishmentscore.api.database.models.Sanction;
import de.netzkronehd.hitboxutils.punishmentscore.inventories.types.SanctionsGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

import static de.netzkronehd.translation.Message.formatColoredValue;

public class Sanctions implements CommandExecutor {
    public final PunishmentsCore plugin;

    public Sanctions(PunishmentsCore plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof final Player p)) {
            sender.sendMessage("Only players can execute this command");
            return true;
        }
        if (!p.hasPermission("punishmentscore.sanctions")) {
            final String msg = this.plugin.getMessages().getString("Messages.no_permission");
            p.sendMessage(formatColoredValue(msg));
            return true;
        }
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin.getHitBoxUtils(), () -> {
            final Map<String, Sanction> sanctions = plugin.getLitebansAPI().getLastPunishment(p.getUniqueId());
            if (sanctions == null) {
                final String msg = this.plugin.getMessages().getString("Messages.error");
                sender.sendMessage(formatColoredValue(msg));
            } else {
                final SanctionsGUI sg = new SanctionsGUI(sanctions);
                sg.build();
                this.plugin.getInventoryManager().openInventory(p, sg);
                Bukkit.getScheduler().callSyncMethod(this.plugin.getHitBoxUtils(), () -> {
                    p.openInventory(sg.getFirstInventory());
                    return null;
                });
            }
        });
        return true;
    }
}
