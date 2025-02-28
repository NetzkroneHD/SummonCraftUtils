package de.netzkronehd.hitboxutils.bungee.manager;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.bungee.synchronizer.BungeeSynchronizer;
import de.netzkronehd.hitboxutils.database.cache.RedisClient;
import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import de.netzkronehd.hitboxutils.utils.Utils;
import lombok.Getter;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static de.netzkronehd.hitboxutils.bungee.utils.BungeeUtils.mapTeamUserModel;

@Getter
public class RedisManager extends Manager {

    private RedisClient redisClient;
    private String serverName;

    private Timer timer;

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
            cfg.set("serverName", "proxy1");
            save();
        }
    }

    @Override
    public void readFile() {
        if (this.redisClient != null) {
            this.redisClient.close();
        }
        if(timer != null) {
            timer.cancel();
        }

        this.serverName = cfg.getString("serverName", "proxy1");


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
                .synchronizer(new BungeeSynchronizer(hitBox))
                .build();

        hitBox.runAsync(() -> redisClient.getRedisCache().connect());
        hitBox.runAsync(() -> redisClient.getRedisPublisher().connect());
        hitBox.runAsync(() -> redisClient.getRedisSubscriber().connect());

        timer = new Timer();

        runAfterReady(() -> timer.schedule(new TimerTask() {
            @Override
            public void run() {
                hitBox.getRedisManager().getRedisClient().getTeamUsers().stream()
                        .filter(teamUserModel -> hitBox.getPlayer(teamUserModel.uuid()) == null && serverName.equalsIgnoreCase(teamUserModel.proxy()))
                        .forEach(teamUserModel -> {
                            hitBox.getRedisManager().removeTeamUser(teamUserModel.uuid());
                            log("Removed team user '"+teamUserModel.uuid()+"'.");
                        });
            }
        }, TimeUnit.MINUTES.toHours(30)));

    }

    public void runAfterReady(Runnable run) {
        redisClient.runAfterReady(run);
    }

    private String getNullOrString(String s) {
        if ("null".equals(s)) return null;
        return s;
    }

    public void setTeamUser(HitBoxPlayer hp) {
        this.redisClient.setTeamUser(mapTeamUserModel(hp));
    }

    public void setTeamUser(HitBoxPlayer hp, String server) {
        this.redisClient.setTeamUser(mapTeamUserModel(hp, server));
    }

    public void removeTeamUser(UUID uuid) {
        this.redisClient.removeTeamUser(uuid);
    }

    public void sendPacket(HitBoxPacket packet) {
        this.redisClient.sendPacket(packet);
    }

}
