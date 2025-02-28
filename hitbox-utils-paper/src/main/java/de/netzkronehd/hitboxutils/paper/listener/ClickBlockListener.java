package de.netzkronehd.hitboxutils.paper.listener;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.clickblock.ClickBlock;
import de.netzkronehd.hitboxutils.paper.clickblock.ClickBlockLocation;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.paper.utils.Items;
import de.netzkronehd.hitboxutils.utils.Utils;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@AllArgsConstructor
public class ClickBlockListener implements Listener {

    private final HitBoxUtils hitBoxUtils;

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(e.getClickedBlock() == null) return;

        final HitBoxPlayer hp = hitBoxUtils.getPlayer(e.getPlayer());
        if(hp == null) return;

        if (Items.ClickBlock.SET_LOCATION.isSimilar(hp.getItemInMainHand())) {
            e.setCancelled(true);
            if (hp.getEditClickBlock() == null) {
                hp.getPlayer().getInventory().remove(Items.ClickBlock.SET_LOCATION);
                return;
            }
            hp.getEditClickBlock().setLocation(e.getClickedBlock().getLocation());
            hp.getEditClickBlock().setClickBlockLocation(ClickBlockLocation.adapt(hp.getEditClickBlock().getLocation()));
            hp.getPlayer().getInventory().remove(Items.ClickBlock.SET_LOCATION);
            hp.sendMessage("Successfully set the location for the ClickBlock§e "+hp.getEditClickBlock().getId()+"§7.");
            return;
        }

        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        final ClickBlockLocation adapt = ClickBlockLocation.adapt(e.getClickedBlock().getLocation());
        final ClickBlock clickBlock = hitBoxUtils.getClickBlockManager().getClickBlocks().get(adapt);

        if(clickBlock == null) return;
        e.setCancelled(true);

        if (!Utils.isOver(hp.getInteractTime())) return;
        hp.setInteractTime(System.currentTimeMillis() + 250);

        if (clickBlock.getPlayers().contains(hp.getUniqueId())) {
            hp.sendMessage("A has interactuado con este bloque.");
            return;
        }

        clickBlock.getPlayers().add(hp.getUniqueId());
        hitBoxUtils.getClickBlockManager().saveClickBlock(clickBlock);
        clickBlock.execute(hp);

    }



}
