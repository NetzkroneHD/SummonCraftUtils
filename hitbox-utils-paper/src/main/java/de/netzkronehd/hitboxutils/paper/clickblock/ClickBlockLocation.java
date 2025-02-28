package de.netzkronehd.hitboxutils.paper.clickblock;

import lombok.Builder;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.World;

@Data
@Builder
public class ClickBlockLocation {

    private final int x, y, z;
    private final World world;

    public static ClickBlockLocation adapt(Location location) {
        if(location == null) return null;
        return new ClickBlockLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld());
    }

}
