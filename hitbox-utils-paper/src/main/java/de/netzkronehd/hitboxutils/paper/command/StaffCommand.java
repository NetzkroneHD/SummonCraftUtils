package de.netzkronehd.hitboxutils.paper.command;

import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.inventory.SelectedPlayerInventory;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.paper.utils.Items;
import org.bukkit.GameMode;

import java.util.Collections;
import java.util.List;

public class StaffCommand extends HitBoxCommand {

    public StaffCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "staff", "hbstaff");
        subcommands.addAll(List.of("freeze", "unfreeze", "spectate", "menu"));
    }

    //staff freeze <Player>
    //staff unfreeze <Player>
    //staff spectate <Player>
    //staff menu <Player>

    @Override
    public void onExecute(HitBoxPlayer ep, String[] args) {
        if(ep.isStaff() || hasCommandPermission(ep)) {
            if (args.length == 0) {
                if (ep.isSpectating()) {
                    ep.disableSpectatorMode();

                    hitBoxUtils.getVanishManager().showPlayer(ep);
                    ep.sendMessage("Staff-Mode§c disabled§7.");
                } else {
                    ep.enableSpectatorMode();
                    ep.sendMessage("Staff-Mode§a enabled§7.");
                }
            } else if (args.length == 2) {
                final HitBoxPlayer tp = hitBoxUtils.getPlayer(args[1]);
                if (tp == null) {
                    ep.sendMessage(Messages.PLAYER_OFFLINE);
                    return;
                }
                if (args[0].equalsIgnoreCase("spectate")) {
                    if (checkStaff(ep, tp)) return;
                    if (!ep.isVanished()) ep.vanish();
                    if (!ep.isSpectating()) ep.enableSpectatorMode();

                    ep.getPlayer().getInventory().setItem(Items.SpectateMode.VANISHED.slot(), Items.SpectateMode.VANISHED.item());

                    ep.getPlayer().setGameMode(GameMode.SPECTATOR);
                    ep.getPlayer().setSpectatorTarget(tp.getPlayer());
                } else if (args[0].equalsIgnoreCase("freeze")) {
                    if (checkStaff(ep, tp)) return;
                    if (tp.isFrozen()) {
                        ep.sendMessage("That player is already frozen.");
                        return;
                    }
                    tp.freeze(ep);

                } else if (args[0].equalsIgnoreCase("unfreeze")) {
                    if (!tp.isFrozen()) {
                        ep.sendMessage("That player is not frozen.");
                        return;
                    }
                    tp.unfreeze(ep);
                } else if (args[0].equalsIgnoreCase("menu")) {
                    final SelectedPlayerInventory inv = hitBoxUtils.getPlayerListInventoryManager().getPlayerInventory(tp);
                    ep.openInventory(inv);

                } else sendHelp(ep);
            } else sendHelp(ep);
        }
    }

    @Override
    public void sendHelp(HitBoxPlayer ep) {
        ep.sendUsage("staff spectate §8<§ePlayer§8>§7 - Sets you in spectate and vanish mode");
        ep.sendUsage("staff freeze §8<§ePlayer§8>§7 - Freezes a player");
        ep.sendUsage("staff unfreeze §8<§ePlayer§8>§7 - Unfreezes a player");
        ep.sendUsage("staff menu §8<§ePlayer§8>§7 - Opens the menu for a player");
    }

    @Override
    public List<String> onTab(HitBoxPlayer ep, String[] args) {
        if (ep.isStaff() || hasPermission(ep)) {
            if (args.length == 1) {
                return getSubCommandsTabComplete(args[0].toLowerCase());
            } else if (args.length == 2) {
                if (subcommands.contains(args[0].toLowerCase())) {
                    return getPlayerTabComplete(args[1]);
                }
            }
        }
        return Collections.emptyList();
    }

    private boolean checkStaff(HitBoxPlayer ep, HitBoxPlayer tp) {
        if (tp.isStaff()) {
            ep.sendMessage("That player is a staff member.");
            return true;
        }
        return false;
    }

}
