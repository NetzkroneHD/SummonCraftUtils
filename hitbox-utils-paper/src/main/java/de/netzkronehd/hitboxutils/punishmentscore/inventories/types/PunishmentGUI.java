package de.netzkronehd.hitboxutils.punishmentscore.inventories.types;

import de.netzkronehd.hitboxutils.punishmentscore.PunishmentsCore;
import de.netzkronehd.hitboxutils.punishmentscore.inventories.PunishmentInventory;
import de.netzkronehd.hitboxutils.punishmentscore.inventories.builders.PaginatedInventory;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class PunishmentGUI extends PaginatedInventory implements PunishmentInventory {
    private final PunishmentsCore plugin = PunishmentsCore.getInstance();

    private final Map<String, Integer> punishmentList;

    private final String punishedPlayer;

    public PunishmentGUI(Map<String, Integer> punishmentList, String punishedPlayer) {
        super("punish.yml");
        this.pages = this.inventoryConfiguration.getInt("pages");
        int size = this.inventoryConfiguration.getInt("size", 54);
        this.punishmentList = punishmentList;
        this.inventories = new ItemStack[this.pages][size];
        this.punishedPlayer = punishedPlayer;
        this.inventoryName = this.inventoryName.replaceAll("%player%", punishedPlayer);
    }

    public void build() {
        if (this.inventories.length > 1)
            setPaginationItems();
        ConfigurationSection sec = this.inventoryConfiguration.getConfigurationSection("items");
        for (String key : sec.getKeys(false)) {
            String name = sec.getString(key + ".displayName");
            int slot = sec.getInt(key + ".slot");
            int page = sec.getInt(key + ".page", 1) - 1;
            boolean needsConfirmation = sec.getBoolean(key + ".confirm");
            int command_id = getCommandId(key);
            String materialName = sec.getString(key + ".material", "STONE");
            int durability = sec.getInt(key + ".damage", 0);
            Material m = Material.matchMaterial(materialName);
            String permission_required = sec.getString(key + ".levels." + command_id + ".permission", "");
            List<String> lore = sec.getStringList(key + ".levels." + command_id + ".lore");
            ItemStack is = getItem(m, durability, name, lore);
            NBT.modify(is, nbti -> {
                nbti.setBoolean("conf", Boolean.valueOf(needsConfirmation));
                nbti.setString("perm", permission_required);
                nbti.setString("key", key);
                nbti.setString("level", Integer.toString(command_id));
            });
            this.inventories[page][slot] = is;
        }
    }

    public void handleClick(InventoryClickEvent event) {
        NBTItem nbti = new NBTItem(event.getCurrentItem());
        Player p = (Player) event.getWhoClicked();
        boolean confirmation = nbti.getBoolean("conf").booleanValue();
        String key = nbti.getString("key");
        String level = nbti.getString("level");
        PunishmentGUI pg = (PunishmentGUI) this.plugin.getInventoryManager().getCurrentInventory(p);
        if (key.equals("backPage")) {
            ItemStack[] is = pg.getBackPage();
            event.getClickedInventory().setContents(is);
            p.updateInventory();
            return;
        }
        if (key.equals("nextPage")) {
            ItemStack[] is = pg.getNextPage();
            event.getClickedInventory().setContents(is);
            p.updateInventory();
            return;
        }
        if (confirmation) {
            ConfirmationGUI cg = new ConfirmationGUI(nbti.getItem(), pg);
            this.plugin.getInventoryManager().openInventory(p, cg);
            cg.build();
            this.plugin.getInventoryManager().skipCloseAdd(p);
            p.openInventory(cg.getFirstInventory());
            return;
        }
        List<String> commands = this.inventoryConfiguration.getStringList("items." + key + ".levels." + level + ".commands");
        String reason = this.inventoryConfiguration.getString("items." + key + ".reason");
        List<String> parsedCommands = parseCommands(p, this.punishedPlayer, reason, commands);
        executeCommands(p, parsedCommands);
        Player punished = Bukkit.getServer().getPlayer(this.punishedPlayer);
        if (this.plugin.getHitBoxUtils().getConfig().getBoolean("notifications") && punished == null)
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin.getHitBoxUtils(), () -> this.plugin.getDbManager().createNotification(this.punishedPlayer));
    }

    public String getPunishedPlayer() {
        return this.punishedPlayer;
    }

    protected String getPunishment(String key) {
        return this.inventoryConfiguration.getString("items." + key + ".reason");
    }

    protected String getName(String key, String level) {
        return this.inventoryConfiguration.getString("items." + key + ".levels." + level + ".name");
    }

    protected List<String> getCommands(String key, String level) {
        return this.inventoryConfiguration.getStringList("items." + key + ".levels." + level + ".commands");
    }

    protected String getReason(String key) {
        return this.inventoryConfiguration.getString("items." + key + ".reason");
    }

    private int getCommandId(String key) {
        String reason = this.inventoryConfiguration.getString("items." + key + ".reason");
        ConfigurationSection cs = this.inventoryConfiguration.getConfigurationSection("items." + key + ".levels");
        int max = 0;
        for (String s : cs.getKeys(false)) {
            int num = Integer.parseInt(s);
            if (num > max)
                max = num;
        }
        int punishmentCount = this.punishmentList.getOrDefault(reason, Integer.valueOf(0)).intValue();
        return Math.min(punishmentCount, max);
    }
}
