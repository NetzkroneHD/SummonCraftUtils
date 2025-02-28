package de.netzkronehd.hitboxutils.punishmentscore.inventories.builders;

import de.netzkronehd.hitboxutils.database.cache.packet.litebans.LitebansPunishPacket;
import de.netzkronehd.hitboxutils.punishmentscore.PunishmentsCore;
import de.netzkronehd.translation.sender.Sender;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class InventoryBuilder {
    protected final ItemStack[] inventories;

    protected final YamlConfiguration inventoryConfiguration;

    protected final int size;

    protected String inventoryName;

    public InventoryBuilder(String configuration) {
        PunishmentsCore plugin = PunishmentsCore.getInstance();
        this.inventoryConfiguration = plugin.getInventoryManager().getInventoryConfiguration(configuration);
        this.size = this.inventoryConfiguration.getInt("size", 54);
        this.inventories = new ItemStack[this.size];
        this.inventoryName = this.inventoryConfiguration.getString("name");
    }

    protected ItemStack getItem(Material m, int durability, String name, List<String> loreData) {
        ItemStack item = new ItemStack(m, 1, (short) durability);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        List<String> lore = new ArrayList<>(loreData);
        lore = lore.stream().map(string -> ChatColor.translateAlternateColorCodes('&', string)).collect(Collectors.toList());
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    protected void executeCommands(Player p, List<String> commands) {
        PunishmentsCore plugin = PunishmentsCore.getInstance();
        plugin.getHitBoxUtils().getLogger().info("Executing commands ("+p.getName()+"): " + commands);
        for (String s : commands) {
            if (s.contains("[close]")) {
                p.closeInventory();
                continue;
            }
            String[] executor = s.split(Pattern.quote("[player]"));
            if (executor.length == 1) {
                plugin.getHitBoxUtils().getLogger().info("Executing console command: " + s);
                plugin.getHitBoxUtils().getRedisManager().sendPacket(new LitebansPunishPacket(
                        plugin.getHitBoxUtils().getRedisManager().getServerName(),
                        Sender.CONSOLE_UUID,
                        p.getUniqueId(),
                        s
                ));
                continue;
            }
            plugin.getHitBoxUtils().getLogger().info("Executing player command: " + s);
            plugin.getHitBoxUtils().getRedisManager().sendPacket(new LitebansPunishPacket(
                    plugin.getHitBoxUtils().getRedisManager().getServerName(),
                    p.getUniqueId(),
                    p.getUniqueId(),
                    executor[1]
            ));
        }
    }

    protected List<String> parseCommands(Player p, String punishedUser, String reason, List<String> cmd) {
        List<String> commandsParsed = new ArrayList<>();
        for (String s : cmd) {
            String finalCommand = s.replaceAll("%player%", punishedUser);
            finalCommand = finalCommand.trim();
            finalCommand = finalCommand.replaceAll("%sender%", p.getName());
            finalCommand = finalCommand.replaceAll("%uuid%", p.getUniqueId().toString());
            finalCommand = finalCommand.replaceAll("%reason%", reason);
            finalCommand = finalCommand.trim();
            commandsParsed.add(finalCommand);
        }
        return commandsParsed;
    }

    public abstract void build();

    public abstract Inventory getFirstInventory();
}
