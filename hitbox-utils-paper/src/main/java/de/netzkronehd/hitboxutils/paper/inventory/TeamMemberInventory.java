package de.netzkronehd.hitboxutils.paper.inventory;

import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class TeamMemberInventory extends HitBoxClickInventory {

    private final TeamInventory teamInventory;

    public TeamMemberInventory(String name, TeamInventory teamInventory) {
        super(name, 9*6);
        this.teamInventory = teamInventory;

    }

    @Override
    public void onClick(InventoryClickEvent e, HitBoxPlayer hp) {

    }

    @Override
    public void onClose(InventoryCloseEvent e, HitBoxPlayer hp) {

    }
}
