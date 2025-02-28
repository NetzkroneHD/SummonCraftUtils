package de.netzkronehd.hitboxutils.paper.manager;

import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.api.ItemBuilder;
import de.netzkronehd.hitboxutils.paper.inventory.ChatLogInventory;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.player.ChatLogEntry;
import de.netzkronehd.hitboxutils.utils.Utils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.*;

import static de.netzkronehd.hitboxutils.paper.api.ItemBuilder.*;

@Getter
public class ChatManager extends Manager {

    private int maxEntries;

    public ChatManager(HitBoxUtils hitBox) {
        super(hitBox);
    }

    @Override
    public void onLoad() {
        createFile();
        readFile();
    }

    @Override
    public void createFile() {
        if (!file.exists()) {
            cfg.set("max-entries", 350);
        }
    }

    @Override
    public void readFile() {
        this.maxEntries = cfg.getInt("max-entries", 350);
    }

    public void openChatLogs(HitBoxPlayer hp, List<ChatLogEntry> chatLogs, String name) {
        if (chatLogs.size() > maxEntries) {
            hp.sendMessage("Can't show§e "+chatLogs.size()+"§7 chat logs, please specify the filter.");
            return;
        }
        if (hp.getChatLogInventory() != null) {
            hp.getChatLogInventory().forEach((integer, chatLogInventory) -> {
                chatLogInventory.clear();
                hp.getInventories().remove(chatLogInventory.getName());
            });
            hp.getChatLogInventory().clear();
        }
        hp.setChatLogInventory(new HashMap<>());

        int site = 0;
        int slot = 0;
        for (ChatLogEntry chatLog : chatLogs) {
            ChatLogInventory inv = hp.getChatLogInventory().get(site);
            if (inv == null) {
                inv = new ChatLogInventory(hp, chatLogs.get(0), name, site);
                hp.getChatLogInventory().put(site, inv);
            }

            inv.setItem(slot, getDisplayItem(chatLog));

            if (slot == 35) {
                site++;
                slot = 0;
            } else slot++;
        }

        for (ChatLogInventory inv : hp.getChatLogInventory().values()) {
            inv.fillLine(GLASS, 4, 5);
            
            if (inv.getPage() == 0) {
                if (site > 0) inv.setItem(53, FORWARD);
            } else if (inv.getPage() + 1 == hp.getChatLogInventory().size()) {
                inv.setItem(45, BACKWARD);
            } else {
                inv.setItem(45, BACKWARD);
                inv.setItem(53, FORWARD);
            }
        }
        hp.openInventory(hp.getChatLogInventory().get(0));

    }

    public void createChatLog(UUID uuid, String name, long timestamp, String server, String message) {
        try {
            hitBox.getDatabaseManager().getDatabase().createChatLog(uuid, name, timestamp, server, message);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ChatLogEntry> getChatLogsFindMessages(UUID uuid, String message) {
        try {
            return hitBox.getDatabaseManager().getDatabase().getChatLogsFindMessages(uuid, message.toLowerCase());
        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }

    public List<ChatLogEntry> getChatLogsFindMessages(String server, String message) {
        try {
            return hitBox.getDatabaseManager().getDatabase().getChatLogsFindMessages(server, message.toLowerCase());
        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }

    public List<ChatLogEntry> getChatLogsBetweenTime(String server, long from, long to) {
        try {
            return hitBox.getDatabaseManager().getDatabase().getChatLogsBetweenTime(server.toLowerCase(), from, to);
        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }

    public List<ChatLogEntry> getChatLogsBetweenTime(UUID uuid, long from, long to) {
        try {
            return hitBox.getDatabaseManager().getDatabase().getChatLogsBetweenTime(uuid, from, to);
        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }

    public ChatLogEntry getChatLog(UUID uuid, long timestamp) {
        try {
            return hitBox.getDatabaseManager().getDatabase().getChatLog(uuid, timestamp);
        } catch (SQLException e) {
            return null;
        }
    }

    private ItemStack getDisplayItem(ChatLogEntry entry) {

        final List<String> lore = new ArrayList<>(Arrays.asList(
                "",
                "§7UUID§8 " + Messages.ARROW_RIGHT + "§e " + entry.getUuid(),
                "§7Server§8 " + Messages.ARROW_RIGHT + "§e " + entry.getServer(),
                "§7Time§8 " + Messages.ARROW_RIGHT + "§e " + Utils.DATE_FORMAT.format(new Date(entry.getTimestamp())),
                "§7Message§8:"
        ));

        final List<String> lines = Utils.getMessageLines(entry.getMessage(), 5);
        for (String line : lines) {
            lore.add("§8"+Messages.ARROW_RIGHT + "§e "+line);
        }
        lore.add("");
        return ItemBuilder.builder(Material.PLAYER_HEAD)
                .setSkullOwner(entry.getName())
                .setName("§e"+entry.getName())
                .setLore(lore.toArray(new String[0]))
                .build();
    }
}
