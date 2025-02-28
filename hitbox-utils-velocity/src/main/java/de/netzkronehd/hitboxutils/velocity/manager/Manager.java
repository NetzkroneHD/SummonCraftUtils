package de.netzkronehd.hitboxutils.velocity.manager;

import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import lombok.Getter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.logging.Level;

import static de.netzkronehd.hitboxutils.utils.Utils.GSON_PRETTY;

public abstract class Manager {

    @Getter
    private static final HashMap<String, Manager> managers = new HashMap<>();
    private static int lowestPriority = 6;

    private int priority = 5;
    protected final HitBoxUtils hitBox;
    protected final File file;

    public Manager(HitBoxUtils hitBox) {
        this.hitBox = hitBox;
        file = new File(hitBox.getDataPath().toFile().getAbsolutePath(), getClass().getSimpleName() + ".json");
        registerManager(this);
    }

    public Manager(HitBoxUtils hitBox, String fileName) {
        this.hitBox = hitBox;
        file = new File(hitBox.getDataPath().toFile().getAbsolutePath(), fileName);
        registerManager(this);
    }

    public abstract void onLoad();

    public void onReload() {

        onLoad();
    }

    public void createFile() {
    }

    public void readFile() {
    }

    protected <T extends ManagerConfig> T getConfigJson(Class<T> managerConfig) {
        try {
            return GSON_PRETTY.fromJson(Files.readString(file.toPath(), StandardCharsets.UTF_8), managerConfig);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(ManagerConfig managerConfig) {
        try {
            if (!file.exists()) {
                Files.createDirectories(file.getParentFile().toPath());
                Files.createFile(file.toPath());
            }
            final BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8));
            writer.write(GSON_PRETTY.toJson(managerConfig));
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void log(String msg) {
        log(Level.INFO, msg);
    }

    public void log(String msg, Object... args) {
        log(Level.INFO, msg, args);
    }

    public void log(Level level, String msg, Object... args) {
        if (level.equals(Level.WARNING)) {
            hitBox.getLogger().warn("("+getClass().getSimpleName()+"): "+msg, args);
        } else if (level.equals(Level.SEVERE)) {
            hitBox.getLogger().error("("+getClass().getSimpleName()+"): "+msg, args);
        } else {
            hitBox.getLogger().info("("+getClass().getSimpleName()+"): "+msg, args);
        }
    }

    public void setPriority(int priority) {
        this.priority = priority;
        if (priority >= lowestPriority) lowestPriority = priority + 1;
    }

    public int getPriority() {
        return priority;
    }

    public static void loadManagers() {
        loadManagers(null, null);
    }

    public static void loadManager(Manager manager, Consumer<Manager> beforeLoad, Consumer<ManagerResponse> afterLoading) {
        final long before = System.currentTimeMillis();
        try {
            manager.log("Loading " + manager.getClass().getSimpleName() + "...");
            if (beforeLoad != null) beforeLoad.accept(manager);
            manager.onLoad();

            final long time = System.currentTimeMillis() - before;

            manager.log(manager.getClass().getSimpleName() + " loaded after " + (System.currentTimeMillis() - before) + "ms");
            if (afterLoading != null) afterLoading.accept(new ManagerResponse(manager, true, null, time));
        } catch (Exception e) {
            manager.log(Level.SEVERE, "Error while loading " + manager.getClass().getSimpleName() + ": " + e);
        }
    }

    public static void loadManagers(Consumer<Manager> beforeLoad, Consumer<ManagerResponse> afterLoading) {
        for (int i = 0; i < lowestPriority; i++) {
            for (Manager m : managers.values()) {
                if (m.getPriority() == i) {
                    loadManager(m, beforeLoad, afterLoading);
                }
            }
        }
    }

    public static void reloadManagers() {
        reloadManagers(null, null);
    }

    public static void reloadManager(Manager m, Consumer<Manager> beforeLoad, Consumer<ManagerResponse> afterLoading) {
        final long before = System.currentTimeMillis();
        try {
            m.log("Reloading " + m.getClass().getSimpleName() + "...");
            if (beforeLoad != null) beforeLoad.accept(m);
            m.onReload();
            final long time = System.currentTimeMillis() - before;
            m.log(m.getClass().getSimpleName() + " reloaded after " + time + "ms");
            if (afterLoading != null) afterLoading.accept(new ManagerResponse(m, true, null, time));
        } catch (Exception e) {
            m.log(Level.SEVERE, "Error while reloading " + m.getClass().getSimpleName() + ": " + e);
            if (afterLoading != null)
                afterLoading.accept(new ManagerResponse(m, false, e, System.currentTimeMillis() - before));
        }
    }

    public static void reloadManagers(Consumer<Manager> beforeLoad, Consumer<ManagerResponse> afterLoading) {
        for (int i = 0; i < lowestPriority; i++) {
            for (Manager m : managers.values()) {
                if (m.getPriority() == i) {
                    reloadManager(m, beforeLoad, afterLoading);
                }
            }
        }
    }

    public static void registerManager(Manager m) {
        managers.put(m.getClass().getSimpleName().toLowerCase(), m);
    }

    public abstract static class ManagerConfig {

        public abstract ManagerConfig loadDefaults();

    }

    public record ManagerResponse(Manager manager, boolean success, Exception exception, long time) {
    }

}
