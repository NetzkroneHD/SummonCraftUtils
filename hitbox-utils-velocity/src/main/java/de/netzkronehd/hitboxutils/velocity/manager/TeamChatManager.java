package de.netzkronehd.hitboxutils.velocity.manager;

import de.netzkronehd.hitboxutils.database.cache.model.GroupModel;
import de.netzkronehd.hitboxutils.database.cache.model.TeamUserModel;
import de.netzkronehd.hitboxutils.database.cache.packet.teamchat.TeamPlayerMessagePacket;
import de.netzkronehd.hitboxutils.message.Messages;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;
import java.util.stream.Collectors;

import static de.netzkronehd.hitboxutils.velocity.message.MessageColor.formatColoredValue;
import static de.netzkronehd.hitboxutils.velocity.utils.VeloUtils.mapUserModel;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.event.HoverEvent.showText;

public class TeamChatManager extends Manager {

    public TeamChatManager(HitBoxUtils hitBox) {
        super(hitBox);
    }

    @Override
    public void onLoad() {

    }

    public void sendTeamChatMessage(String msg) {
        sendToTeamMembers(formatColoredValue("§8[§bTeamChat§8]§7 "+msg));
    }

    public void sendTeamChatMessage(HitBoxPlayer hp, String msg) {
        sendTeamChatMessage(hp.getPrefixAndName(), hp.getServerName(), msg);
        hitBox.getRedisManager().sendPacket(new TeamPlayerMessagePacket(
                hitBox.getRedisManager().getConfig().getServerName(),
                mapUserModel(hp),
                msg,
                System.currentTimeMillis()));
    }

    public void sendToTeamMembers(Component text) {
        getTeamChatMembers().forEach(player -> player.sendMessage(text));
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


    public void sendTeamChatMessage(String displayName, String serverName, String msg) {
        sendToTeamMembers(Component.empty().toBuilder().append(formatColoredValue("§8[§bTeamChat§8]§8 (§e"))
                .append(
                        formatColoredValue(serverName)
                                .hoverEvent(showText(formatColoredValue("§7Join the server§e " + serverName + "§7.")))
                                .clickEvent(runCommand("/server "+serverName))
                                .color(NamedTextColor.YELLOW)
                )
                .append(formatColoredValue("§8) " + displayName + " §8" + Messages.ARROW_RIGHT + "§f " + msg))
                .build()
        );
    }
}
