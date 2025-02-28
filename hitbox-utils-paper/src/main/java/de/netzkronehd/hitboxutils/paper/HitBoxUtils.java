package de.netzkronehd.hitboxutils.paper;

import de.netzkronehd.hitboxutils.database.cache.packet.server.ServerStartedPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.server.ServerStoppedPacket;
import de.netzkronehd.hitboxutils.license.License;
import de.netzkronehd.hitboxutils.paper.api.WorldGuardApi;
import de.netzkronehd.hitboxutils.paper.clickblock.ClickBlockExecuteCommand;
import de.netzkronehd.hitboxutils.paper.command.*;
import de.netzkronehd.hitboxutils.paper.listener.*;
import de.netzkronehd.hitboxutils.paper.manager.*;
import de.netzkronehd.hitboxutils.paper.placeholder.HitBoxExpansion;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.punishmentscore.PunishmentsCore;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static de.netzkronehd.hitboxutils.utils.Constants.PluginMessage.BUNGEE_CORD;
import static de.netzkronehd.hitboxutils.utils.Constants.PluginMessage.PLUGIN_MESSAGE_CHANNEL;

@Getter
public final class HitBoxUtils extends JavaPlugin {

    private static HitBoxUtils instance;

    private AfkManager afkManager;
    private ChatFilterManager chatFilterManager;
    private ChatManager chatManager;
    private ClearItemManager clearItemManager;
    private ClickBlockManager clickBlockManager;
    private CommandManager commandManager;
    private ConsoleCommandManager consoleCommandManager;
    private DatabaseManager databaseManager;
    private InventoryManager inventoryManager;
    private MessageManager messageManager;
    private MineDetectionManager mineDetectionManager;
    private PlayerListInventoryManager playerListInventoryManager;
    private PlayerManager playerManager;
    private PlaytimeManager playtimeManager;
    private PunishmentManager punishmentManager;
    private RedisManager redisManager;
    private RestartManager restartManager;
    private SchedulerManager schedulerManager;
    private ServerMuteManager serverMuteManager;
    private SoundManager soundManager;
    private StaffSettingManager staffSettingManager;
    private VanishManager vanishManager;

    private HashMap<UUID, HitBoxPlayer> playerCache;

    private LuckPerms luckPermsApi;
    private WorldGuardApi worldGuardApi;
    private PunishmentsCore punishmentsCore;

    @Override
    public void onLoad() {
        instance = this;
        playerCache = new HashMap<>();
        ConfigurationSerialization.registerClass(ClickBlockExecuteCommand.class);

    }

    @Override
    public void onEnable() {

        getLogger().info("Checking license...");
        License.checkLicense();

        afkManager = new AfkManager(this);
        chatManager = new ChatManager(this);
        chatFilterManager = new ChatFilterManager(this);
        clearItemManager = new ClearItemManager(this);
        clickBlockManager = new ClickBlockManager(this);
        commandManager = new CommandManager(this);
        consoleCommandManager = new ConsoleCommandManager(this);
        databaseManager = new DatabaseManager(this);
        inventoryManager = new InventoryManager(this);
        messageManager = new MessageManager(this);
        mineDetectionManager = new MineDetectionManager(this);
        playerListInventoryManager = new PlayerListInventoryManager(this);
        playerManager = new PlayerManager(this);
        playtimeManager = new PlaytimeManager(this);
        punishmentManager = new PunishmentManager(this);
        redisManager = new RedisManager(this);
        restartManager = new RestartManager(this);
        schedulerManager = new SchedulerManager(this);
        serverMuteManager = new ServerMuteManager(this);
        soundManager = new SoundManager(this);
        staffSettingManager = new StaffSettingManager(this);
        vanishManager = new VanishManager(this);

        Manager.loadManagers();

        try {
            luckPermsApi = Objects.requireNonNull(LuckPermsProvider.get());
            getLogger().info("LuckPermsApi loaded.");
        } catch (Exception ex) {
            getLogger().severe("Could not load LuckPermApi: " + ex);
        }

        getServer().getMessenger().registerIncomingPluginChannel(this, "bungeecord:main", new MessageListener(this));
        getServer().getMessenger().registerIncomingPluginChannel(this, BUNGEE_CORD, new MessageListener(this));
        getServer().getMessenger().registerIncomingPluginChannel(this, PLUGIN_MESSAGE_CHANNEL, new MessageListener(this));
        getServer().getMessenger().registerOutgoingPluginChannel(this, PLUGIN_MESSAGE_CHANNEL);
        getServer().getMessenger().registerOutgoingPluginChannel(this, BUNGEE_CORD);

        getLogger().info("Loading PlaceholderAPI...");
        try {
            new HitBoxExpansion(this).register();
        } catch (Exception e) {
            getLogger().severe("Error loading PlaceholderAPI: " + e);
        }

        getLogger().info("Loading WorldGuardAPI...");
        this.worldGuardApi = new WorldGuardApi(this);
        this.worldGuardApi.checkSupport();
        if (this.worldGuardApi.isSupported()) {
            getLogger().info("WorldGuardAPI loaded.");
        } else getLogger().info("Cloud not load WorldGuardAPI.");

        getLogger().info("Registering commands and listeners...");

        commandManager.registerCommand(
                new AdminCommand(this),
                new ChatLogCommand(this),
                new ClearItemsCommand(this),
                new ClickBlockCommand(this),
                new EnchantCommand(this),
                new GameModeCommand(this),
                new IpPaperCommand(this),
                new RenameCommand(this),
                new RepairCommand(this),
                new ScreenShareCommand(this),
                new SendCommand(this),
                new ServerMuteCommand(this),
                new ServerRestartCommand(this),
                new StaffCommand(this),
                new TeleportCommand(this)
        );

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new FreezeListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new MineDetectionListener(this), this);
        getServer().getPluginManager().registerEvents(new SignListener(this), this);
        getServer().getPluginManager().registerEvents(new SpectateListener(this), this);

        schedulerManager.startScheduler();
        playerManager.updatePlayerCount();

        getServer().getScheduler().scheduleSyncDelayedTask(this, () -> redisManager.runAfterReady(() -> {
                redisManager.sendPacket(new ServerStartedPacket(redisManager.getServerName(), redisManager.getServerName()));
                redisManager.log("ServerStartedPacket successfully sent.");
        }));

        getLogger().info("Registered commands and listeners.");

        getLogger().info("Loading PunishmentCore Plugin...");
        punishmentsCore = new PunishmentsCore(this);
        try {
            punishmentsCore.onEnable();
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().info("Error loading PunishmentCore Plugin: "+e);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling plugin...");
        for (HitBoxPlayer player : getPlayers()) {
            if(!player.isSpectating()) continue;
            player.disableSpectatorMode();
        }
        playerManager.setPlayerCount(0);

        if(schedulerManager.getSyncTask() != null && !schedulerManager.getSyncTask().isCancelled()) {
            schedulerManager.getSyncTask().cancel();
        }
        if(afkManager.getAfkTimer() != null && !afkManager.getAfkTimer().isCancelled()) {
            afkManager.getAfkTimer().cancel();
        }
        redisManager.sendPacket(new ServerStoppedPacket(redisManager.getServerName(), redisManager.getServerName()));
        getLogger().info("Plugin disabled.");
        getLogger().info("Disabling PunishmentCore Plugin...");
        punishmentsCore.onDisable();
    }

    public void runAsync(Runnable run) {
        getServer().getScheduler().runTaskAsynchronously(this, run);
    }

    public void runSync(Runnable run) {
        schedulerManager.runSync(run);
    }

    public void runAsync(Runnable run, long delay) {
        getServer().getScheduler().runTaskLaterAsynchronously(this, run, delay);
    }

    public HitBoxPlayer getPlayer(String name) {
        return getPlayer(getServer().getPlayer(name));
    }

    public HitBoxPlayer getPlayer(Player player) {
        if (player == null) return null;
        return getPlayer(player.getUniqueId());
    }


    public HitBoxPlayer getPlayer(UUID uuid) {
        return playerCache.get(uuid);
    }

    public HitBoxPlayer getPlayerByPrefixName(String name) {
        return playerCache.values().stream().filter(p -> p.getPrefixAndName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public HitBoxPlayer getPlayerByDisplayName(String displayName) {
        return playerCache.values().stream().filter(p -> p.getDisplayName().equalsIgnoreCase(displayName)).findFirst().orElse(null);
    }

    public static HitBoxUtils getInstance() {
        return instance;
    }

    public Collection<HitBoxPlayer> getPlayers() {
        return playerCache.values();
    }


}
