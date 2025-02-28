package de.netzkronehd.hitboxutils.paper.manager;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.api.ItemBuilder;
import de.netzkronehd.hitboxutils.paper.inventory.PlayerListInventory;
import de.netzkronehd.hitboxutils.paper.inventory.SelectedPlayerInventory;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.paper.utils.Items;
import lombok.Getter;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static de.netzkronehd.hitboxutils.paper.api.ItemBuilder.*;

@Getter
public class PlayerListInventoryManager extends Manager {

    private final HashMap<Integer, PlayerListInventory> playerListInventories;
    private final HashMap<UUID, SelectedPlayerInventory> playerInventories;
    private final HashMap<UUID, Long> frozenPlayers;

    public PlayerListInventoryManager(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils);
        playerListInventories = new HashMap<>();
        playerInventories = new HashMap<>();
        frozenPlayers = new HashMap<>();
    }

    @Override
    public void onLoad() {
        updatePlayerList();
    }

    @Override
    public void onReload() {
        frozenPlayers.clear();
    }

    public void updatePlayerList() {
        final List<HitBoxPlayer> sortedByName = hitBox.getPlayers().stream().sorted().toList();
        int site = 0;
        int slot = 0;

        for (PlayerListInventory value : playerListInventories.values()) {
            value.clear();
        }

        for (HitBoxPlayer ep : sortedByName) {
            PlayerListInventory inv = playerListInventories.get(site);
            if (inv == null) {
                inv = new PlayerListInventory(hitBox, site);
                playerListInventories.put(site, inv);
            }

            inv.setItem(slot, new ItemBuilder(Material.PLAYER_HEAD, 1).setName(ep.getPrefixAndName()).setSkullOwner(ep.getName()).build());
            if (slot == 35) {
                site++;
                slot = 0;
            } else slot++;
        }

        for (PlayerListInventory inv : playerListInventories.values()) {
            for (int i = 36; i <= 53; i++) {
                inv.setItem(i, GLASS);
            }
            inv.setItem(Items.PlayerList.OPEN_SETTINGS.slot(), Items.PlayerList.OPEN_SETTINGS.item());

            if (inv.getPage() == 0) {
                if (site > 0) inv.setItem(53, FORWARD);
            } else if (inv.getPage() + 1 == playerListInventories.size()) {
                inv.setItem(45, BACKWARD);
            } else {
                inv.setItem(45, BACKWARD);
                inv.setItem(53, FORWARD);
            }
        }


    }

    public int getSites() {
        return playerListInventories.size();
    }

    public int calculateSites() {
        return (hitBox.getPlayers().size() / 35) + 1;
    }

    public SelectedPlayerInventory getPlayerInventory(HitBoxPlayer ep) {
        SelectedPlayerInventory inv = playerInventories.get(ep.getUniqueId());
        if (inv == null) {
            inv = new SelectedPlayerInventory(ep);
            playerInventories.put(ep.getUniqueId(), inv);
        }
        return inv;
    }

    public void removePlayer(UUID uuid) {
        final SelectedPlayerInventory inv = playerInventories.get(uuid);
        if (inv != null) {
            inv.clear();
        }
        playerInventories.remove(uuid);
    }
}
