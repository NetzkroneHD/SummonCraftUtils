package de.netzkronehd.hitboxutils.paper.command;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.clickblock.ClickBlock;
import de.netzkronehd.hitboxutils.paper.clickblock.ClickBlockExecuteCommand;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.paper.utils.Items;
import de.netzkronehd.hitboxutils.utils.Utils;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ClickBlockCommand extends HitBoxCommand {

    public ClickBlockCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "clickblock");
        subcommands.addAll(Arrays.asList("create", "edit", "delete", "addcmd", "removecmd", "setlocation", "save", "clearplayers"));
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if(!hasCommandPermission(hp)) return;
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("create")) {
                hp.setEditClickBlock(new ClickBlock(hitBoxUtils.getClickBlockManager().getNewId(), null));
                hp.sendMessage("Created a new ClickBlock with the Id§e "+hp.getEditClickBlock().getId()+"§7.");

            } else if (args[0].equalsIgnoreCase("setlocation")) {
                if (!checkSelectedBlock(hp)) return;
                hp.getPlayer().getInventory().addItem(Items.ClickBlock.SET_LOCATION);
                hp.playSound(Sound.ENTITY_ITEM_PICKUP);
            } else if (args[0].equalsIgnoreCase("clearplayers")) {
                if (!checkSelectedBlock(hp)) return;
                hp.getEditClickBlock().getPlayers().clear();
                hp.sendMessage("Successfully cleared the players.");
            } else if (args[0].equalsIgnoreCase("save")) {
                if (!checkSelectedBlock(hp)) return;
                if (hp.getEditClickBlock().getLocation() == null) {
                    hp.sendMessage("You need to set a location for a click block.");
                    return;
                }
                hitBoxUtils.getClickBlockManager().saveClickBlock(hp.getEditClickBlock());
                hp.sendMessage("Successfully saved§e "+hp.getEditClickBlock().getId()+"§7.");
                hp.setEditClickBlock(null);
            } else sendHelp(hp);
        } else if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("edit")) {

                try {
                    final ClickBlock block = hitBoxUtils.getClickBlockManager().getBlock(Integer.parseInt(args[1]));
                    if (block == null) {
                        hp.sendMessage("Cloud not find a ClickBlock with the id§e "+args[1]+"§7.");
                        return;
                    }
                    hp.setEditClickBlock(block);
                    hp.sendMessage("You successfully selected the ClickBlock§e "+block.getId()+"§7.");

                } catch (NumberFormatException ex) {
                    hp.sendMessage("Please use a number.");
                }

            } else if (args[0].equalsIgnoreCase("delete")) {
                try {
                    final ClickBlock block = hitBoxUtils.getClickBlockManager().getBlock(Integer.parseInt(args[1]));
                    if (block == null) {
                        hp.sendMessage("Cloud not find a ClickBlock with the id§e "+args[1]+"§7.");
                        return;
                    }
                    hitBoxUtils.getClickBlockManager().deleteBlock(block);
                    hp.sendMessage("You successfully deleted the ClickBlock§e "+block.getId()+"§7.");
                } catch (NumberFormatException ex) {
                    hp.sendMessage("Please use a number.");
                }
            } else if (args[0].equalsIgnoreCase("addcmd") && args.length > 2) {
                if(!checkSelectedBlock(hp)) return;

                final boolean console = (args[1].equalsIgnoreCase("console"));
                final String text = Utils.getArgsAsText(args, 2);
                final ClickBlockExecuteCommand command = ClickBlockExecuteCommand.builder()
                        .id(hitBoxUtils.getClickBlockManager().getNewId())
                        .console(console)
                        .command(text)
                        .build();

                for (ClickBlockExecuteCommand cmd : hp.getEditClickBlock().getCommands()) {
                    if(cmd.isConsole() != command.isConsole()) continue;
                    if(!cmd.getCommand().equalsIgnoreCase(command.getCommand())) continue;
                    hp.sendMessage("The command '§e"+command.getCommand()+"§7' is already in the command list.");
                    return;
                }
                hp.getEditClickBlock().getCommands().add(command);
                hp.sendMessage("Added '§e"+command.getCommand()+"§7' to the command list.");

            } else if (args[0].equalsIgnoreCase("removecmd")) {
                if (!checkSelectedBlock(hp)) return;
                final String text = Utils.getArgsAsText(args, 1);

                if (hp.getEditClickBlock().getCommands().removeIf(cmd -> cmd.getCommand().equalsIgnoreCase(text))) {
                    hp.sendMessage("Removed the command '§e"+text+"§7' from the command list.");
                } else {
                    hp.sendMessage("Cloud not find that command in the list.");
                }
            } else sendHelp(hp);

        } else sendHelp(hp);

    }

    public boolean checkSelectedBlock(HitBoxPlayer hp) {
        if (hp.getEditClickBlock() == null) {
            hp.sendMessage("You need to select a ClickBlock.");
            return false;
        }
        return true;
    }



    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("clickblock");
        hp.sendArrow("§ecreate§7 - Creates a clickable block");
        hp.sendArrow("§eedit§8 <§eid§8>§7 - Edits a clickable block");
        hp.sendArrow("§edelete§8 <§eid§8>§7 - Deletes a clickable block");
        hp.sendArrow("§eaddcmd§8 §8<§ePlayer/Console§8> <§eCommand§8>§7 - Adds a command");
        hp.sendArrow("§eremovecmd§8 <§eCommand§8>§7 - Removes a command");
        hp.sendArrow("§esetlocation§7 - Sets the location of the block");
        hp.sendArrow("§eclearplayers§7 - Clears the players who clicked");
        hp.sendArrow("§esave§7 - Saves the ClickBock command");

    }

        /*
    /clickblock create
    /clickblock edit <id>
    /clickblock delete <id>
    /clickblock setlocation
    /clickblock addcmd
    /clickblock removecmd
    /clickblock save
     */

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if (hasPermission(hp)) {
            if (args.length == 1) {
                return getSubCommandsTabComplete(args[0].toLowerCase());
            } else if (args.length >= 2) {
                if (args[0].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("delete")) {
                    final List<String> tabs = new ArrayList<>();
                    hitBoxUtils.getClickBlockManager().getClickBlocks().values().forEach(clickBlock -> {
                        if (!String.valueOf(clickBlock.getId()).startsWith(args[1])) return;
                        tabs.add(clickBlock.getId() + "");
                    });
                    return tabs;
                } else if (args[0].equalsIgnoreCase("addcmd")) {
                    final List<String> tabs = new ArrayList<>();
                    args[1] = args[1].toLowerCase();
                    if("player".startsWith(args[1])) tabs.add("player");
                    if("console".startsWith(args[1])) tabs.add("console");
                    return tabs;
                } else if (args[0].equalsIgnoreCase("removecmd")) {
                    if (hp.getEditClickBlock() != null) {
                        final List<String> tabs = new ArrayList<>();
                        args[1] = args[1].toLowerCase();

                        for (ClickBlockExecuteCommand command : hp.getEditClickBlock().getCommands()) {
                            if(!command.getCommand().toLowerCase().startsWith(args[1])) continue;
                            tabs.add(command.getCommand());
                        }
                        return tabs;
                    }
                }
            }
        }
        return Collections.emptyList();
    }
}
