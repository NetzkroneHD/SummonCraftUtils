package de.netzkronehd.hitboxutils.paper.synchronizer;

import de.netzkronehd.hitboxutils.database.cache.Synchronizer;
import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.RestartCancelPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.RestartPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.custom.CustomRestartCancelPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.custom.CustomRestartPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.spigot.SpigotRestartCancelPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.spigot.SpigotRestartPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.sound.GlobalSoundPacket;
import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;


@RequiredArgsConstructor
public class SpigotSynchronizer implements Synchronizer {

    private final HitBoxUtils hitBoxUtils;

    @Override
    public void preHandlePacket(HitBoxPacket packet) {
        hitBoxUtils.getLogger().info("Received packet '"+packet.getClass().getSimpleName()+"' from '"+packet.getSource()+"' with data: "+packet);
    }

    @Override
    public void handlePacket(SpigotRestartPacket spigotRestartPacket) {
        if(hitBoxUtils.getRestartManager().isRunning()) return;
        hitBoxUtils.getRestartManager().startCountdown(spigotRestartPacket.getCountdown(), true);

    }

    @Override
    public void handlePacket(SpigotRestartCancelPacket spigotRestartCancelPacket) {
        if(!hitBoxUtils.getRestartManager().isRunning()) return;
        hitBoxUtils.getRestartManager().cancel();
    }

    @Override
    public void handlePacket(RestartPacket restartPacket) {
        if(hitBoxUtils.getRestartManager().isRunning()) return;
        hitBoxUtils.getRestartManager().startCountdown(restartPacket.getCountdown(), false);
    }

    @Override
    public void handlePacket(RestartCancelPacket restartCancelPacket) {
        if(!hitBoxUtils.getRestartManager().isRunning()) return;
        hitBoxUtils.getRestartManager().cancel();
    }

    @Override
    public void handlePacket(CustomRestartPacket customRestartPacket) {
        if(!customRestartPacket.getServer().equalsIgnoreCase(hitBoxUtils.getRedisManager().getServerName())) return;
        if(hitBoxUtils.getRestartManager().isRunning()) return;
        hitBoxUtils.getRestartManager().startCountdown(customRestartPacket.getCountdown(), true);
    }

    @Override
    public void handlePacket(CustomRestartCancelPacket customRestartCancelPacket) {
        if(!customRestartCancelPacket.getServer().equalsIgnoreCase(hitBoxUtils.getRedisManager().getServerName())) return;
        if(!hitBoxUtils.getRestartManager().isRunning()) return;
        hitBoxUtils.getRestartManager().cancel();
    }

    @Override
    public void handlePacket(GlobalSoundPacket globalSoundPacket) {
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(globalSoundPacket.getUuid());
        if(hp == null) return;
        try {
            hp.playSound(Sound.valueOf(globalSoundPacket.getGlobalSound().name()), globalSoundPacket.getVolume(), globalSoundPacket.getPitch());
        } catch (IllegalArgumentException ex) {
            hitBoxUtils.getLogger().info("Could not play global sound: " + globalSoundPacket.getGlobalSound().name());
        }
    }

}
