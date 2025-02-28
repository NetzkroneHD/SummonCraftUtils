package de.netzkronehd.hitboxutils.paper.command;

import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.utils.Constants;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public abstract class HitBoxCommand implements CommandExecutor, TabCompleter {

    protected final HitBoxUtils hitBoxUtils;
    private final String name, simpleName;
    private final List<String> alias;
    protected final List<String> subcommands;

    public HitBoxCommand(HitBoxUtils hitBoxUtils, String name, String... alias) {
        this.hitBoxUtils = hitBoxUtils;
        this.name = Constants.COMMAND_PREFIX+name;
        this.simpleName = name;
        this.alias = new ArrayList<>(Arrays.asList(alias));
        this.alias.add(this.simpleName);
        this.subcommands = new ArrayList<>();
    }

    public abstract void onExecute(HitBoxPlayer hp, String[] args);

    public void onExecute(CommandSender sender, String[] args) {

    }

    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        final List<String> tabs = new ArrayList<>();
        if (args.length == 1) {
            tabs.addAll(getPlayerTabComplete(args[0]));
        }
        return tabs;
    }

    public List<String> onTab(CommandSender sender, String[] args) {
        final List<String> tabs = new ArrayList<>();
        if (args.length == 1) {
            tabs.addAll(getPlayerTabComplete(args[0]));
        }
        return tabs;
    }

    public void sendHelp(HitBoxPlayer hp) {
        hp.sendMessage(Messages.USAGE, simpleName);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof final Player p) {
            final HitBoxPlayer dp = hitBoxUtils.getPlayer(p.getUniqueId());
            onExecute(dp, args);
        } else onExecute(sender, args);
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof final Player p) {
            final HitBoxPlayer dp = hitBoxUtils.getPlayer(p.getUniqueId());
            return onTab(dp, args);
        } else {
            return onTab(sender, args);
        }
    }

    public boolean hasCommandPermission(HitBoxPlayer hp) {
        if (hp.hasPermission(simpleName)) {
            return true;
        }
        hp.sendMessage(Messages.NO_PERMS);
        return false;
    }

    public boolean hasCommandPermission(HitBoxPlayer hp, String suffix) {
        if (hp.hasPermission(simpleName + "." + suffix)) {
            return true;
        }
        hp.sendMessage(Messages.NO_PERMS);
        return false;
    }

    public boolean hasPermission(HitBoxPlayer hp) {
        return hp.hasPermission(simpleName);
    }

    public boolean hasPermission(HitBoxPlayer hp, String suffix) {
        return hp.hasPermission(simpleName, suffix);
    }

    public List<String> getSubCommandsTabComplete(String arg) {
        return subcommands.stream().filter(s -> s.startsWith(arg)).toList();
    }

    public List<String> getPlayerTabComplete(String arg) {
        arg = arg.toLowerCase();
        final List<String> players = new ArrayList<>();
        for (HitBoxPlayer hp : hitBoxUtils.getPlayers()) {
            if (hp.getName().toLowerCase().startsWith(arg)) players.add(hp.getName());
        }
        return players;
    }

}
