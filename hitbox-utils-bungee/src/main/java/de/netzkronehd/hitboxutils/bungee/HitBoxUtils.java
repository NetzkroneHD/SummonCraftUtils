package de.netzkronehd.hitboxutils.bungee;

import de.netzkronehd.hitboxutils.api.GeolocationApi;
import de.netzkronehd.hitboxutils.bansystemapi.BanSystemApi;
import de.netzkronehd.hitboxutils.bansystemapi.BanSystemApiHolder;
import de.netzkronehd.hitboxutils.bungee.commands.*;
import de.netzkronehd.hitboxutils.bungee.discord.manager.DiscordCommandManager;
import de.netzkronehd.hitboxutils.bungee.discord.manager.DiscordTeamChatManager;
import de.netzkronehd.hitboxutils.bungee.listener.ChatListener;
import de.netzkronehd.hitboxutils.bungee.listener.ConnectListener;
import de.netzkronehd.hitboxutils.bungee.listener.PluginMessageListener;
import de.netzkronehd.hitboxutils.bungee.manager.*;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.database.cache.packet.server.ServerStartedPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.server.ServerStoppedPacket;
import de.netzkronehd.hitboxutils.license.License;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.*;

@Getter
public class HitBoxUtils extends Plugin {

    @Getter
    private static HitBoxUtils instance;

    private HashMap<UUID, HitBoxPlayer> playerCache;


    private AltsManager altsManager;
    private BroadcastManager broadcastManager;
    private ChatFilterManager chatFilterManager;
    private CooldownManager cooldownManager;
    private DatabaseManager databaseManager;
    private DiscordCommandManager discordCommandManager;
    private DiscordTeamChatManager discordTeamChatManager;
    private DiscordManager discordManager;
    private DiscordVerifyManager discordVerifyManager;
    private MessageManager messageManager;
    private PlayerInfoManager playerInfoManager;
    private PlayerManager playerManager;
    private PlaytimeManager playtimeManager;
    private PunishmentManager punishmentManager;
    private RedisManager redisManager;
    private RestartManager restartManager;
    private ServerManager serverManager;
    private SoundManager soundManager;
    private StaffSettingManager staffSettingManager;
    private TeamChatManager teamChatManager;
    private VoteManager voteManager;

    private LuckPerms luckPermsApi;
    private GeolocationApi geolocationApi;
    private BanSystemApi banSystemApi;
    private BungeeAudiences bungeeAudiences;

    @Setter
    private boolean printPacketDebug;

    @Override
    public void onEnable() {
        instance = this;
        this.bungeeAudiences = BungeeAudiences.create(this);

        final long before = System.currentTimeMillis();
        playerCache = new HashMap<>();

        getLogger().info("Checking license...");
        License.checkLicense();

        getLogger().info("Loading LuckPermsApi...");
        try {
            luckPermsApi = Objects.requireNonNull(LuckPermsProvider.get());
            getLogger().info("LuckPermsApi loaded.");
        } catch (Exception ex) {
            getLogger().severe("Could not load LuckPermApi: " + ex);
        }
        getLogger().info("Loading manager and apis...");
        geolocationApi = new GeolocationApi();

        altsManager = new AltsManager(this);
        broadcastManager = new BroadcastManager(this);
        chatFilterManager = new ChatFilterManager(this);
        cooldownManager = new CooldownManager(this);
        databaseManager = new DatabaseManager(this);
        discordCommandManager = new DiscordCommandManager(this);
        discordTeamChatManager = new DiscordTeamChatManager(this);
        discordManager = new DiscordManager(this);
        discordVerifyManager = new DiscordVerifyManager(this);
        messageManager = new MessageManager(this);
        playerInfoManager = new PlayerInfoManager(this);
        playerManager = new PlayerManager(this);
        playtimeManager = new PlaytimeManager(this);
        punishmentManager = new PunishmentManager(this);
        serverManager = new ServerManager(this);
        redisManager = new RedisManager(this);
        restartManager = new RestartManager(this);
        soundManager = new SoundManager(this);
        staffSettingManager = new StaffSettingManager(this);
        teamChatManager = new TeamChatManager(this);
        voteManager = new VoteManager(this);

        getLogger().info("Loading BanSystemApi...");
        banSystemApi = BanSystemApiHolder.getApi();

        if (BanSystemApiHolder.isSupported()) {
            getLogger().info("Loaded BanSystemApi by "+banSystemApi.getName()+".");
        } else getLogger().warning("Could not find any BanSystemApi.");


        Manager.loadManagers();

        getLogger().info("Manager and apis loaded.");

        getLogger().info("Loading commands and listeners...");

        new AdminCommand(this).register();
        new AdminVerifyCommand(this).register();
        new AltsManagementCommand(this).register();
        new BroadcastCommand(this).register();
        new BungeeSoundCommand(this).register();
        new ChatImageCommand(this).register();
        new CheckBansCommand(this).register();
        new CheckMutesCommand(this).register();
        new CreateServerCommand(this).register();
        new CustomRestartCommand(this).register();
        new FindCommand(this).register();
        new FindProxyCommand(this).register();
        new GlobalRestartCommand(this).register();
        new GradientColorCommand(this).register();
        new IpInfoCommand(this).register();
        new JoinCommand(this).register();
        new LastJoinCommand(this).register();
        new PingCommand(this).register();
        new PlaytimeCommand(this).register();
        new PlaytimeTopCommand(this).register();
        new PluginMessageCommand(this).register();
        new ProxyRestartCommand(this).register();
        new RightsClearCommand(this).register();
        new SendCommand(this).register();
        new ServerCommand(this).register();
        new SoundCommand(this).register();
        new SudoCommand(this).register();
        new TeamChatCommand(this).register();
        new TestVoteCommand(this).register();
        new VerifyCommand(this).register();
        new WhereCommand(this).register();

        getProxy().getPluginManager().registerListener(this, new ChatListener(this));
        getProxy().getPluginManager().registerListener(this, new ConnectListener(this));
        getProxy().getPluginManager().registerListener(this, new PluginMessageListener(this));

        getLogger().info("Commands and listeners loaded.");

        getLogger().info("Plugin enabled after " + (System.currentTimeMillis() - before) + "ms.");

        redisManager.getRedisClient().runAfterReady(() -> {
            getLogger().info("Clearing TeamMembers...");
            redisManager.getRedisClient().getTeamUsers().stream()
                    .filter(teamUserModel -> teamUserModel.proxy().equals(redisManager.getServerName()))
                    .forEach(teamUserModel -> redisManager.removeTeamUser(teamUserModel.uuid()));
            getLogger().info("TeamMembers cleared.");
            redisManager.sendPacket(new ServerStartedPacket(redisManager.getServerName(), redisManager.getServerName()));
        });

    }

    @Override
    public void onDisable() {
        if (discordManager.getDiscordBot() != null) {
            getLogger().info("Disabling discord bot...");
            if(discordManager.getDiscordBot().getJda() != null) discordManager.getDiscordBot().getJda().shutdownNow();
            getLogger().info("Discord bot disabled.");
        }

        getPlayers().stream().filter(HitBoxPlayer::isTeamChatAllowed).forEach(hp -> redisManager.removeTeamUser(hp.getUniqueId()));

        getLogger().info("Plugin disabled.");
        redisManager.sendPacket(new ServerStoppedPacket(redisManager.getServerName(), redisManager.getServerName()));
    }

    public void runAsync(Runnable runnable) {
        getProxy().getScheduler().runAsync(this, runnable);
    }

    public Collection<HitBoxPlayer> getPlayers() {
        return playerCache.values();
    }

    public List<HitBoxPlayer> getStaffPlayers() {
        return getPlayers().stream().filter(HitBoxPlayer::isStaff).toList();
    }

    public HitBoxPlayer getPlayer(String name) {
        if (name == null) return null;
        return getPlayer(ProxyServer.getInstance().getPlayer(name));
    }

    public HitBoxPlayer getPlayer(ProxiedPlayer player) {
        if (player == null) return null;
        return getPlayer(player.getUniqueId());
    }

    public HitBoxPlayer getPlayer(UUID uuid) {
        return playerCache.get(uuid);
    }

}
