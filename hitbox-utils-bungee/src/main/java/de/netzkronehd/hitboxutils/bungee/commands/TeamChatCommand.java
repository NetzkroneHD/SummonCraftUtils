package de.netzkronehd.hitboxutils.bungee.commands;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.database.cache.model.TeamUserModel;
import de.netzkronehd.hitboxutils.database.cache.packet.teamchat.TeamChatReloadPacket;
import de.netzkronehd.hitboxutils.message.MessageBuilder;
import de.netzkronehd.hitboxutils.message.Messages;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static de.netzkronehd.hitboxutils.message.MessageBuilder.builder;
import static de.netzkronehd.hitboxutils.message.MessageBuilder.showText;

public class TeamChatCommand extends HitBoxCommand {

    private final String joinCommand;

    public TeamChatCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "teamchat", "hbteamchat", "tc");
        subcommands.add("toggle");
        subcommands.add("reload");
        this.joinCommand = UUID.randomUUID().toString().replace("-", "");
        hitBoxUtils.getLogger().info("JoinCommand: " + joinCommand);
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if (!(hp.isStaff() || hasCommandPermission(hp))) return;
        if (args.length == 0) {
            final List<TeamUserModel> staff = hitBoxUtils.getTeamChatManager().getTeamUsersSorted();
            final MessageBuilder builder = builder(Messages.PREFIX + "Staff-Members §8(§e" + staff.size() + "§8):");
            for (TeamUserModel teamUser : staff) {
                builder.append("\n§8" + Messages.ARROW_RIGHT + "§e " + teamUser.displayName() + "§r ", true)
                        .append(showText("§7-§e " + teamUser.server(), "§7Online on§e " + teamUser.proxy() + "§7.\n§7Join on the server§e " + teamUser.server() + "§7.")
                                .runCommand("/tc "+joinCommand+" "+teamUser.server())
                        );
            }
            hp.sendMessage(builder.build());
            sendHelp(hp);
        } else if(args.length == 1 && args[0].equalsIgnoreCase("reload") && hp.hasPermission("teamchat.reload")) {
            hp.sendMessage("Reloading Teamchat...");
            hitBoxUtils.getRedisManager().sendPacket(new TeamChatReloadPacket(hitBoxUtils.getRedisManager().getServerName()));
            hp.sendMessage("Reloaded Teamchat members.");
        } else if (args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
            hp.setTeamChat(!hp.isTeamChat());
            hp.sendMessage("You" + (hp.isTeamChat() ? "§a enabled" : "§c disabled") + "§7 the only TeamChat mode.");
        } else if(args.length == 2 && args[0].equals(joinCommand)) {
            final ServerInfo serverInfo = hitBoxUtils.getProxy().getServerInfo(args[1]);
            if (serverInfo == null) {
                hp.sendMessage("§cThe server§e " + args[1] + "§c is not available.");
                return;
            }
            hp.sendMessage("§7Connecting to the server§e " + args[1] + "§7...");
            hp.getPlayer().connect(serverInfo);
        } else {
            final String message = getArgsAsText(args, 0);
            hitBoxUtils.getTeamChatManager().sendTeamChatMessage(hp, message);
            hitBoxUtils.getDiscordTeamChatManager().sendMessageInDiscord(hp, message);
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
