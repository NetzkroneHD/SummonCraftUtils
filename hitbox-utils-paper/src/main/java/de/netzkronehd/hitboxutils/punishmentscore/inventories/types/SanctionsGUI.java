package de.netzkronehd.hitboxutils.punishmentscore.inventories.types;

import de.netzkronehd.hitboxutils.punishmentscore.api.database.models.Sanction;
import de.netzkronehd.hitboxutils.punishmentscore.inventories.PunishmentInventory;
import de.netzkronehd.hitboxutils.punishmentscore.inventories.builders.InventoryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SanctionsGUI extends InventoryBuilder implements PunishmentInventory {
    private final Map<String, Sanction> sanction;
    private final int size;

    public SanctionsGUI(Map<String, Sanction> sanction) {
        super("sanctions.yml");
        this.size = this.inventoryConfiguration.getInt("size");
        this.sanction = sanction;
        this.inventoryName = this.inventoryConfiguration.getString("name");
    }

    @Override
    public void build() {
        String displayName;
        List<String> stringList;
        ConfigurationSection types = this.inventoryConfiguration.getConfigurationSection("items");
        for (String type : types.getKeys(false)) {
            ConfigurationSection cs = types.getConfigurationSection(type);
            int slot = cs.getInt("slot");
            String materialName = cs.getString("material");
            if (this.sanction.containsKey(type)) {
                int id = this.sanction.get(type).getId();
                displayName = cs.getString("displayName").replaceAll("%id%", String.valueOf(id));
                stringList = formatLore(type);
            } else {
                displayName = cs.getString("no-punishments-name");
                stringList = cs.getStringList("no-punishments-lore");
            }
            List<String> lore = stringList;
            ItemStack item = getItem(Material.matchMaterial(materialName), 0, displayName, lore);
            this.inventories[slot] = item;
        }
    }

    @Override
    public Inventory getFirstInventory() {
        Inventory inv = Bukkit.createInventory(null, this.size, this.inventoryName);
        inv.setContents(this.inventories);
        return inv;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
    }

    @Override
    public String getPunishedPlayer() {
        return "";
    }

    private List<String> formatLore(String key) {
        List<String> unformattedLore = this.inventoryConfiguration.getStringList("items." + key + ".lore");
        Sanction sanction = this.sanction.get(key);
        List<String> lore = new ArrayList<>();
        String date = formatTimestamp(sanction.getDate());
        String until = formatTimestamp(sanction.getUntil());
        for (String line : unformattedLore) {
            String formated = line.replaceAll("%reason%", sanction.getReason());
            lore.add(formated.replaceAll("%staff%", sanction.getStaff()).replaceAll("%date%", date).replaceAll("%until%", until));
        }
        if (canAppeal(sanction.getDate())) {
            lore.addAll(this.inventoryConfiguration.getStringList("items." + key + ".appeal-lore"));
        }
        return lore;
    }

    private boolean canAppeal(Long time) {
        long difference = Math.abs(Timestamp.from(Instant.now()).getTime() - time.longValue());
        long seconds = TimeUnit.SECONDS.convert(difference, TimeUnit.MILLISECONDS);
        return seconds < ((long) this.inventoryConfiguration.getInt("appeal-time"));
    }

    private String formatTimestamp(Long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date(timestamp);
        return sdf.format(date);
    }
}
