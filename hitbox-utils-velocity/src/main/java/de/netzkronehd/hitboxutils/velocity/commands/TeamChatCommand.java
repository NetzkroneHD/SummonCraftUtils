package de.netzkronehd.hitboxutils.velocity.commands;

import de.netzkronehd.hitboxutils.database.cache.model.TeamUserModel;
import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.netzkronehd.hitboxutils.velocity.message.MessageColor.formatColoredValue;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.event.HoverEvent.showText;

public class TeamChatCommand extends HitBoxCommand {

    public TeamChatCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "teamchat", "hbteamchat", "tc");
        subcommands.add("toggle");
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if (!(hp.isStaff() || hasCommandPermission(hp))) return;
        if (args.length == 0) {
            final List<TeamUserModel> staff = hitBoxUtils.getTeamChatManager().getTeamUsersSorted();

            final ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.empty().toBuilder();
            builder.append(formatColoredValue(Messages.PREFIX + "Staff-Members §8(§e" + staff.size() + "§8):"));

            for (TeamUserModel teamUserModel : staff) {
                builder.append(formatColoredValue("\n§8" + Messages.ARROW_RIGHT + "§e " + teamUserModel.displayName() + "§r "));

                final Component server = formatColoredValue("§7-§e " + teamUserModel.server())
                        .clickEvent(runCommand("/server "+ teamUserModel.server()))
                        .hoverEvent(showText(formatColoredValue("§7Online on§e "+teamUserModel.proxy()+"\n§7Join on the server§e "+teamUserModel.server()+"§7.")));
                builder.append(server);
            }
            hp.sendMessage(builder.build());
            sendHelp(hp);
        } else if (args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
            hp.setTeamChat(!hp.isTeamChat());
            hp.sendMessage("You" + (hp.isTeamChat() ? "§a enabled" : "§c disabled") + "§7 the only TeamChat mode.");
        } else {
            hitBoxUtils.getTeamChatManager().sendTeamChatMessage(hp, getArgsAsText(args, 0));
        }
    }

    @Override
    public List<String> onTab(HitBoxPlayer hp, String[] args) {
        if (hasPermission(hp)) {
            if (args.length == 1) {
                args[0] = args[0].toLowerCase();

                final List<String> tabs = new ArrayList<>();
                tabs.addAll(getSubCommandTab(args[0]));
                tabs.addAll(getPlayerTabComplete(args[0]));

                return tabs;
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage("teamchat§8 <§eMessage§8>");
    }

}
