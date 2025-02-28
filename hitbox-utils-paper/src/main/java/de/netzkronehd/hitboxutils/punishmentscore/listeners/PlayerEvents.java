package de.netzkronehd.hitboxutils.punishmentscore.listeners;

import de.netzkronehd.hitboxutils.punishmentscore.PunishmentsCore;
import de.netzkronehd.hitboxutils.punishmentscore.TitleAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvents implements Listener {
    private final PunishmentsCore plugin;

    public PlayerEvents(PunishmentsCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (this.plugin.getInventoryManager().hasInventory(p)) {
            return;
        }
        this.plugin.getInventoryManager().closeInventory(p);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin.getHitBoxUtils(), () -> {
            if (this.plugin.getDbManager().hasNotifications(p.getName())) {
                final String title = this.plugin.getMessages().getString("Messages.notifications.title");
                final String subtitle = this.plugin.getMessages().getString("Messages.notifications.subtitle");
                final String text = this.plugin.getMessages().getString("Messages.notifications.text");
                Bukkit.getScheduler().callSyncMethod(this.plugin.getHitBoxUtils(), () -> {
                    TitleAPI.sendTitle(this.plugin, p, 10, 70, 20, title, subtitle);
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', text));
                    return null;
                });
                this.plugin.getDbManager().deleteNotifications(p.getName());
            }
        });
    }
}
