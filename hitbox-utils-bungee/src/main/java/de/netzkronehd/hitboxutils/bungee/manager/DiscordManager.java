package de.netzkronehd.hitboxutils.bungee.manager;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.discord.listener.CommandListener;
import de.netzkronehd.hitboxutils.bungee.discord.listener.DiscordListener;
import de.netzkronehd.hitboxutils.bungee.discord.listener.TeamChatMessageListener;
import de.netzkronehd.hitboxutils.bungee.discord.listener.VerifyListener;
import de.netzkronehd.hitboxutils.discord.DiscordBoost;
import de.netzkronehd.hitboxutils.discord.DiscordBot;
import lombok.Getter;
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
    private String token;
    private String activity;
    private String guildId;


    public DiscordManager(HitBoxUtils hitBox) {
        super(hitBox);
        setPriority(6);
        this.intents = new ArrayList<>();
        this.onReady = new ArrayList<>();
    }

    @Override
    public void onLoad() {
        createFile();
        readFile();
    }

    @Override
    public void createFile() {
        if (!file.exists()) {
            cfg.set("token", "token");
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
            final List<String> intentsName = new ArrayList<>();
            for (GatewayIntent intent : intents) {
                intentsName.add(intent.name());
            }
            cfg.set("Activity", "Type /boost");
            cfg.set("Gateway-Intents", intentsName);
            cfg.set("Guild-Id", "0");

            save();
        }
    }

    @Override
    public void readFile() {
        intents.clear();
        token = cfg.getString("token", "token");
        for (String name : cfg.getStringList("Gateway-Intents")) {
            try {
                intents.add(GatewayIntent.valueOf(name.toUpperCase()));
            } catch (Exception e) {
                log(Level.SEVERE, "Could not load GatewayIntent '" + name + "': " + e);
            }
        }
        activity = cfg.getString("Activity", "/verify /deleteverify");

        if (discordBot != null) {
            log("Disabling discord bot...");
            if(discordBot.getJda() != null) discordBot.getJda().shutdownNow();
            log("Discord bot disabled.");
        }
        discordBot = new DiscordBot(token, intents);
        discordBot.setReady(false);
        this.guildId = cfg.getString("Guild-Id", "0");
        discordBot.setGuildId(this.guildId);

        if (this.guildId.equalsIgnoreCase("0")) {
            log(Level.WARNING, "Please provide a proper guild id.");
        }

        log("Loading DiscordBot...");
        try {
            if (!token.equalsIgnoreCase("token")) {
                connect();
            } else {
                log("Please provide a proper Discord-Bot-Token.");
            }
        } catch (InvalidTokenException ex) {
            log("Invalid bot token: " + ex);
        }

    }

    public void connect() throws InvalidTokenException, IllegalArgumentException {
        discordBot.connect(Activity.playing("Loading..."),
                OnlineStatus.DO_NOT_DISTURB,
                new DiscordListener(hitBox),
                new CommandListener(hitBox.getDiscordCommandManager()),
                new VerifyListener(hitBox),
                new TeamChatMessageListener(hitBox));
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
}
