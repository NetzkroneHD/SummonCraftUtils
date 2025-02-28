package de.netzkronehd.hitboxutils.paper.command;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.api.ItemBuilder;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnchantCommand extends HitBoxCommand {

    public EnchantCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "enchant");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if (!hasCommandPermission(hp)) return;
        if (args.length == 2) {
            try {
                final Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(args[0]));
                if (enchantment == null) {
                    hp.sendMessage("Could not find the Enchantment§e " + args[0] + "§7.");
                    return;
                }
                final ItemStack item = hp.getPlayer().getInventory().getItemInMainHand();
                if(item.getType() == Material.AIR) {
                    hp.sendMessage("Please hold an item in your hand.");
                    return;
                }
                final int level = Integer.parseInt(args[1]);
                hp.getPlayer().getInventory().setItemInMainHand(new ItemBuilder(item).enchant(enchantment, level).build());
                hp.playSound(Sound.BLOCK_ANVIL_USE);
            } catch (NumberFormatException ex) {
                hp.sendMessage("Please use a number.");
            }
        } else sendHelp(hp);
    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("enchant§8 <§eEnchantment§8> <§eLevel§8>");
    }

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if (hasPermission(hp)) {
            if (args.length == 1) {
                final List<String> tabs = new ArrayList<>();
                args[0] = args[0].toLowerCase();
                for (Enchantment value : Enchantment.values()) {
                    if (value.getKey().getKey().toLowerCase().startsWith(args[0])) {
                        tabs.add(value.getKey().getKey());
                    }
                }
                return tabs;
            }
        }
        return Collections.emptyList();
    }
}
