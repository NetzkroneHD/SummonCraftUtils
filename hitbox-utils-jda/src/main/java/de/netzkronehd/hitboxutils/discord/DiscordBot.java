package de.netzkronehd.hitboxutils.discord;


import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class DiscordBot {

    private final String token;
    private final List<GatewayIntent> intents;
    private JDA jda;
    private String guildId;
    private boolean ready;



    public DiscordBot(String token, List<GatewayIntent> intents) {
        this.token = token;
        this.intents = intents;
    }

    public DiscordBot(String token, GatewayIntent... intents) {
        this.token = token;
        this.intents = Arrays.stream(intents).toList();
    }

    public void connect(Activity activity, OnlineStatus status, ListenerAdapter... listeners) throws InvalidTokenException, IllegalArgumentException {
        final JDABuilder jdaBuilder = JDABuilder.create(token, intents)
                .enableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                .disableCache(CacheFlag.ACTIVITY, CacheFlag.SCHEDULED_EVENTS, CacheFlag.CLIENT_STATUS, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.ONLINE_STATUS)
                .setActivity(activity)
                .setStatus(status);

        jdaBuilder.addEventListeners((Object[]) listeners);

        jda = jdaBuilder.build();
    }

    public void connect(JDABuilder builder) throws InvalidTokenException, IllegalArgumentException {
        jda = builder.build();
    }

    public void disconnect(boolean now) {
        if (now) jda.shutdownNow();
        else jda.shutdown();
    }

    @Nullable
    public Guild getGuild() {
        return jda.getGuildById(guildId);
    }

}
