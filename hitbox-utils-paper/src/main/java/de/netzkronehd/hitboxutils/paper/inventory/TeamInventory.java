package de.netzkronehd.hitboxutils.paper.inventory;

import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;
import java.util.Map;

public class TeamInventory extends HitBoxClickInventory {

    private final Map<String, TeamMemberInventory> teamMemberInventories;

    public TeamInventory(String name) {
        super(name, 9*6);
        this.teamMemberInventories = new HashMap<>();
    }

    @Override
    public void onClick(InventoryClickEvent e, HitBoxPlayer hp) {

    }

    @Override
    public void onClose(InventoryCloseEvent e, HitBoxPlayer hp) {

    }
}
