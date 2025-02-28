package de.netzkronehd.hitboxutils.velocity.manager;

import de.netzkronehd.hitboxutils.database.cache.RedisClient;
import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import de.netzkronehd.hitboxutils.utils.Utils;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.velocity.synchronizer.VeloSynchronizer;
import lombok.*;

import java.util.UUID;
import java.util.logging.Logger;

import static de.netzkronehd.hitboxutils.velocity.utils.VeloUtils.mapUserModel;

@Getter
public class RedisManager extends Manager {

    private RedisClient redisClient;

    private RedisManagerConfig config;

    public RedisManager(HitBoxUtils hitBox) {
        super(hitBox);
        setPriority(2);
        config = new RedisManagerConfig();
    }

    @Override
    public void onLoad() {
        createFile();
        readFile();
    }

    @Override
    public void createFile() {
        if(!file.exists()) {
            config = new RedisManagerConfig().loadDefaults();
            save(config);
        }
    }

    @Override
    public void readFile() {
        if (this.redisClient != null) {
            this.redisClient.close();
        }
        config = getConfigJson(RedisManagerConfig.class);

        this.redisClient = RedisClient.builder()
                .host(config.host)
                .port(config.port)
                .user(getNullOrString(config.user))
                .password(getNullOrString(config.password))
                .clientName(getNullOrString(config.clientName))
                .database(config.database)
                .keyPrefix(config.keyPrefix)
                .messageChannel(config.messageChannel)
                .messageSalt(config.messageSalt)
                .logger(Logger.getLogger(hitBox.getClass().getName()))
                .synchronizer(new VeloSynchronizer(hitBox))
                .build();

        hitBox.runAsync(() -> redisClient.getRedisCache().connect());
        hitBox.runAsync(() -> redisClient.getRedisPublisher().connect());
        hitBox.runAsync(() -> redisClient.getRedisSubscriber().connect());

        config = new RedisManagerConfig(null, 0, null, null, null, 0, null, null, null, config.serverName);
    }

    private String getNullOrString(String s) {
        if ("null".equals(s)) return null;
        return s;
    }

    public void setTeamUser(HitBoxPlayer hp) {
        this.redisClient.setTeamUser(mapUserModel(hp));
    }

    public void removeTeamUser(UUID uuid) {
        this.redisClient.removeTeamUser(uuid);
    }

    public void sendPacket(HitBoxPacket packet) {
        this.redisClient.sendPacket(packet);
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RedisManagerConfig extends ManagerConfig {

        private String host;
        private int port;
        private String user;
        private String password;
        private String clientName;
        private int database;
        private String keyPrefix;
        private String messageChannel;
        private String messageSalt;
        private String serverName;

        @Override
        public RedisManagerConfig loadDefaults() {
            host = "127.0.0.1";
            port = 6379;
            user = "user";
            password = "password";
            clientName = "clientName";
            database = 0;
            keyPrefix = "hitboxutils.";
            messageChannel = "hitboxutils";
            messageSalt = Utils.getRandomString(20);
            serverName = "Velocity1";
            return this;
        }
    }

}
