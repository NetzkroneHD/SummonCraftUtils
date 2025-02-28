package de.netzkronehd.hitboxutils.paper.manager;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import lombok.Getter;
import org.bukkit.scheduler.BukkitTask;

@Getter
public class AfkManager extends Manager {

    private BukkitTask afkTimer;

    public AfkManager(HitBoxUtils hitBox) {
        super(hitBox);
    }

    @Override
    public void onLoad() {

    }

    public void startTimer() {
        afkTimer = hitBox.getServer().getScheduler().runTaskTimer(hitBox, () ->
                hitBox.getPlayerCache().values().stream().filter(hp -> (!hp.isStaff() || !hp.hasPermission("afk.bypass"))).forEach(hp ->
                        hp.setAfkTime(hp.getAfkTime()+1)), 0,20);
    }

}
