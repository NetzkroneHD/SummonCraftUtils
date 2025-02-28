package de.netzkronehd.hitboxutils.velocity.commands;

import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.utils.Utils;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter
public abstract class HitBoxCommand implements SimpleCommand {

    protected final HitBoxUtils hitBoxUtils;
    protected final Set<String> subcommands;
    private final String simpleName;

    private final CommandMeta commandMeta;

    public HitBoxCommand(HitBoxUtils hitBoxUtils, String name, String... alias) {
        this.simpleName = name;
        this.hitBoxUtils = hitBoxUtils;
        this.subcommands = new HashSet<>();
        this.commandMeta = hitBoxUtils.getProxyServer()
                .getCommandManager()
                .metaBuilder(name)
                .aliases(alias)
                .plugin(hitBoxUtils)
                .build();
    }

    public abstract void onExecute(HitBoxPlayer hp, String[] args);

    public void onExecute(CommandSource sender, String[] args) {
        sender.sendMessage(Component.text("This command is only for players."));
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

    public List<String> onTab(CommandSource source, String[] args) {
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
    public void execute(Invocation invocation) {
        if(!(invocation.source() instanceof final Player p)) return;
        final HitBoxPlayer up = hitBoxUtils.getPlayer(p);
        if (up == null) return;
        onExecute(up, invocation.arguments());
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        if(invocation.source() instanceof final Player p) {
            final HitBoxPlayer hp = hitBoxUtils.getPlayer(p);
            if(hp != null) {
                return onTab(hp, invocation.arguments());
            }
        } else {
            return onTab(invocation.source(), invocation.arguments());
        }
        return Collections.emptyList();
    }

    public List<String> onSuggestAsync(HitBoxPlayer hp, Invocation invocation) {
        return (List<String>) CompletableFuture.completedFuture(Collections.emptyList());
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
        hitBoxUtils.getCommands().add(this);
    }

}
