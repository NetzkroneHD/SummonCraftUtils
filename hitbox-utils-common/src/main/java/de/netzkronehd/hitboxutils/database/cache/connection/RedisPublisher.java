package de.netzkronehd.hitboxutils.database.cache.connection;

import de.netzkronehd.hitboxutils.utils.Utils;

import java.util.logging.Logger;

public class RedisPublisher extends RedisConnection {

    private final String messageChannel;
    private final String messageSalt;

    public RedisPublisher(String host, int port, String user, String password, String clientName, int database, String messageChannel, Logger log, String messageSalt) {
        super(host, port, user, password, clientName, database, log);
        this.messageChannel = messageChannel;
        this.messageSalt = messageSalt;
    }

    public void publish(String message) {
        final String encoded = Utils.toBase64(message);
        this.jedis.publish(this.messageChannel, this.messageSalt+encoded);
    }

}
