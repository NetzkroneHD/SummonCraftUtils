package de.netzkronehd.hitboxutils.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.netzkronehd.hitboxutils.api.GeolocationApi;
import de.netzkronehd.hitboxutils.bansystemapi.BanSystemApi;
import de.netzkronehd.hitboxutils.bansystemapi.BanSystemApiHolder;
import de.netzkronehd.hitboxutils.velocity.commands.*;
import de.netzkronehd.hitboxutils.velocity.discord.manager.DiscordCommandManager;
import de.netzkronehd.hitboxutils.velocity.listener.ChatListener;
import de.netzkronehd.hitboxutils.velocity.listener.ConnectListener;
import de.netzkronehd.hitboxutils.velocity.manager.*;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.*;

@Plugin(
        id = "hitboxutils",
        name = "hitbox-utils-velocity",
        version = "1.0-SNAPSHOT",
        authors = {"NetzkroneHD"},
        dependencies = {
                @Dependency(id = "luckperms"),
                @Dependency(id = "litebans", optional = true),
                @Dependency(id = "advancedbans", optional = true)
        }
)
@Getter
public class HitBoxUtils {

    private final Logger logger;
    private final ProxyServer proxyServer;
    private final Path dataPath;
    private final Map<UUID, HitBoxPlayer> playerCache;
    private final Set<HitBoxCommand> commands;

    private final AltsManager altsManager;
    private final ChatManager chatManager;
    private final CooldownManager cooldownManager;
    private final DatabaseManager databaseManager;
    private final DiscordCommandManager discordCommandManager;
    private final DiscordManager discordManager;
    private final DiscordVerifyManager discordVerifyManager;
    private final MessageManager messageManager;
    private final PlayerManager playerManager;
    private final PlaytimeManager playtimeManager;
    private final PunishmentManager punishmentManager;
    private final RedisManager redisManager;
    private final RestartManager restartManager;
    private final SoundManager soundManager;
    private final StaffSettingManager staffSettingManager;
    private final TeamChatManager teamChatManager;

    private final GeolocationApi geolocationApi;

    private LuckPerms luckPermsApi;
    private BanSystemApi banSystemApi;

    @Inject
    public HitBoxUtils(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataPath) {
        this.logger = logger;
        this.proxyServer = proxyServer;
        this.dataPath = dataPath;
        this.playerCache = new HashMap<>();
        this.commands = new HashSet<>();

        this.altsManager = new AltsManager(this);
        this.chatManager = new ChatManager(this);
        this.cooldownManager = new CooldownManager(this);
        this.databaseManager = new DatabaseManager(this);
        this.discordCommandManager = new DiscordCommandManager(this);
        this.discordManager = new DiscordManager(this);
        this.discordVerifyManager = new DiscordVerifyManager(this);
        this.messageManager = new MessageManager(this);
        this.playerManager = new PlayerManager(this);
        this.playtimeManager = new PlaytimeManager(this);
        this.punishmentManager = new PunishmentManager(this);
        this.redisManager = new RedisManager(this);
        this.restartManager = new RestartManager(this);
        this.soundManager = new SoundManager(this);
        this.staffSettingManager = new StaffSettingManager(this);
        this.teamChatManager = new TeamChatManager(this);

        this.geolocationApi = new GeolocationApi();

    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent e) {
        final long before = System.currentTimeMillis();

        logger.info("Loading manager and apis...");

        Manager.loadManagers();

        logger.info("Loading LuckPermsApi...");
        try {
            luckPermsApi = LuckPermsProvider.get();
            logger.info("LuckPermsApi loaded.");
        } catch (Exception ex) {
            logger.error("Could not load LuckPermApi: {}", ex.toString());
        }

        getLogger().info("Loading BanSystemApi...");
        banSystemApi = BanSystemApiHolder.getApi();

        if (BanSystemApiHolder.isSupported()) {
            logger.info("Loaded BanSystemApi by {}.", banSystemApi.getName());
        } else logger.info("Could not find any BanSystemApi.");

        logger.info("Manager and apis loaded.");

        logger.info("Loading commands and listeners...");

        new AdminCommand(this).register();
        new AdminVerifyCommand(this).register();
        new AltsManagementCommand(this).register();
        new BroadcastCommand(this).register();
        new BungeeSoundCommand(this).register();
        new CheckBansCommand(this).register();
        new CheckMutesCommand(this).register();
        new GlobalRestartCommand(this).register();
        new FindCommand(this).register();
        new GradientColorCommand(this).register();
        new IpInfoCommand(this).register();
        new JoinCommand(this).register();
        new LastJoinCommand(this).register();
        new PingCommand(this).register();
        new PlaytimeCommand(this).register();
        new PlaytimeTopCommand(this).register();
        new PluginMessageCommand(this).register();
        new ProxyRestartCommand(this).register();
        new PunishCommand(this).register();
        new SendCommand(this).register();
        new ServerCommand(this).register();
        new SudoCommand(this).register();
        new TeamChatCommand(this).register();
        new VerifyCommand(this).register();
        new WhereCommand(this).register();

        proxyServer.getEventManager().register(this, new ChatListener(this));
        proxyServer.getEventManager().register(this, new ConnectListener(this));

        commands.forEach(command -> {
            proxyServer.getCommandManager().register(command.getCommandMeta(), command);
            logger.info("Registered command '{}'.", command.getSimpleName());
        });

        logger.info("Commands and listeners loaded.");
        logger.info("Plugin enabled after {}ms.", (System.currentTimeMillis()-before));

        redisManager.getRedisClient().runAfterReady(() -> {
            logger.info("Clearing TeamMembers...");
            redisManager.getRedisClient().getTeamUsers().stream()
                    .filter(teamUserModel -> teamUserModel.proxy().equals(redisManager.getConfig().getServerName()))
                    .forEach(teamUserModel -> redisManager.removeTeamUser(teamUserModel.uuid()));
            logger.info("TeamMembers cleared.");
        });

    }

    @Subscribe
    public void onProxyShotdown(ProxyShutdownEvent e) {
        logger.info("Shutting down...");
        logger.info("Saving playtime...");
        final long timeLeft = System.currentTimeMillis();

        for (HitBoxPlayer player : getPlayers())  {
            if(playtimeManager.exists(player.getUniqueId())) {
                playtimeManager.addPlaytime(player.getUniqueId(), (timeLeft-player.getPlayerPlaytime().getTimeJoined()), 0);
            } else playtimeManager.createPlaytime(player.getUniqueId(), (timeLeft-player.getPlayerPlaytime().getTimeJoined()), 0);
        }

        if (discordManager.getDiscordBot() != null) {
            getLogger().info("Disabling discord bot...");
            if(discordManager.getDiscordBot().getJda() != null) discordManager.getDiscordBot().getJda().shutdownNow();
            getLogger().info("Discord bot disabled.");
        }

        logger.info("Playtime saved.");

        logger.info("Plugin disabled.");

    }

    public void runAsync(Runnable run) {
        proxyServer.getScheduler()
                .buildTask(this, run)
                .schedule();
    }

    public Collection<HitBoxPlayer> getPlayers() {
        return playerCache.values();
    }

    public HitBoxPlayer getPlayer(String name) {
        if (name == null) return null;
        return getPlayer(proxyServer.getPlayer(name).orElse(null));
    }

    public HitBoxPlayer getPlayer(Player player) {
        if (player == null) return null;
        return getPlayer(player.getUniqueId());
    }

    public HitBoxPlayer getPlayer(UUID uuid) {
        return playerCache.get(uuid);
    }

}
