package de.netzkronehd.hitboxutils.database.cache;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import de.netzkronehd.hitboxutils.database.cache.connection.RedisCache;
import de.netzkronehd.hitboxutils.database.cache.connection.RedisPublisher;
import de.netzkronehd.hitboxutils.database.cache.connection.RedisSubscriber;
import de.netzkronehd.hitboxutils.database.cache.handler.CustomMessageHandler;
import de.netzkronehd.hitboxutils.database.cache.model.SupportChatModel;
import de.netzkronehd.hitboxutils.database.cache.model.TeamUserModel;
import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.PacketContainer;
import lombok.Builder;
import lombok.Getter;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static de.netzkronehd.hitboxutils.database.cache.RedisClient.RedisKeys.SUPPORT_CHATS;
import static de.netzkronehd.hitboxutils.database.cache.RedisClient.RedisKeys.TEAM_MEMBERS;

@Getter
public class RedisClient {

    public static final Gson GSON = new Gson();

    private final RedisCache redisCache;
    private final RedisSubscriber redisSubscriber;
    private final RedisPublisher redisPublisher;
    private final String keyPrefix;

    @Builder
    public RedisClient(String host, int port, String user, String password, String clientName, int database, String keyPrefix, String messageChannel, String messageSalt, Synchronizer synchronizer, Logger logger) {
        this.redisCache = new RedisCache(host, port, user, password, clientName, database, logger, keyPrefix);
        this.redisSubscriber = new RedisSubscriber(host, port, user, password, clientName, database, messageChannel, messageSalt, synchronizer, logger);
        this.redisPublisher = new RedisPublisher(host, port, user, password, clientName, database, messageChannel, logger, messageSalt);
        this.keyPrefix = keyPrefix;
    }

    public void close() {
        this.redisCache.close();
        this.redisSubscriber.close();
        this.redisPublisher.close();
    }

    public void setSupportChat(SupportChatModel supportChat) {
        set(SUPPORT_CHATS, supportChat.owner().toString(), supportChat);
    }

    public Optional<SupportChatModel> getSupportChat(UUID uuid) {
        return get(SUPPORT_CHATS, uuid.toString(), SupportChatModel.class);
    }

    public Set<SupportChatModel> getSupportChats() {
        final Set<SupportChatModel> supportChats = new HashSet<>();
        final Set<String> uuids = keys(SUPPORT_CHATS);

        uuids.stream().map(key -> replacePrefix(SUPPORT_CHATS, key)).forEach(s -> {
            try {
                final UUID uuid = UUID.fromString(s);
                getSupportChat(uuid).ifPresent(supportChats::add);
            } catch (IllegalArgumentException ignored) {}
        });

        return supportChats;
    }

    public void setTeamUser(TeamUserModel user) {
        set(TEAM_MEMBERS, user.uuid().toString(), user);
    }

    public Optional<TeamUserModel> getTeamUser(UUID uuid) {
        return get(TEAM_MEMBERS, uuid.toString(), TeamUserModel.class);
    }

    public void removeTeamUser(UUID uuid) {
        this.redisCache.remove(TEAM_MEMBERS, uuid.toString());
    }

    public Set<TeamUserModel> getTeamUsers() {
        final Set<TeamUserModel> team = new HashSet<>();
        final Set<String> uuids = keys(TEAM_MEMBERS);
        uuids.stream().map(s -> replacePrefix(TEAM_MEMBERS, s)).forEach(s -> {
            try {
                final UUID uuid = UUID.fromString(s);
                getTeamUser(uuid).ifPresent(team::add);
            } catch (IllegalArgumentException ignored) {}
        });
        return team;
    }

    public void sendPacket(HitBoxPacket packet) {
        final PacketContainer packetContainer = new PacketContainer(packet.getClass(), packet);
        publish(RedisKeys.PACKET_PREFIX+packetContainer.toMessage());
    }

    public void publish(String message) {
        this.redisPublisher.publish(message);
    }

    public void addCustomMessageHandler(CustomMessageHandler customMessageHandler) {
        this.redisSubscriber.addMessageHandler(customMessageHandler);
    }

    public void set(String key, Object value) {
        this.redisCache.set(key, value);
    }

    public void set(String key, Object value, Type type) {
        this.redisCache.set(key, value, type);
    }

    public void set(String prefix, String key, Object value) {
        this.redisCache.set(prefix, key, value);
    }

    public void set(String prefix, String key, Object value, Type type) {
        this.redisCache.set(prefix, key, value, type);
    }

    public <T> Optional<T> get(String key, Class<T> classOfT) {
        return this.redisCache.get(key, classOfT);
    }

    public <T> Optional<T> get(String key, Type type) {
        return this.redisCache.get(key, type);
    }

    public Optional<String> get(String key) {
        return this.redisCache.get(key);
    }

    public Optional<String> get(String prefix, String key) {
        return this.redisCache.get(prefix, key);
    }

    public <T> Optional<T> get(String prefix, String key, Class<T> classOfT) {
        return this.redisCache.get(prefix, key, classOfT);
    }

    public <T> Optional<T> get(String prefix, String key, Type type) {
        return this.redisCache.get(prefix, key, type);
    }

    public Set<String> keys(String prefix) {
        return this.redisCache.keys(prefix);
    }

    public boolean isReady() {
        return this.redisCache.isReady() && this.redisPublisher.isReady() && this.redisSubscriber.isReady();
    }

    public void awaitReady() {
        final AtomicBoolean ready = new AtomicBoolean(isReady());
        do {
            ready.set(isReady());

        } while (!ready.get());
    }

    public void runAfterReady(Runnable run) {
        new Thread(() -> {
            final AtomicBoolean atomicBoolean = new AtomicBoolean(true);
            while (atomicBoolean.get()) {
                atomicBoolean.set(!this.isReady());
            }
            run.run();
        }).start();
    }

    private String replacePrefix(String prefix, String fullKey) {
        return fullKey.replace(keyPrefix+prefix+":", "");
    }

    public static class RedisKeyTypes {
        public static final Type TEAM_USER_LIST = new TypeToken<ArrayList<TeamUserModel>>(){}.getType();
    }

    public static class RedisKeys {
        public static final String PACKET_PREFIX = "hit_box_packet ";
        public static final String TEAM_MEMBERS = "team_members";
        public static final String SUPPORT_CHATS = "support_chats";
        public static final String USER_INFOS = "user_infos";
    }

}
