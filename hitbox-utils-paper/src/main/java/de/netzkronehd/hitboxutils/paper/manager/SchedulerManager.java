package de.netzkronehd.hitboxutils.paper.manager;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import lombok.Getter;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SchedulerManager extends Manager {

    private BukkitTask syncTask;

    private final List<Runnable> syncQueue;

    public SchedulerManager(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils);
        syncQueue = new ArrayList<>();
    }

    @Override
    public void onLoad() {

    }

    public void startScheduler() {
        syncTask = hitBox.getServer().getScheduler().runTaskTimer(hitBox, () -> {
            for (Runnable run : syncQueue) {
                run.run();
            }
            syncQueue.clear();
        }, 0, 1);

    }

    public synchronized void runSync(Runnable runnable) {
        syncQueue.add(runnable);
    }

}
