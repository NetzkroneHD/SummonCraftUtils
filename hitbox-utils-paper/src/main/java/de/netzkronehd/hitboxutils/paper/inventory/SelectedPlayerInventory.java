package de.netzkronehd.hitboxutils.paper.inventory;

import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.api.ItemBuilder;
import de.netzkronehd.hitboxutils.paper.manager.PunishmentManager;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.paper.utils.Items;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ConcurrentModificationException;

public class SelectedPlayerInventory extends HitBoxClickInventory {

    private final HitBoxUtils hitBoxUtils;
    private final HitBoxPlayer player;

    public SelectedPlayerInventory(HitBoxPlayer hp) {
        super("§7Player " + hp.getDisplayName(), 9 * 5);
        this.hitBoxUtils = hp.getHitBoxUtils();
        this.player = hp;
        fillBorders(ItemBuilder.GLASS);
        fillLine(ItemBuilder.GLASS, 3);
        setItem(4, new ItemBuilder(Material.PLAYER_HEAD, 1)
                .setName(hp.getPrefixAndName())
                .setSkullOwner(hp.getName())
                .setLore(
                        "",
                        "§7UUID§8 " + Messages.ARROW_RIGHT + "§e " + hp.getUniqueId(),
                        "§7Staff§8 " + Messages.ARROW_RIGHT + "§e " + hp.isStaff()
                )
                .build());
        for (Items.SortedItem item : Items.PlayerMenu.PLAYER_MENU_ITEMS) {
            setItem(item.slot(), item.item());
        }
    }

    @Override
    public void onClick(InventoryClickEvent e, HitBoxPlayer hp) {
        if (ItemBuilder.GLASS.isSimilar(e.getCurrentItem())) return;

        if (ItemBuilder.BACKWARD.isSimilar(e.getCurrentItem())) {
            hp.openInventory(hitBoxUtils.getPlayerListInventoryManager().getPlayerListInventories().get((hp.getPlayerListPage() == null ? 0 : hp.getPlayerListPage())));
            return;
        }
        if (hp.getUniqueId().equals(player.getUniqueId())) return;

        if (Items.PlayerMenu.TELEPORT.item().isSimilar(e.getCurrentItem())) {
            hp.teleport(player.getPlayer().getLocation());
        } else if (Items.PlayerMenu.SPECTATE.item().isSimilar(e.getCurrentItem())) {
            if (!hp.isSpectating()) hp.enableSpectatorMode();
            if (!hp.isVanished()) hp.vanish();
            hp.getPlayer().getInventory().setItem(Items.SpectateMode.VANISHED.slot(), Items.SpectateMode.VANISHED.item());

            hp.getPlayer().setGameMode(GameMode.SPECTATOR);
            hp.getPlayer().setSpectatorTarget(player.getPlayer());

        } else if (Items.PlayerMenu.CHEATING.item().isSimilar(e.getCurrentItem())) {
            onPunishItem(hp, PunishmentManager.Punishments.CHEATING, Items.PlayerMenu.CHEATING);

        } else if (Items.PlayerMenu.KILLAURA.item().isSimilar(e.getCurrentItem())) {
            onPunishItem(hp, PunishmentManager.Punishments.KILLAURA, Items.PlayerMenu.KILLAURA);

        } else if (Items.PlayerMenu.AUTO_TOTEM.item().isSimilar(e.getCurrentItem())) {
            onPunishItem(hp, PunishmentManager.Punishments.AUTO_TOTEM, Items.PlayerMenu.AUTO_TOTEM);

        } else if (Items.PlayerMenu.SPEED.item().isSimilar(e.getCurrentItem())) {
            onPunishItem(hp, PunishmentManager.Punishments.SPEED, Items.PlayerMenu.SPEED);

        } else if (Items.PlayerMenu.X_RAY.item().isSimilar(e.getCurrentItem())) {
            onPunishItem(hp, PunishmentManager.Punishments.X_RAY, Items.PlayerMenu.X_RAY);

        } else if (Items.PlayerMenu.AUTO_CRITICAL.item().isSimilar(e.getCurrentItem())) {
            onPunishItem(hp, PunishmentManager.Punishments.AUTO_CRITICAL, Items.PlayerMenu.AUTO_CRITICAL);

        } else if (Items.PlayerMenu.FLY.item().isSimilar(e.getCurrentItem())) {
            onPunishItem(hp, PunishmentManager.Punishments.FLY, Items.PlayerMenu.FLY);

        } else if (Items.PlayerMenu.TOXIC_BEHAVIOR.item().isSimilar(e.getCurrentItem())) {
            onPunishItem(hp, PunishmentManager.Punishments.TOXIC_BEHAVIOR, Items.PlayerMenu.TOXIC_BEHAVIOR);

        } else if (Items.PlayerMenu.FLOOD.item().isSimilar(e.getCurrentItem())) {
            onPunishItem(hp, PunishmentManager.Punishments.FLOOD, Items.PlayerMenu.FLOOD);

        } else if (Items.PlayerMenu.SPAM.item().isSimilar(e.getCurrentItem())) {
            onPunishItem(hp, PunishmentManager.Punishments.SPAM, Items.PlayerMenu.SPAM);

        } else if (Items.PlayerMenu.SLIGHT_INSULT.item().isSimilar(e.getCurrentItem())) {
            onPunishItem(hp, PunishmentManager.Punishments.SLIGHT_INSULT, Items.PlayerMenu.SLIGHT_INSULT);

        } else if (Items.PlayerMenu.SERIOUS_INSULT.item().isSimilar(e.getCurrentItem())) {
            onPunishItem(hp, PunishmentManager.Punishments.SERIOUS_INSULT, Items.PlayerMenu.SERIOUS_INSULT);

        } else if (Items.PlayerMenu.THIRD_PARTY_ADVERTISING.item().isSimilar(e.getCurrentItem())) {
            onPunishItem(hp, PunishmentManager.Punishments.THIRD_PARTY_ADVERTISING, Items.PlayerMenu.THIRD_PARTY_ADVERTISING);

        } else if (Items.PlayerMenu.FREEZE.item().isSimilar(e.getCurrentItem())) {
            if (checkStaff(hp, player)) return;

            if (player.isFrozen()) {
                player.unfreeze(hp);
            } else player.freeze(hp);

        }

    }

    @Override
    public void onClose(InventoryCloseEvent e, HitBoxPlayer hp) {

    }

    private boolean checkStaff(HitBoxPlayer hp, HitBoxPlayer tp) {
        if (tp.isStaff()) {
            hp.sendMessage("That player is a staff member.");
            return true;
        }
        return false;
    }

    public void onPunishItem(HitBoxPlayer hp, PunishmentManager.Punishments punishment, Items.SortedItem item) {
        if (checkStaff(hp, player)) return;
        final String time = hitBoxUtils.getPunishmentManager().calculatePunishment(player.getUniqueId(), punishment);

        setItem(item.slot(), Items.PlayerMenu.PUNISHED);
        hitBoxUtils.getPunishmentManager().createPunishment(player.getUniqueId(), punishment.getType(), punishment.getName());
        hitBoxUtils.getServer().dispatchCommand(hp.getPlayer(), punishment.getType().getCommand() + " " + name + " " + time + " " + punishment.getName());
    }

    public void close(String reason) {
        try {
            inventory.getViewers().forEach(humanEntity -> {
                if (humanEntity instanceof final Player p) {
                    final HitBoxPlayer tp = hitBoxUtils.getPlayer(p.getUniqueId());

                    if (tp == null) return;
                    if (tp.getPlayerListPage() == null) return;

                    tp.openInventory(hitBoxUtils.getPlayerListInventoryManager().getPlayerListInventories().get(tp.getPlayerListPage()));
                    tp.sendMessage("Closed menu for§e " + player.getDisplayName() + "§7. Reason§8:§e " + (reason == null ? "Unknown" : reason));
                }
            });
        } catch (ConcurrentModificationException ex) {
            hitBoxUtils.getLogger().warning("Error while player-closing menu: " + ex);
        }
    }
}
