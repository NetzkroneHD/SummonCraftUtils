package de.netzkronehd.hitboxutils.database.cache.connection;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import static de.netzkronehd.hitboxutils.database.cache.RedisClient.GSON;

public class RedisCache extends RedisConnection {

    private final String keyPrefix;

    public RedisCache(String host, int port, String user, String password, String clientName, int database, Logger log, String keyPrefix) {
        super(host, port, user, password, clientName, database, log);
        this.keyPrefix = keyPrefix;
    }

    public void set(String key, Object value) {
        this.jedis.set(keyPrefix+key, GSON.toJson(value));
    }

    public void set(String key, Object value, Type type) {
        this.jedis.set(keyPrefix+key, GSON.toJson(value, type));
    }

    public void set(String prefix, String key, Object value) {
        set(prefix+":"+key, value);
    }

    public void set(String prefix, String key, Object value, Type type) {
        set(prefix+":"+key, value, type);
    }

    public Set<String> keys(String prefix) {
        return this.jedis.keys(keyPrefix+prefix+":*");
    }

    public void remove(String key) {
        this.jedis.del(keyPrefix+key);
    }

    public void remove(String prefix, String key) {
        remove(prefix+":"+key);
    }

    public <T> Optional<T> get(String key, Class<T> classOfT) {
        final String json = jedis.get(keyPrefix+key);
        if(json == null) return Optional.empty();
        return Optional.of(GSON.fromJson(json, classOfT));
    }

    public <T> Optional<T> get(String key, Type type) {
        final String json = jedis.get(keyPrefix+key);
        if(json == null) return Optional.empty();
        return Optional.of(GSON.fromJson(json, type));
    }

    public Optional<String> get(String key) {
        final String json = jedis.get(keyPrefix+key);
        if(json == null) return Optional.empty();
        return Optional.of(json);
    }

    public Optional<String> get(String prefix, String key) {
        return get(prefix+":"+key);
    }

    public <T> Optional<T> get(String prefix, String key, Class<T> classOfT) {
        return get(prefix+":"+key, classOfT);
    }

    public <T> Optional<T> get(String prefix, String key, Type type) {
        return get(prefix+":"+key, type);
    }

}
