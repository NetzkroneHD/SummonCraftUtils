package de.netzkronehd.hitboxutils.paper.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
@Getter
@Setter
public class SpectateMode {

    private final GameMode gameMode;
    private final boolean allowedToFly, flying;
    private final ItemStack[] inventory;
    private boolean vanished;
}
