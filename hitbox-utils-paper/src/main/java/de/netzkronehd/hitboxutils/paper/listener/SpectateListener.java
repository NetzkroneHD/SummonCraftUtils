package de.netzkronehd.hitboxutils.paper.listener;

import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.inventory.PlayerListInventory;
import de.netzkronehd.hitboxutils.paper.inventory.SpectateInventory;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.paper.utils.Items;
import de.netzkronehd.hitboxutils.paper.utils.SpigotUtils;
import de.netzkronehd.hitboxutils.utils.Utils;
import de.netzkronehd.translation.sender.Sender;
import lombok.AllArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.Container;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffectType;

import static de.netzkronehd.translation.Message.formatColoredValue;

@AllArgsConstructor
public class SpectateListener implements Listener {

    private final HitBoxUtils hitBoxUtils;

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent e) {
        if (!(e.getTarget() instanceof final Player p)) return;
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(p.getUniqueId());
        if (hp == null) return;
        if (!hp.isSpectating()) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockChange(EntityChangeBlockEvent e) {
        if (!(e.getEntity() instanceof final Player p)) return;
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(p.getUniqueId());
        if (hp == null) return;
        if (!hp.isSpectating()) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(e.getPlayer().getUniqueId());
        if (hp == null) return;
        if (!hp.isSpectating()) return;

        hp.getPlayer().setAllowFlight(true);
        hp.getPlayer().setFlying(true);


    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEffect(EntityPotionEffectEvent e) {
        if(e.getAction() != EntityPotionEffectEvent.Action.ADDED) return;
        if(!(e.getEntity() instanceof final Player p)) return;

        final HitBoxPlayer hp = hitBoxUtils.getPlayer(p);
        if(hp == null) return;
        if(!hp.isSpectating()) return;
        if(e.getNewEffect() == null) return;
        if(e.getNewEffect().getType().getName().equals(PotionEffectType.NIGHT_VISION.getName())) {
            e.setCancelled(false);
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractAtEntityEvent e) {
        if (!(e.getRightClicked() instanceof final Player p)) return;
        final HitBoxPlayer tp = hitBoxUtils.getPlayer(p);
        if(tp == null) return;
        if(tp.isSpectating()) return;
        if(tp.isVanished()) return;

        final HitBoxPlayer hp = hitBoxUtils.getPlayer(e.getPlayer());
        if (hp == null) return;
        if (!hp.isSpectating()) return;
        if (!hp.getPlayer().getInventory().getItemInMainHand().getType().isAir()) return;

        e.setCancelled(true);
        hp.getPlayer().setGameMode(GameMode.SPECTATOR);
        hp.getPlayer().setSpectatorTarget(p);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(e.getPlayer().getUniqueId());
        if (hp == null) return;
        if (!hp.isSpectating()) return;
        if (hp.getPlayer().getSpectatorTarget() != null) {
            hp.getPlayer().setSpectatorTarget(null);
            hp.getPlayer().setGameMode(GameMode.ADVENTURE);
            hp.getPlayer().setAllowFlight(true);
            hp.getPlayer().setFlying(true);
            return;
        }
        if (!e.isSneaking()) return;
        if (hp.getSneakTime() == null) {
            hp.setSneakTime(System.currentTimeMillis());
            return;
        }
        long time = System.currentTimeMillis() - hp.getSneakTime();
        if (time < 2000) {
            if (hp.getPlayer().getGameMode() == GameMode.ADVENTURE && hp.getStaffSettings() != null && hp.getStaffSettings().isDoubleSneakSpectator()) {
                hp.getPlayer().setGameMode(GameMode.SPECTATOR);
            } else if(hp.getPlayer().getGameMode() == GameMode.SPECTATOR){
                hp.getPlayer().setGameMode(GameMode.ADVENTURE);
                hp.getPlayer().setAllowFlight(true);
                hp.getPlayer().setFlying(true);
            }
        }
        hp.setSneakTime(null);
    }

    @EventHandler
    public void onSpitDamage(EntityDamageByEntityEvent e) {
        if(!(e.getDamager() instanceof final LlamaSpit spit)) return;
        if(!Messages.PREFIX.toString().equals(spit.getCustomName())) return;
        e.setCancelled(true);


    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof final Player p)) return;
        if (!(e.getEntity() instanceof Player t)) return;
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(p);
        if (hp == null) return;
        if (!hp.isSpectating()) return;
        if (Items.SpectateMode.KNOCKBACK_TEST.item().isSimilar(hp.getPlayer().getInventory().getItemInMainHand())) {
            final HitBoxPlayer tp = hitBoxUtils.getPlayer(t);
            if (tp != null && (tp.isSpectating() || tp.isVanished())) {
                e.setCancelled(true);
                return;
            }
            e.setCancelled(false);
            e.setDamage(0);
        } else e.setCancelled(true);

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamageHighest(EntityDamageEvent e) {
        handleDamage(e);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamageLowest(EntityDamageEvent e) {
        handleDamage(e);
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof final Player p)) return;
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(p.getUniqueId());
        if (hp == null) return;
        if (!hp.isSpectating()) return;
        e.setFoodLevel(20);
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e) {
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(e.getPlayer().getUniqueId());
        if (hp == null) return;
        if (!hp.isSpectating()) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof final Player p)) return;
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(p.getUniqueId());
        if (hp == null) return;
        if (!hp.isSpectating()) return;
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
        for (Items.SortedItem spectateModeItem : Items.SpectateMode.SPECTATE_MODE_ITEMS) {
            if (!spectateModeItem.item().isSimilar(e.getCurrentItem())) continue;
            e.setCancelled(true);
            return;
        }

    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(e.getPlayer().getUniqueId());
        if (hp == null) return;
        if (!hp.isSpectating()) return;
        hp.getPlayer().setFireTicks(0);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(e.getPlayer().getUniqueId());
        if (hp == null) return;
        if (!hp.isSpectating()) return;

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (Items.SpectateMode.VANISH.item().isSimilar(e.getPlayer().getInventory().getItemInMainHand())) {
                e.setCancelled(true);

                if (!Utils.isOver(hp.getInteractTime())) return;
                hp.setInteractTime(System.currentTimeMillis() + 250);

                hp.getPlayer().getInventory().setItem(Items.SpectateMode.VANISHED.slot(), Items.SpectateMode.VANISHED.item());
                hitBoxUtils.getVanishManager().vanishPlayer(hp);
            } else if (Items.SpectateMode.VANISHED.item().isSimilar(e.getPlayer().getInventory().getItemInMainHand())) {
                e.setCancelled(true);

                if (!Utils.isOver(hp.getInteractTime())) return;
                hp.setInteractTime(System.currentTimeMillis() + 250);

                hp.getPlayer().getInventory().setItem(Items.SpectateMode.VANISH.slot(), Items.SpectateMode.VANISH.item());
                hitBoxUtils.getVanishManager().showPlayer(hp);
            } else if (Items.SpectateMode.TELEPORT_WAND.item().isSimilar(e.getPlayer().getInventory().getItemInMainHand())) {
                e.setCancelled(true);

                if (!Utils.isOver(hp.getInteractTime())) return;
                hp.setInteractTime(System.currentTimeMillis() + 250);

                final Location target = SpigotUtils.getTargetLocation(hp, 255);
                if(target == null) return;
                target.add(0.5, 1, 0.5);
                target.setYaw(hp.getPlayer().getLocation().getYaw());
                target.setPitch(hp.getPlayer().getLocation().getPitch());
                hp.teleport(target);
            } else if (Items.SpectateMode.SPEED_NORMAL.item().isSimilar(e.getPlayer().getInventory().getItemInMainHand())) {
                e.setCancelled(true);

                if (!Utils.isOver(hp.getInteractTime())) return;
                hp.setInteractTime(System.currentTimeMillis() + 250);


                hp.getPlayer().getInventory().setItem(Items.SpectateMode.SPEED_FAST.slot(), Items.SpectateMode.SPEED_FAST.item());
                hp.getPlayer().setFlySpeed(Utils.getMoveSpeed(3, true));
                hp.playSound(Sound.ENTITY_CHICKEN_EGG, 1, 2);
            } else if (Items.SpectateMode.SPEED_FAST.item().isSimilar(e.getPlayer().getInventory().getItemInMainHand())) {
                e.setCancelled(true);

                if (!Utils.isOver(hp.getInteractTime())) return;
                hp.setInteractTime(System.currentTimeMillis() + 250);


                hp.getPlayer().getInventory().setItem(Items.SpectateMode.SPEED_SUPER_FAST.slot(), Items.SpectateMode.SPEED_SUPER_FAST.item());
                hp.getPlayer().setFlySpeed(Utils.getMoveSpeed(5, true));
                hp.playSound(Sound.ENTITY_CHICKEN_EGG, 1, 5);

            } else if (Items.SpectateMode.SPEED_SUPER_FAST.item().isSimilar(e.getPlayer().getInventory().getItemInMainHand())) {
                e.setCancelled(true);

                if (!Utils.isOver(hp.getInteractTime())) return;
                hp.setInteractTime(System.currentTimeMillis() + 250);


                hp.getPlayer().getInventory().setItem(Items.SpectateMode.SPEED_NORMAL.slot(), Items.SpectateMode.SPEED_NORMAL.item());
                hp.getPlayer().setFlySpeed(Utils.getMoveSpeed(1, true));
                hp.playSound(Sound.ENTITY_CHICKEN_EGG);
            } else if (Items.SpectateMode.FLY_MODE_ACTIVATED.item().isSimilar(e.getPlayer().getInventory().getItemInMainHand())) {
                e.setCancelled(true);

                if (!Utils.isOver(hp.getInteractTime())) return;
                hp.setInteractTime(System.currentTimeMillis() + 250);


                hp.getPlayer().setFlying(false);
                hp.getPlayer().setAllowFlight(false);
                hp.playSound(Sound.BLOCK_BARREL_CLOSE);
                hp.getPlayer().getInventory().setItem(Items.SpectateMode.FLY_MODE_DEACTIVATED.slot(), Items.SpectateMode.FLY_MODE_DEACTIVATED.item());
            } else if (Items.SpectateMode.FLY_MODE_DEACTIVATED.item().isSimilar(e.getPlayer().getInventory().getItemInMainHand())) {
                e.setCancelled(true);

                if (!Utils.isOver(hp.getInteractTime())) return;
                hp.setInteractTime(System.currentTimeMillis() + 250);


                hp.getPlayer().setAllowFlight(true);
                hp.getPlayer().setFlying(true);
                hp.playSound(Sound.BLOCK_BARREL_OPEN);
                hp.getPlayer().getInventory().setItem(Items.SpectateMode.FLY_MODE_ACTIVATED.slot(), Items.SpectateMode.FLY_MODE_ACTIVATED.item());
            } else if (Items.SpectateMode.KNOCKBACK_TEST.item().isSimilar(e.getPlayer().getInventory().getItemInMainHand()) && e.getPlayer().isSneaking()) {
                e.setCancelled(true);

                if (!Utils.isOver(hp.getInteractTime())) return;
                hp.setInteractTime(System.currentTimeMillis() + 250);

                final LlamaSpit spit = hp.getPlayer().launchProjectile(LlamaSpit.class);
                spit.setCustomNameVisible(false);
                spit.customName(formatColoredValue(Messages.PREFIX.toString()));
                hitBoxUtils.getServer().getOnlinePlayers().forEach(player -> player.playSound(hp.getPlayer().getLocation(), Sound.ENTITY_LLAMA_SPIT, 1, 1));

            } else if (Items.SpectateMode.PLAYERS.item().isSimilar(e.getPlayer().getInventory().getItemInMainHand())) {
                e.setCancelled(true);

                if (!Utils.isOver(hp.getInteractTime())) return;
                hp.setInteractTime(System.currentTimeMillis() + 250);


                if (hp.getPlayerListPage() != null) {
                    final PlayerListInventory inventory = hitBoxUtils.getPlayerListInventoryManager().getPlayerListInventories().get(hp.getPlayerListPage());
                    if (inventory != null) {
                        hp.openInventory(inventory);
                        hp.playSound(Sound.BLOCK_CHEST_OPEN);
                        return;
                    }
                }

                hp.openInventory(hitBoxUtils.getPlayerListInventoryManager().getPlayerListInventories().get(0));
                hp.setPlayerListPage(0);

                hp.playSound(Sound.BLOCK_CHEST_OPEN);
            } else if (Items.SpectateMode.SPECTATOR.item().isSimilar(e.getPlayer().getInventory().getItemInMainHand())) {
                e.setCancelled(true);

                if (!Utils.isOver(hp.getInteractTime())) return;
                hp.setInteractTime(System.currentTimeMillis() + 250);


                hp.getPlayer().setGameMode(GameMode.SPECTATOR);
            } else if (hp.getPlayer().getInventory().getItemInMainHand().getType().isAir()
                    && e.getAction() == Action.RIGHT_CLICK_BLOCK
                    && e.getClickedBlock() != null
                    && e.getClickedBlock().getState() instanceof final Container container
                    && hp.isVanished()) {
                e.setCancelled(true);
                if (e.getClickedBlock().getType() == Material.ENDER_CHEST) return;

                final String name = "§e"+container.getBlock().getType().name()+"§7 -§e "+container.getBlock().getLocation().getBlockX()+"§7 -§e "+container.getBlock().getLocation().getBlockY()+"§7 -§e "+container.getBlock().getLocation().getBlockZ();
                final SpectateInventory inventory;
                if (container.getInventory().getType() == InventoryType.CHEST) {
                    inventory = new SpectateInventory(hp, name, Bukkit.createInventory(null, container.getInventory().getSize(), formatColoredValue(name)));
                } else {
                    inventory = new SpectateInventory(hp, name, Bukkit.createInventory(null, container.getInventory().getType(), formatColoredValue(name)));
                }
                inventory.setContent(container.getInventory().getContents());
                hp.openInventory(inventory);
            }
        } else if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (Items.SpectateMode.KNOCKBACK_TEST.item().isSimilar(e.getPlayer().getInventory().getItemInMainHand()) && hp.getPlayer().isSneaking() && hp.getUniqueId().equals(Sender.NETZKRONEHD_UUID)) {
                e.setCancelled(true);

                if (!Utils.isOver(hp.getInteractTime())) return;
                hp.setInteractTime(System.currentTimeMillis() + 250);

                final Location target = SpigotUtils.getTargetLocation(hp, 255);
                if(target == null) return;
                target.setY(target.getY()+1);

                target.getWorld().strikeLightningEffect(target);

            }
        }
    }

    @EventHandler
    public void onPickUp(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof final Player p) {
            final HitBoxPlayer hp = hitBoxUtils.getPlayer(p.getUniqueId());
            if (hp == null) return;
            if (!hp.isSpectating()) return;
            if (hp.getStaffSettings() != null) {
                e.setCancelled(!hp.getStaffSettings().isPickUpItems());
                return;
            }
            e.setCancelled(true);

        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(e.getPlayer().getUniqueId());
        if (hp == null) return;
        if (!hp.isSpectating()) return;
        for (Items.SortedItem spectateModeItem : Items.SpectateMode.SPECTATE_MODE_ITEMS) {
            if (!spectateModeItem.item().isSimilar(e.getItemDrop().getItemStack())) continue;
            e.setCancelled(true);
            return;
        }
    }

    private void handleDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof final Player p)) return;
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(p.getUniqueId());
        if (hp == null) return;
        if (!hp.isSpectating()) return;
        e.setCancelled(true);
    }

}
