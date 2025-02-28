package de.netzkronehd.hitboxutils.paper.manager;

import de.netzkronehd.hitboxutils.database.cache.RedisClient;
import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.synchronizer.SpigotSynchronizer;
import de.netzkronehd.hitboxutils.utils.Utils;
import lombok.Getter;

@Getter
public class RedisManager extends Manager {

    private RedisClient redisClient;
    private String serverName;

    public RedisManager(HitBoxUtils hitBox) {
        super(hitBox);
        setPriority(2);
    }

    @Override
    public void onLoad() {
        createFile();
        readFile();
    }

    @Override
    public void createFile() {
        if(!file.exists()) {
            cfg.set("host", "127.0.0.1");
            cfg.set("port", 6379);
            cfg.set("user", "user");
            cfg.set("password", "password");
            cfg.set("clientName", "clientName");
            cfg.set("database", 0);
            cfg.set("keyPrefix", "hitboxutils.");
            cfg.set("messageChannel", "hitboxutils");
            cfg.set("messageSalt", Utils.getRandomString(20));
            cfg.set("serverName", "lobby1");
            save();
        }
    }

    @Override
    public void readFile() {
        if (this.redisClient != null) {
            this.redisClient.close();
        }
        this.serverName = cfg.getString("serverName", "lobby");


        this.redisClient = RedisClient.builder()
                .host(cfg.getString("host", "127.0.0.1"))
                .port(cfg.getInt("port", 6379))
                .user(getNullOrString(cfg.getString("user")))
                .password(getNullOrString(cfg.getString("password")))
                .clientName(getNullOrString(cfg.getString("clientName")))
                .database(cfg.getInt("database", 0))
                .keyPrefix(cfg.getString("keyPrefix", "hitboxutils."))
                .messageChannel(cfg.getString("messageChannel", "hitboxutils"))
                .messageSalt(cfg.getString("messageSalt", "default-message-salt"))
                .logger(hitBox.getLogger())
                .synchronizer(new SpigotSynchronizer(hitBox))
                .build();

        hitBox.runAsync(() -> redisClient.getRedisCache().connect());
        hitBox.runAsync(() -> redisClient.getRedisPublisher().connect());
        hitBox.runAsync(() -> redisClient.getRedisSubscriber().connect());
    }

    public void runAfterReady(Runnable run) {
        redisClient.runAfterReady(run);
    }

    public void sendPacket(HitBoxPacket packet) {
        this.redisClient.sendPacket(packet);
    }

    private String getNullOrString(String s) {
        if ("null".equals(s)) return null;
        return s;
    }
}
