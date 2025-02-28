package de.netzkronehd.hitboxutils.paper.api;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardApi {

    private final HitBoxUtils hitBoxUtils;

    private WorldGuard worldGuard;
    private WorldGuardPlugin worldGuardPlugin;
    private boolean supported;
    private Throwable throwable;

    public WorldGuardApi(HitBoxUtils hitBoxUtils) {
        this.hitBoxUtils = hitBoxUtils;
    }

    public void checkSupport() {
        try {
            this.worldGuardPlugin = WorldGuardPlugin.inst();
            this.worldGuard = WorldGuard.getInstance();
            supported = true;
        } catch (NoClassDefFoundError | Exception e) {
            supported = false;
            this.throwable = e;
        }
    }

    public boolean isSupported() {
        return supported;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public boolean isAllowedToBuild(HitBoxPlayer hp, Location loc) {
        if (supported) {
            return isAllowedToBuild(hp.getPlayer(), loc);
        } else return true;
    }

    private boolean isAllowedToBuild(Player p, Location loc) {
        final RegionQuery query = this.worldGuard.getPlatform().getRegionContainer().createQuery();
        final com.sk89q.worldedit.util.Location adaptedLocation = BukkitAdapter.adapt(loc);
        final LocalPlayer player = this.worldGuardPlugin.wrapPlayer(p);

        return query.testState(adaptedLocation, player, Flags.BLOCK_BREAK) || query.testState(adaptedLocation, player, Flags.BUILD);
    }

}
