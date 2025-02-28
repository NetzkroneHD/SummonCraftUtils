package de.netzkronehd.hitboxutils.velocity.manager;

import de.netzkronehd.hitboxutils.discord.DiscordBoost;
import de.netzkronehd.hitboxutils.discord.DiscordBot;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.discord.listener.CommandListener;
import de.netzkronehd.hitboxutils.velocity.discord.listener.DiscordListener;
import de.netzkronehd.hitboxutils.velocity.discord.listener.VerifyListener;
import lombok.*;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

@Getter
public class DiscordManager extends Manager {

    private final List<GatewayIntent> intents;
    private final List<Runnable> onReady;

    private DiscordBot discordBot;
    private DiscordManagerConfig config;


    public DiscordManager(HitBoxUtils hitBox) {
        super(hitBox);
        setPriority(6);
        this.intents = new ArrayList<>();
        this.onReady = new ArrayList<>();
        this.config = new DiscordManagerConfig();
    }

    @Override
    public void onLoad() {
        createFile();
        readFile();
    }

    @Override
    public void createFile() {
        if (!file.exists()) {
            config.loadDefaults();
            save(config);
        }
    }

    @Override
    public void readFile() {
        config = getConfigJson(DiscordManagerConfig.class);
        intents.clear();
        for (String name : config.intents) {
            try {
                intents.add(GatewayIntent.valueOf(name.toUpperCase()));
            } catch (Exception e) {
                log(Level.SEVERE, "Could not load GatewayIntent '" + name + "': " + e);
            }
        }
        if (discordBot != null) {
            log("Disabling discord bot...");
            if(discordBot.getJda() != null) discordBot.getJda().shutdownNow();
            log("Discord bot disabled.");
        }
        discordBot = new DiscordBot(config.token, intents);
        discordBot.setReady(false);
        discordBot.setGuildId(config.guildId);

        if (config.guildId.equalsIgnoreCase("0")) {
            log(Level.WARNING, "Please provide a proper guild id.");
        }

        log("Loading DiscordBot...");
        try {
            if (!config.token.equalsIgnoreCase("token")) {
                connect();
            } else {
                log("Please provide a proper Discord-Bot-Token.");
            }
        } catch (InvalidTokenException ex) {
            log("Invalid bot token: " + ex);
        }
        config.token = null;
    }

    public void connect() throws InvalidTokenException, IllegalArgumentException {
        discordBot.connect(Activity.playing("Loading..."),
                OnlineStatus.DO_NOT_DISTURB,
                new DiscordListener(hitBox),
                new CommandListener(hitBox.getDiscordCommandManager()),
                new VerifyListener(hitBox));
    }

    public void executeWhenReady(Runnable runnable) {
        this.onReady.add(runnable);
    }

    public boolean hasDiscordBoost(long memberId) {
        try {
            return hitBox.getDatabaseManager().getDatabase().existsDiscordBoost(memberId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasUnclaimedDiscordBoosts(long memberId) {
        try {
            return hitBox.getDatabaseManager().getDatabase().hasUnclaimedDiscordBoosts(memberId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public DiscordBoost createDiscordBoost(long memberId, long timestamp) {
        try {
            return hitBox.getDatabaseManager().getDatabase().createBoost(memberId, timestamp, null, false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<DiscordBoost> getDiscordBoost(UUID uuid) {
        try {
            return hitBox.getDatabaseManager().getDatabase().getDiscordBoosts(uuid);
        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }

    public List<DiscordBoost> getUnclaimedDiscordBoost(UUID uuid) {
        try {
            return hitBox.getDatabaseManager().getDatabase().getUnclaimedDiscordBoosts(uuid);
        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }

    public List<DiscordBoost> getDiscordBoost(long memberId) {
        try {
            return hitBox.getDatabaseManager().getDatabase().getDiscordBoosts(memberId);
        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }

    public List<DiscordBoost> getUnclaimedDiscordBoost(long memberId) {
        try {
            return hitBox.getDatabaseManager().getDatabase().getUnclaimedDiscordBoosts(memberId);
        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }

    public void saveBoost(DiscordBoost boost) {
        try {
            if (hitBox.getDatabaseManager().getDatabase().existsDiscordBoost(boost.getMemberId(), boost.getTimestamp())) {
                hitBox.getDatabaseManager().getDatabase().setDiscordBoost(boost.getMemberId(), boost);
            } else {
                hitBox.getDatabaseManager().getDatabase().createBoost(boost.getMemberId(), boost.getTimestamp(), boost.getUuid(), boost.isAccepted());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isReady() {
        return discordBot != null && discordBot.isReady();
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DiscordManagerConfig extends ManagerConfig {
        private List<String> intents;
        private String token;
        private String activity;
        private String guildId;

        @Override
        public ManagerConfig loadDefaults() {
            final GatewayIntent[] intents = {
                    GatewayIntent.GUILD_INVITES,
                    GatewayIntent.DIRECT_MESSAGES,
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.GUILD_MESSAGE_REACTIONS,
                    GatewayIntent.GUILD_VOICE_STATES,
                    GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                    GatewayIntent.GUILD_MEMBERS,
                    GatewayIntent.MESSAGE_CONTENT,
                    GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                    GatewayIntent.GUILD_MESSAGE_TYPING,
                    GatewayIntent.DIRECT_MESSAGE_TYPING
            };
            this.intents = new ArrayList<>(intents.length);
            for (GatewayIntent intent : intents) {
                this.intents.add(intent.name());
            }
            this.token = "token";
            this.activity = "Type /boost";
            this.guildId = "0";

            return this;
        }
    }

}
