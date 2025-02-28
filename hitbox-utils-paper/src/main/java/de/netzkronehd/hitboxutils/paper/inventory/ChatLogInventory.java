package de.netzkronehd.hitboxutils.paper.inventory;

import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.player.ChatLogEntry;
import de.netzkronehd.hitboxutils.utils.Utils;
import lombok.Getter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import static de.netzkronehd.hitboxutils.paper.api.ItemBuilder.*;

@Getter
public class ChatLogInventory extends PlayerClickInventory {

    private final int page;

    public ChatLogInventory(HitBoxPlayer player, ChatLogEntry entry, String name, int page) {
        super(player, "§e"+name+"§8 -§e "+(Utils.DATE_FORMAT.format(entry.getTimestamp()))+"§8-§e "+(page+1), 9*6);
        this.page = page;
    }

    @Override
    public void onClick(InventoryClickEvent e, HitBoxPlayer hp) {
        if (GLASS.isSimilar(e.getCurrentItem())) return;
        if (BACKWARD.isSimilar(e.getCurrentItem())) {
            hp.openInventory(hp.getChatLogInventory().get(page - 1));
        } else if (FORWARD.isSimilar(e.getCurrentItem())) {
            hp.openInventory(hp.getChatLogInventory().get(page + 1));
        }
    }

    @Override
    public void onClose(InventoryCloseEvent e, HitBoxPlayer hp) {
        if (e.getReason() == InventoryCloseEvent.Reason.OPEN_NEW) return;
        hp.getChatLogInventory().values().forEach(HitBoxInventory::clear);
        hp.getChatLogInventory().clear();
        hp.setChatLogInventory(null);
        hp.sendMessage("Closed chat logs.");
    }
}
