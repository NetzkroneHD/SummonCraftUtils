package de.netzkronehd.hitboxutils.database.cache.connection;

import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

import java.util.logging.Logger;

public class RedisConnection {

    private final String host;
    private final int port;
    private final String user;
    private final String password;
    private final String clientName;
    private final int database;
    protected final Logger log;

    protected Jedis jedis;

    public RedisConnection(String host, int port, String user, String password, String clientName, int database, Logger log) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.clientName = clientName;
        this.database = database;
        this.log = log;
    }

    public void connect() {
        final DefaultJedisClientConfig config = DefaultJedisClientConfig.builder()
                .user(user)
                .password(password)
                .clientName(clientName)
                .database(database)
                .database(database).build();

        this.jedis = new Jedis(new HostAndPort(host, port), config);
    }

    public boolean isReady() {
        return jedis != null && jedis.isConnected();
    }

    public void close() {
        this.jedis.close();
    }
}
