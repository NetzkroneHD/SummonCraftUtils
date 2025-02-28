package de.netzkronehd.hitboxutils.paper.clickblock;

import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.paper.utils.SpigotUtils;
import lombok.Builder;
import lombok.Data;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class ClickBlock {

    private final int id;
    private final List<ClickBlockExecuteCommand> commands;
    private final List<UUID> players;
    private Location location;
    private ClickBlockLocation clickBlockLocation;

    @Builder
    public ClickBlock(int id, Location location) {
        this.id = id;
        this.location = location;
        this.clickBlockLocation = ClickBlockLocation.adapt(location);
        this.commands = new ArrayList<>();
        this.players = new ArrayList<>();
    }

    public void execute(HitBoxPlayer hp) {
        for (ClickBlockExecuteCommand command : this.commands) {
            command.execute(hp, this);
        }
    }

    public boolean isLocationSimilar(Location location) {
        return SpigotUtils.isLocationSimilar(this.location, location);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClickBlock that)) return false;

        if (id != that.id) return false;
        return isLocationSimilar(that.location);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (location != null ? location.hashCode() : 0);
        return result;
    }
}
