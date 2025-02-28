package de.netzkronehd.hitboxutils.paper.manager;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.logging.Level;

import static org.bukkit.configuration.file.YamlConfiguration.loadConfiguration;

public abstract class Manager {

    private static final HashMap<String, Manager> managers = new HashMap<>();
    private static int lowestPriority = 6;

    private int priority = 5;
    protected final HitBoxUtils hitBox;
    protected final File file;
    protected YamlConfiguration cfg;

    public Manager(HitBoxUtils hitBox) {
        this.hitBox = hitBox;
        file = new File("plugins/HitBox", getClass().getSimpleName() + ".yml");
        cfg = loadConfiguration(file);
        registerManager(this);
    }

    public Manager(HitBoxUtils hitBox, String fileName) {
        this.hitBox = hitBox;
        file = new File("plugins/HitBox", fileName);
        cfg = loadConfiguration(file);
        registerManager(this);
    }

    public abstract void onLoad();

    public void onReload() {
        cfg = loadConfiguration(file);
        onLoad();
    }

    public void createFile() {
    }

    public void readFile() {
    }

    public void save() {
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void log(String msg) {
        log(Level.INFO, msg);
    }

    public void log(Level level, String msg) {
        hitBox.getLogger().log(level, "(" + getClass().getSimpleName() + "): " + msg);
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
        managers.values().stream()
                .sorted(Comparator.comparingInt(Manager::getPriority))
                .forEach(manager -> loadManager(manager, beforeLoad, afterLoading));
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
        managers.values().stream()
                .sorted(Comparator.comparingInt(Manager::getPriority))
                .forEach(manager -> reloadManager(manager, beforeLoad, afterLoading));
    }

    public static void registerManager(Manager m) {
        managers.put(m.getClass().getSimpleName().toLowerCase(), m);
    }

    public static HashMap<String, Manager> getManagers() {
        return managers;
    }

    public record ManagerResponse(Manager manager, boolean success, Exception exception, long time) {
    }

}
