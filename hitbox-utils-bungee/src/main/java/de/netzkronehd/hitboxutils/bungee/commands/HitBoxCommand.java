package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.utils.Constants;
import de.netzkronehd.hitboxutils.utils.Utils;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.*;

@Getter
public abstract class HitBoxCommand extends Command implements TabExecutor {

    protected final HitBoxUtils hitBoxUtils;
    protected final Set<String> subcommands;
    private final String simpleName;

    public HitBoxCommand(HitBoxUtils hitBoxUtils, String name, String... aliases) {
        super(Constants.COMMAND_PREFIX + name, null, Utils.addToArray(aliases, name));
        this.simpleName = name;
        this.hitBoxUtils = hitBoxUtils;
        this.subcommands = new HashSet<>();
    }

    public abstract void onExecute(HitBoxPlayer hp, String[] args);

    public void onExecute(CommandSender sender, String[] args) {
        sender.sendMessage(new TextComponent("This command is only for players."));
    }

    public List<String> getSubCommandTab(String arg) {
        return subcommands.stream().filter(s -> s.startsWith(arg)).toList();
    }

    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if (args.length == 1) {
            args[0] = args[0].toLowerCase();
            if (!subcommands.isEmpty()) {
                return getSubCommandTab(args[0].toLowerCase());
            }
            return getPlayerTabComplete(args[0]);
        }

        return Collections.emptyList();
    }

    public List<String> onTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            args[0] = args[0].toLowerCase();
            if (!subcommands.isEmpty()) {
                return getSubCommandTab(args[0].toLowerCase());
            }
            return getPlayerTabComplete(args[0]);
        }

        return Collections.emptyList();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer pp) {
            final HitBoxPlayer fp = hitBoxUtils.getPlayer(pp.getUniqueId());
            if (fp == null) return;
            onExecute(fp, args);
        } else onExecute(sender, args);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender instanceof final ProxiedPlayer pp) {
            final HitBoxPlayer hp = hitBoxUtils.getPlayer(pp.getUniqueId());
            if (hp != null && hasPermission(hp)) {
                return onTab(hp, args);
            }
        } else {
            return onTab(sender, args);
        }
        return Collections.emptyList();
    }


    public List<String> getPlayerTabComplete(String arg) {
        arg = arg.toLowerCase();
        final List<String> players = new ArrayList<>();
        for (HitBoxPlayer hp : hitBoxUtils.getPlayers()) {
            if (hp.getName().toLowerCase().startsWith(arg)) players.add(hp.getName());
        }

        return players;
    }

    public boolean hasPermission(HitBoxPlayer hp) {
        return hp.hasPermission(simpleName);
    }

    public boolean hasPermission(HitBoxPlayer hp, String suffix) {
        return hp.hasPermission(simpleName + "." + suffix);
    }

    public boolean hasCommandPermission(HitBoxPlayer hp) {
        if (hasPermission(hp)) {
            return true;
        }
        hp.sendMessage(Messages.NO_PERMS);
        return false;
    }

    public boolean hasCommandPermission(HitBoxPlayer hp, String suffix) {
        if (hasPermission(hp, suffix)) {
            return true;
        }
        hp.sendMessage(Messages.NO_PERMS);
        return false;

    }

    public String getArgsAsText(String[] args, int from) {
        return Utils.getArgsAsText(args, from);
    }


    public void sendHelp(HitBoxPlayer hp) {
        hp.sendMessage(Messages.USAGE, simpleName);
    }

    public void register() {
        hitBoxUtils.getProxy().getPluginManager().registerCommand(hitBoxUtils, this);
        hitBoxUtils.getLogger().info("Registered command '" + simpleName + "'.");
    }

}
