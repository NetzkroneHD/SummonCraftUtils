package de.netzkronehd.hitboxutils.velocity.synchronizer;

import de.netzkronehd.hitboxutils.database.cache.Synchronizer;
import de.netzkronehd.hitboxutils.database.cache.packet.mine.MineMessagePacket;
import de.netzkronehd.hitboxutils.database.cache.packet.proxy.FindProxyPlayerPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.proxy.FoundProxyPlayerPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.RestartCancelPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.RestartPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.custom.CustomRestartCancelPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.custom.CustomRestartPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.proxy.ProxyRestartCancelPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.proxy.ProxyRestartPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.server.ServerStartedPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.server.ServerStoppedPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.teamchat.TeamChatPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.teamchat.TeamPlayerJoinPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.teamchat.TeamPlayerLeavePacket;
import de.netzkronehd.hitboxutils.database.cache.packet.teamchat.TeamPlayerMessagePacket;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;

import java.util.UUID;

import static de.netzkronehd.hitboxutils.velocity.message.MessageColor.formatColoredValue;
import static de.netzkronehd.hitboxutils.velocity.message.MessageColor.prefix;
import static de.netzkronehd.hitboxutils.velocity.utils.VeloUtils.mapUserModel;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.event.HoverEvent.showText;

@RequiredArgsConstructor
public class VeloSynchronizer implements Synchronizer {

    private final HitBoxUtils hitBoxUtils;

    @Override
    public void handlePacket(TeamChatPacket teamChatPacket) {
        hitBoxUtils.getTeamChatManager().sendTeamChatMessage(teamChatPacket.getMsg());
    }

    @Override
    public void handlePacket(TeamPlayerMessagePacket teamPlayerMessagePacket) {
        if(isOnline(teamPlayerMessagePacket.getTeamUser().uuid())) return;
        hitBoxUtils.getTeamChatManager().sendTeamChatMessage(teamPlayerMessagePacket.getTeamUser().displayName(), teamPlayerMessagePacket.getTeamUser().server(), teamPlayerMessagePacket.getMessage());
    }

    @Override
    public void handlePacket(TeamPlayerJoinPacket teamPlayerJoinPacket) {
        if(isOnline(teamPlayerJoinPacket.getTeamUser().uuid())) return;
        hitBoxUtils.getTeamChatManager().sendTeamChatMessage(teamPlayerJoinPacket.getTeamUser().displayName() + "§7 is now§a online§7.");
    }

    @Override
    public void handlePacket(TeamPlayerLeavePacket teamPlayerLeavePacket) {
        if(isOnline(teamPlayerLeavePacket.getTeamUser().uuid())) return;
        hitBoxUtils.getTeamChatManager().sendTeamChatMessage(teamPlayerLeavePacket.getTeamUser().displayName() + "§7 is now§c offline§7.");
    }

    @Override
    public void handlePacket(ProxyRestartPacket proxyRestartPacket) {
        if(hitBoxUtils.getRestartManager().isRunning()) return;
        hitBoxUtils.getRestartManager().startCountdown(proxyRestartPacket.getCountdown());
    }

    @Override
    public void handlePacket(ProxyRestartCancelPacket proxyRestartCancelPacket) {
        if(!hitBoxUtils.getRestartManager().isRunning()) return;
        hitBoxUtils.getRestartManager().cancel();
    }

    @Override
    public void handlePacket(RestartPacket restartPacket) {
        if(hitBoxUtils.getRestartManager().isRunning()) return;
        hitBoxUtils.getRestartManager().startCountdown(restartPacket.getCountdown());
    }

    @Override
    public void handlePacket(RestartCancelPacket restartCancelPacket) {
        if(!hitBoxUtils.getRestartManager().isRunning()) return;
        hitBoxUtils.getRestartManager().cancel();
    }

    @Override
    public void handlePacket(MineMessagePacket mineMessagePacket) {
        final Component msg = prefix("§7The player "+mineMessagePacket.getUser().displayName()+"§7 has mined§e "+mineMessagePacket.getMine().mines()+"x "+mineMessagePacket.getMine().material()+"§7 in the past§b "+mineMessagePacket.getMine().minutes()+" minutes§7.")
                .clickEvent(runCommand("/server "+mineMessagePacket.getUser().server()))
                .hoverEvent(showText(formatColoredValue("§7Join the server§e "+mineMessagePacket.getUser().server())));

        hitBoxUtils.getPlayers().stream()
                .filter(player -> player.isStaff() && player.getStaffSettings() != null && player.getStaffSettings().isMineBroadcast())
                .forEach(player -> player.sendMessage(msg));
    }

    @Override
    public void handlePacket(ServerStartedPacket serverStartedPacket) {
        if(serverStartedPacket.getServer().equalsIgnoreCase(hitBoxUtils.getRedisManager().getConfig().getServerName())) return;
        hitBoxUtils.getPlayers().stream()
                .filter(player -> player.hasPermission("serverinfo"))
                .forEach(player -> player.sendMessage("The server§e "+serverStartedPacket.getServer()+"§7 was§a started§7."));
    }

    @Override
    public void handlePacket(ServerStoppedPacket serverStoppedPacket) {
        if(serverStoppedPacket.getServer().equalsIgnoreCase(hitBoxUtils.getRedisManager().getConfig().getServerName())) return;
        hitBoxUtils.getPlayers().stream()
                .filter(player -> player.hasPermission("serverinfo"))
                .forEach(player -> player.sendMessage("The server§e "+serverStoppedPacket.getServer()+"§7 was§c stopped§7."));
    }

    @Override
    public void handlePacket(CustomRestartPacket customRestartPacket) {
        if(!customRestartPacket.getServer().equalsIgnoreCase(hitBoxUtils.getRedisManager().getConfig().getServerName())) return;
        if(hitBoxUtils.getRestartManager().isRunning()) return;
        hitBoxUtils.getRestartManager().startCountdown(customRestartPacket.getCountdown());
    }

    @Override
    public void handlePacket(CustomRestartCancelPacket customRestartCancelPacket) {
        if(!customRestartCancelPacket.getServer().equalsIgnoreCase(hitBoxUtils.getRedisManager().getConfig().getServerName())) return;
        if(!hitBoxUtils.getRestartManager().isRunning()) return;
        hitBoxUtils.getRestartManager().cancel();
    }

    @Override
    public void handlePacket(FindProxyPlayerPacket findProxyPlayerPacket) {
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(findProxyPlayerPacket.getPlayerName());
        if(hp == null) return;
        hitBoxUtils.runAsync(() -> hitBoxUtils.getRedisManager().sendPacket(new FoundProxyPlayerPacket(hitBoxUtils.getRedisManager().getConfig().getServerName(), mapUserModel(hp), hitBoxUtils.getRedisManager().getConfig().getServerName(), findProxyPlayerPacket.getRequester())));
    }

    @Override
    public void handlePacket(FoundProxyPlayerPacket foundProxyPlayerPacket) {
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(foundProxyPlayerPacket.getRequester());
        if(hp == null) return;
        hp.sendMessageColored(foundProxyPlayerPacket.getTeamUserModel().displayName()+"§7 is online on proxy§e "+foundProxyPlayerPacket.getProxy()+"§7.");
    }

    private boolean isOnline(UUID uuid) {
        return hitBoxUtils.getPlayer(uuid) != null;
    }

}
