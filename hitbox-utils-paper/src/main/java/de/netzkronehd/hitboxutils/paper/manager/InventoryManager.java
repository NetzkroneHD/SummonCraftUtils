package de.netzkronehd.hitboxutils.paper.manager;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.inventory.HitBoxClickInventory;

import java.util.HashMap;

public class InventoryManager extends Manager {

    public static final HashMap<String, HitBoxClickInventory> INVENTORIES = new HashMap<>();

    public InventoryManager(HitBoxUtils hitBox) {
        super(hitBox);
    }

    @Override
    public void onLoad() {

    }
}
