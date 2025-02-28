package de.netzkronehd.hitboxutils.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class UUIDFetcher {

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeadapter()).create();

    private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s?at=%d";
    private static final String NAME_URL = "https://api.mojang.com/user/profiles/%s/names";
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    public static final Map<String, UUID> UUID_CACHE = new HashMap<>();
    public static final Map<UUID, String> NAME_CACHE = new HashMap<>();

    private String name;
    private UUID id;

    public static void getUUID(String name, Consumer<UUID> action) {
        EXECUTOR_SERVICE.execute(() -> action.accept(getUUID(name)));
    }

    public static UUID getUUID(String name) {
        return getUUIDAt(name, System.currentTimeMillis());
    }

    public static void getUUIDAt(String name, long timestamp, Consumer<UUID> action) {
        EXECUTOR_SERVICE.execute(() -> action.accept(getUUIDAt(name, timestamp)));
    }

    public static UUID getUUIDAt(String name, long timestamp) {
        final UUID uuid = UUID_CACHE.get(name.toLowerCase());
        if (uuid != null) {
            return uuid;
        }
        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL(String.format(UUID_URL, name, timestamp / 1000)).openConnection();
            connection.setReadTimeout(5000);
            final UUIDFetcher data = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDFetcher.class);

            putInCache(data.id, data.name);
            return data.id;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void getName(UUID uuid, Consumer<String> action) {
        EXECUTOR_SERVICE.execute(() -> action.accept(getName(uuid)));
    }

    public static String getName(UUID uuid) {
        if (NAME_CACHE.containsKey(uuid)) {
            return NAME_CACHE.get(uuid);
        }
        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL(String.format(NAME_URL, UUIDTypeadapter.fromUUID(uuid))).openConnection();
            connection.setReadTimeout(5000);
            final UUIDFetcher[] nameHistory = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDFetcher[].class);
            final UUIDFetcher currentNameData = nameHistory[nameHistory.length - 1];

            putInCache(uuid, currentNameData.name);
            return currentNameData.name;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void putInCache(UUID uuid, String name) {
        NAME_CACHE.put(uuid, name);
        UUID_CACHE.put(name.toLowerCase(), uuid);
    }

}
