package de.netzkronehd.hitboxutils.paper.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.netzkronehd.hitboxutils.database.cache.model.MineModel;
import de.netzkronehd.hitboxutils.database.cache.packet.mine.MineMessagePacket;
import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import lombok.Getter;
import org.bukkit.Material;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import static de.netzkronehd.hitboxutils.paper.utils.SpigotUtils.mapUserModel;

public class MineDetectionManager extends Manager {

    private final Map<UUID, Cache<Material, AtomicInteger>> mineCache;

    private final Map<Material, MineModel> mineModels;
    @Getter
    private boolean enabled;

    public MineDetectionManager(HitBoxUtils hitBox) {
        super(hitBox);
        this.mineCache = new HashMap<>();
        this.mineModels = new HashMap<>();
    }

    @Override
    public void onLoad() {
        createFile();
        readFile();
    }

    @Override
    public void createFile() {
        if (!file.exists()) {
            cfg.set("enabled", false);
            cfg.set(Material.DIAMOND_ORE.name() + ".duration-minutes", "15");
            cfg.set(Material.DIAMOND_ORE.name() + ".broadcast-mines", "10");
            save();
        }
    }

    @Override
    public void readFile() {
        mineCache.values().forEach(Cache::invalidateAll);
        mineCache.clear();
        mineModels.clear();

        this.enabled = cfg.getBoolean("enabled", false);
        if(!this.enabled) {
            log("Mine detections is disabled.");
            return;
        }

        for (String materialName : cfg.getKeys(false)) {
            if(materialName.equalsIgnoreCase("enabled")) continue;
            try {
                final Material material = Material.valueOf(materialName.toUpperCase());
                final int mines = cfg.getInt(materialName+".broadcast-mines", 10);
                final int duration = cfg.getInt(materialName+".duration-minutes", 15);
                mineModels.put(material, new MineModel(material.name(), mines, duration));
            } catch (IllegalArgumentException ex) {
                log(Level.WARNING, "Could not load material '"+materialName+"': "+ex);
            }
        }
        log("Loaded "+mineModels.size()+" mine detections.");
    }

    public void addDetection(HitBoxPlayer hp, Material material) {
        final MineModel mineModel = mineModels.get(material);
        if(mineModel == null) return;

        Cache<Material, AtomicInteger> materialCache = this.mineCache.get(hp.getUniqueId());
        if (materialCache == null) {
            materialCache = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofMinutes(mineModel.minutes())).build();
            this.mineCache.put(hp.getUniqueId(), materialCache);
        }

        try {
            final int mines = materialCache.get(material, () -> new AtomicInteger(0)).addAndGet(1);
            if (mines < mineModel.mines() || mines % mineModel.mines() != 0) return;
            this.hitBox.getRedisManager().sendPacket(new MineMessagePacket(hitBox.getRedisManager().getServerName(), mapUserModel(hp), new MineModel(material.name(), mines, mineModel.minutes())));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeDetection(UUID uuid) {
        final Cache<Material, AtomicInteger> cache = this.mineCache.get(uuid);
        if (cache != null) {
            cache.invalidateAll();
        }
        this.mineCache.remove(uuid);
    }

}
