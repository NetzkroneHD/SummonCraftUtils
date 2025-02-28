package de.netzkronehd.hitboxutils.paper.command;

import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import org.bukkit.GameMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameModeCommand extends HitBoxCommand {

    public GameModeCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "gamemode", "gm");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if(!hasCommandPermission(hp)) return;
        if(args.length == 1) {
            try {
                final GameMode gameMode = getGameMode(args[0].toLowerCase());
                hp.getPlayer().setGameMode(gameMode);
                hp.sendMessage("You are now in GameMode§e "+hp.getPlayer().getGameMode().name()+"§7.");
            } catch (NumberFormatException | NullPointerException ex) {
                sendHelp(hp);
            }
        } else if (args.length == 2) {
            final HitBoxPlayer tp = hitBoxUtils.getPlayer(args[1]);
            if(tp == null) {
                hp.sendMessage(Messages.PLAYER_OFFLINE);
                return;
            }
            try {
                final GameMode gameMode = getGameMode(args[0].toLowerCase());
                tp.getPlayer().setGameMode(gameMode);
                tp.sendMessage("You are now in GameMode§e "+tp.getPlayer().getGameMode().name()+"§7.");
                hp.sendMessage("§e"+tp.getPrefixAndName()+"§7 is now in GameMode§e "+tp.getPlayer().getGameMode().name()+"§7.");
            } catch (NumberFormatException | NullPointerException ex) {
                sendHelp(hp);
            }
        } else sendHelp(hp);
    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("gm §8<§eGameMode§8> <§ePlayer§8>");
    }

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if(hasPermission(hp)) {
            if(args.length == 1) {
                args[0] = args[0].toLowerCase();
                final List<String> tabs = new ArrayList<>();
                Arrays.stream(GameMode.values()).filter(gameMode -> gameMode.name().toLowerCase().startsWith(args[0])).forEach(gameMode -> tabs.add(gameMode.name()));
                if("0".startsWith(args[0])) tabs.add("0");
                if("1".startsWith(args[0])) tabs.add("1");
                if("2".startsWith(args[0])) tabs.add("2");
                if("3".startsWith(args[0])) tabs.add("3");
                return tabs;
            } else if(args.length == 2) {
                return getPlayerTabComplete(args[1]);
            }
        }
        return Collections.emptyList();
    }

    private GameMode getGameMode(String input) {
        if (input.startsWith("c")) {
            return GameMode.CREATIVE;
        } else if (input.startsWith("su")) {
            return GameMode.SURVIVAL;
        } else if (input.startsWith("sp")) {
            return GameMode.SPECTATOR;
        } else if (input.startsWith("a")) {
            return GameMode.ADVENTURE;
        } else {
            final int id = Integer.parseInt(input);
            return GameMode.getByValue(id);
        }
    }

}
