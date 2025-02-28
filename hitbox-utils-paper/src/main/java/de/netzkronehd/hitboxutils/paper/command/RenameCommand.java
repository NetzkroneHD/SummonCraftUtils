package de.netzkronehd.hitboxutils.paper.command;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.api.ItemBuilder;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import org.bukkit.Material;
import org.bukkit.Sound;

import static de.netzkronehd.hitboxutils.message.HexColor.translateHexCodes;
import static de.netzkronehd.hitboxutils.utils.Utils.getArgsAsText;

public class RenameCommand extends HitBoxCommand {

    public RenameCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "rename");
    }

    @Override
    public void onExecute(HitBoxPlayer ep, String[] args) {
        if(hasCommandPermission(ep)) {
            if(args.length >= 1) {
                if(ep.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) {
                    ep.sendMessage("You have to hold an item in your main hand.");
                    return;
                }
                ep.getPlayer().getInventory().setItemInMainHand(
                        new ItemBuilder(ep.getPlayer().getInventory().getItemInMainHand())
                                .setName(translateHexCodes(getArgsAsText(args, 0)))
                                .build());
                ep.playSound(Sound.BLOCK_ANVIL_USE);
            } else sendHelp(ep);
        }
    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage(getSimpleName()+"§8 <§eName§8>");
    }
}
