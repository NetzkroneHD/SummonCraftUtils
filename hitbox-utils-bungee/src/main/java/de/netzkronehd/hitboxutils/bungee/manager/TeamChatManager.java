package de.netzkronehd.hitboxutils.bungee.manager;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.database.cache.model.GroupModel;
import de.netzkronehd.hitboxutils.database.cache.model.TeamUserModel;
import de.netzkronehd.hitboxutils.database.cache.packet.teamchat.TeamPlayerMessagePacket;
import de.netzkronehd.hitboxutils.message.MessageBuilder;
import de.netzkronehd.hitboxutils.message.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;
import java.util.stream.Collectors;

import static de.netzkronehd.hitboxutils.bungee.utils.BungeeUtils.mapTeamUserModel;
import static de.netzkronehd.hitboxutils.message.MessageBuilder.builder;
import static de.netzkronehd.hitboxutils.message.MessageBuilder.runCommandAndShowText;

public class TeamChatManager extends Manager {

    public static final String TEAM_CHAT_PREFIX = "§8[§bTeamChat§8]";

    public TeamChatManager(HitBoxUtils hitBox) {
        super(hitBox);
    }

    @Override
    public void onLoad() {

    }

    public void sendTeamChatMessage(String msg) {
        sendToTeamMembers(builder(TEAM_CHAT_PREFIX+"§7 ").append(msg, true).build());
    }

    public void sendTeamChatMessage(String displayName, String serverName, String msg) {
        final MessageBuilder messageBuilder = builder(TEAM_CHAT_PREFIX+" (§e")
                .append(runCommandAndShowText(serverName, "/server "+serverName, "§7Join the server§e " + serverName + "§7.").color(ChatColor.YELLOW))
                .append("§8) "+displayName+" §8"+Messages.ARROW_RIGHT+"§f "+msg, true);
        sendToTeamMembers(messageBuilder.build());
    }

    public void sendTeamChatMessage(HitBoxPlayer hp, String msg) {
        sendTeamChatMessage(hp.getPrefixAndName(), hp.getServerName(), msg);
        hitBox.getRedisManager().sendPacket(new TeamPlayerMessagePacket(
                hitBox.getRedisManager().getServerName(),
                mapTeamUserModel(hp),
                msg,
                System.currentTimeMillis())
        );
    }

    public void updateTeamUsers() {
        hitBox.getRedisManager().getRedisClient().getTeamUsers().stream()
                .filter(teamUserModel -> teamUserModel.proxy().equals(hitBox.getRedisManager().getServerName()))
                .forEach(teamUserModel -> hitBox.getRedisManager().removeTeamUser(teamUserModel.uuid()));

        hitBox.getTeamChatManager().getTeamChatMembers().forEach(player -> hitBox.getRedisManager().setTeamUser(player));
    }

    public void sendToTeamMembers(TextComponent text) {
        getTeamChatMembers().forEach(hitBoxPlayer -> hitBoxPlayer.sendMessage(text));
    }

    public List<HitBoxPlayer> getTeamChatMembers() {
        return hitBox.getPlayers().stream().filter(HitBoxPlayer::isTeamChatAllowed).toList();
    }

    public List<HitBoxPlayer> getTeamChatMembersSorted() {
        return hitBox.getPlayers().stream()
                .filter(HitBoxPlayer::isTeamChatAllowed)
                .sorted((o1, o2) -> Integer.compare(o2.getPrimaryGroup().getWeight().orElse(0), o1.getPrimaryGroup().getWeight().orElse(0)))
                .toList();
    }

    public List<TeamUserModel> getTeamUsersSorted() {
        final List<TeamUserModel> teamUsers = getTeamChatMembers().stream()
                .map(hp -> new TeamUserModel(hp.getUniqueId(),
                        hp.getName(),
                        hp.getPrefixAndName(),
                        hp.getServerName(),
                        hp.getProxy(),
                        new GroupModel(hp.getPrimaryGroup().getName(), hp.getPrefix(), hp.getPrimaryGroup().getWeight().orElse(0))
                )).collect(Collectors.toList());

        teamUsers.addAll(hitBox.getRedisManager().getRedisClient().getTeamUsers().stream()
                .filter(teamUserModel -> hitBox.getPlayer(teamUserModel.uuid()) == null)
                .toList()
        );

        return teamUsers.stream()
                .sorted((o1, o2) -> Integer.compare(o2.group().weight(), o1.group().weight()))
                .toList();
    }

}
