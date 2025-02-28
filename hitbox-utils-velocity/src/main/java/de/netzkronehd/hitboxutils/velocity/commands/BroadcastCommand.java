package de.netzkronehd.hitboxutils.velocity.commands;

import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.message.MessageColor;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;
import net.kyori.adventure.text.Component;

import java.util.Collections;
import java.util.List;

import static java.time.Duration.ofSeconds;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.title.Title.Times.times;
import static net.kyori.adventure.title.Title.title;

public class BroadcastCommand extends HitBoxCommand {

    public BroadcastCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "broadcast", "bc");
        subcommands.addAll(List.of("title", "subtitle", "chat", "actionbar"));
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if(!hasCommandPermission(hp)) return;
        if(args.length < 2) {
            sendHelp(hp);
            return;
        }
        final Component text = MessageColor.formatColoredValue(getArgsAsText(args, 1));

        if(args[0].equalsIgnoreCase("chat")) {
            hitBoxUtils.getProxyServer().sendMessage(text);
        } else if(args[0].equalsIgnoreCase("title")) {
            hitBoxUtils.getProxyServer().showTitle(title(
                    text,
                    text(""),
                    times(
                            ofSeconds(2),
                            ofSeconds(5),
                            ofSeconds(2)
                    )
            ));
        } else if(args[0].equalsIgnoreCase("subtitle")) {
            hitBoxUtils.getProxyServer().showTitle(title(
                    text(""),
                    text,
                    times(
                            ofSeconds(2),
                            ofSeconds(5),
                            ofSeconds(2)
                    )
            ));
        } else if(args[0].equalsIgnoreCase("actionbar")) {
            hitBoxUtils.getProxyServer().sendActionBar(text);
        } else sendHelp(hp);

    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("bc §8<§eActionbar§8/§eSubtitle§8/§eTitle§8/§eChat§8> <§eMessage§8>");
    }

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if(hasPermission(hp)) {
            if(args.length == 1) {
                return getSubCommandTab(args[0].toLowerCase());
            }
        }
        return Collections.emptyList();
    }

}
