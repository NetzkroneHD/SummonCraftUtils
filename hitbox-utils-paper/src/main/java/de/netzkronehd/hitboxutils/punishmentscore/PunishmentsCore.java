package de.netzkronehd.hitboxutils.punishmentscore;

import com.google.common.base.Preconditions;
import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.punishmentscore.api.LitebansAPI;
import de.netzkronehd.hitboxutils.punishmentscore.api.database.DatabaseManager;
import de.netzkronehd.hitboxutils.punishmentscore.api.database.MySQL;
import de.netzkronehd.hitboxutils.punishmentscore.commands.Orders;
import de.netzkronehd.hitboxutils.punishmentscore.commands.Punish;
import de.netzkronehd.hitboxutils.punishmentscore.commands.Sanctions;
import de.netzkronehd.hitboxutils.punishmentscore.inventories.InventoryManager;
import de.netzkronehd.hitboxutils.punishmentscore.listeners.InventoryEvents;
import de.netzkronehd.hitboxutils.punishmentscore.listeners.PlayerEvents;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Getter
public final class PunishmentsCore {

    @Getter
    private static PunishmentsCore instance;

    private final HitBoxUtils hitBoxUtils;
    private final PluginDescriptionFile pluginfile;
    private Path dataFolderPath;
    private InventoryManager inventoryManager;
    private YamlConfiguration messages;
    private DatabaseManager dbManager;
    private String version;
    private LitebansAPI litebansAPI;

    private final YamlConfiguration config;

    public PunishmentsCore(HitBoxUtils hitBoxUtils) {
        this.hitBoxUtils = hitBoxUtils;
        this.pluginfile = hitBoxUtils.getDescription();
        this.config = YamlConfiguration.loadConfiguration(new File(hitBoxUtils.getDataFolder(), "config.yml"));
    }


    public void onEnable() {
        instance = this;
        hitBoxUtils.getLogger().info("Loading PunishmentCore...");
        final String packageName = hitBoxUtils.getServer().getClass().getPackage().getName();
        this.version = packageName.substring(packageName.lastIndexOf(46) + 1);
        final long ms = System.currentTimeMillis();
        this.dataFolderPath = hitBoxUtils.getDataFolder().toPath();
        hitBoxUtils.saveDefaultConfig();
        hitBoxUtils.getLogger().info("Loading LiteBans Database...");

        final String host = getConfig().getString("LitebansDatabase.ip");
        final String database = getConfig().getString("LitebansDatabase.database");
        final String username = getConfig().getString("LitebansDatabase.user");
        final String password = getConfig().getString("LitebansDatabase.password");
        final int port = getConfig().getInt("LitebansDatabase.port");
        final String tablePrefix = getConfig().getString("LitebansDatabase.tablePrefix", "litebans_");
        this.litebansAPI = new LitebansAPI(tablePrefix);

        final MySQL mySQL = new MySQL(host, database, username, password, port);
        try {
            mySQL.connect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        litebansAPI.setConnection(mySQL.getConnection());
        hitBoxUtils.getLogger().info("LiteBans Database loaded!");

        loadInventories();
        this.messages = loadFile("messages.yml");
        Bukkit.getPluginManager().registerEvents(new InventoryEvents(this), hitBoxUtils);
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(this), hitBoxUtils);
        hitBoxUtils.getCommand("punish").setExecutor(new Punish(this));
        hitBoxUtils.getCommand("orders").setExecutor(new Orders(this));
        hitBoxUtils.getCommand("sanctions").setExecutor(new Sanctions(this));
        if (getConfig().getBoolean("Database.use")) {
            this.dbManager = new DatabaseManager();
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + this.pluginfile.getName() + " has been enabled in " + (System.currentTimeMillis() - ms) + " ms. Version: " + this.pluginfile.getVersion());

    }

    public void onDisable() {
        this.inventoryManager.closeAllInventories();
        if (this.dbManager != null) {
            this.dbManager.close();
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + this.pluginfile.getName() + " has been disabled. Version: " + this.pluginfile.getVersion());
    }

    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    public void reloadFiles() {
        this.messages = loadFile("messages.yml");
        loadInventories();
        hitBoxUtils.reloadConfig();
        if (hitBoxUtils.getConfig().getBoolean("Database.use")) {
            if (this.dbManager != null) {
                this.dbManager.reloadConnection();
            } else {
                this.dbManager = new DatabaseManager();
            }
        }
    }

    public YamlConfiguration getMessages() {
        return this.messages;
    }

    public DatabaseManager getDbManager() {
        return this.dbManager;
    }

    public boolean isNew() {
        return Integer.valueOf(this.version.split("_")[1]).intValue() > 16;
    }

    private YamlConfiguration loadFile(String filePath) {
        YamlConfiguration fileConfiguration;
        File file = new File(hitBoxUtils.getDataFolder(), filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            hitBoxUtils.saveResource(filePath, false);
        }
        try {
            fileConfiguration = YamlConfiguration.loadConfiguration(file);
        } catch (IllegalArgumentException e) {
            fileConfiguration = new YamlConfiguration();
            e.printStackTrace();
        }
        return fileConfiguration;
    }

    private void loadInventories() {
        Map<String, YamlConfiguration> inventoryConfiguration = new HashMap<>();
        String[] inventories = {"confirmation.yml", "orders.yml", "punish.yml", "sanctions.yml"};
        for (String inventory : inventories) {
            inventoryConfiguration.put(inventory, loadFile("inventories/" + inventory));
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + " Loaded " + inventory);
        }
        Preconditions.checkState(Files.isDirectory(this.dataFolderPath.resolve("inventories")), "inventories folder doesn't exist");
        this.inventoryManager = new InventoryManager(inventoryConfiguration);
    }
}
