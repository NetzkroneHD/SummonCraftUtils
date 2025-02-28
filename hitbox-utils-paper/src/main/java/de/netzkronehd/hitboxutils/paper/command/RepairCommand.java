package de.netzkronehd.hitboxutils.paper.command;

import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.utils.Constants;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.netzkronehd.hitboxutils.paper.utils.SpigotUtils.getDisplayName;

public class RepairCommand extends HitBoxCommand {

    public RepairCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "repair");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if(!hasCommandPermission(hp)) return;
        final ItemStack item = hp.getPlayer().getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            hp.sendMessage("Please hold an item in your hand.");
            return;
        }
        if (!(item.getItemMeta() instanceof final Damageable damageable)) {
            hp.sendMessage("You have to hold a repairable Item in your hand.");
            return;
        }
        damageable.setDamage(0);
        item.setItemMeta(damageable);
        hp.getPlayer().getInventory().setItemInMainHand(item);
        hp.playSound(Sound.BLOCK_ANVIL_USE);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if(!sender.hasPermission(Constants.PERMISSION_PREFIX+getSimpleName())) {
            sender.sendMessage(Messages.NO_PERMS.toString());
            return;
        }

        if (args.length == 1) {
            final HitBoxPlayer ht = hitBoxUtils.getPlayer(args[0]);
            if (ht == null) {
                sender.sendMessage(Messages.PLAYER_OFFLINE.toString());
                return;
            }
            final ItemStack item = ht.getPlayer().getInventory().getItemInMainHand();
            if(!repair(sender, ht, item)) return;
            ht.getPlayer().getInventory().setItemInMainHand(item);
            sender.sendMessage("§7Successfully repaired main hand§e "+ getDisplayName(item)+"§7 for§e "+ht.getName()+"§7.");
        } else if (args.length == 2) {
            final HitBoxPlayer ht = hitBoxUtils.getPlayer(args[0]);
            if (ht == null) {
                sender.sendMessage(Messages.PLAYER_OFFLINE.toString());
                return;
            }

            if (args[1].equalsIgnoreCase("main")) {
                final ItemStack item = ht.getPlayer().getInventory().getItemInMainHand();
                if(!repair(sender, ht, item)) return;
                ht.getPlayer().getInventory().setItemInMainHand(item);
                sender.sendMessage("§7Successfully repaired main hand§e " + getDisplayName(item) + "§7 for§e " + ht.getName() + "§7.");
            } else if (args[1].equalsIgnoreCase("off")) {
                final ItemStack off = ht.getPlayer().getInventory().getItemInOffHand();
                if(!repair(sender, ht, off)) return;
                ht.getPlayer().getInventory().setItemInOffHand(off);
                sender.sendMessage("§7Successfully repaired off hand§e " + getDisplayName(off) + "§7 for§e " + ht.getName() + "§7.");
            } else sendHelp(sender);
        } else sendHelp(sender);
    }

    private boolean repair(CommandSender sender, HitBoxPlayer ht, ItemStack item) {
        if (item.getType() == Material.AIR) {
            sender.sendMessage("§e" + ht.getName() + "§7 does not have an item in the main hand.");
            return false;
        }
        if (!(item.getItemMeta() instanceof final Damageable damageable)) {
            sender.sendMessage("§e" + ht.getName() + "§7 does not have an repairable item in the main hand.");
            return false;
        }
        damageable.setDamage(0);
        item.setItemMeta(damageable);
        return true;
    }

    public void sendHelp(CommandSender sender) {
        sender.sendMessage(Messages.USAGE+"repair§8 <§ePlayer§8>");
        sender.sendMessage(Messages.USAGE+"repair§8 <§ePlayer§8>§e main/off");
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        if (sender.hasPermission(Constants.PERMISSION_PREFIX + getSimpleName())) {
            if (args.length == 1) {
                return getPlayerTabComplete(args[0]);
            } else if (args.length == 2) {
                final List<String> tabs = new ArrayList<>();
                args[1] = args[1].toLowerCase();
                if("main".startsWith(args[1])) tabs.add("main");
                if("off".startsWith(args[1])) tabs.add("off");
                return tabs;
            }
        }
        return Collections.emptyList();
    }
}
