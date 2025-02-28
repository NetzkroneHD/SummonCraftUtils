package de.netzkronehd.hitboxutils.bungee.synchronizer;

import com.google.gson.Gson;
import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.database.cache.Synchronizer;
import de.netzkronehd.hitboxutils.database.cache.packet.HitBoxPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.broadcast.BroadcastPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.filterbroadcast.FilterBroadcastPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.litebans.LitebansPunishPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.mine.MineMessagePacket;
import de.netzkronehd.hitboxutils.database.cache.packet.player.PlayerCommandPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.player.PlayerVotePacket;
import de.netzkronehd.hitboxutils.database.cache.packet.proxy.FindProxyPlayerPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.proxy.FoundProxyPlayerPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.RestartCancelPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.RestartPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.custom.CustomRestartCancelPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.custom.CustomRestartPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.proxy.ProxyRestartCancelPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.proxy.ProxyRestartPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.server.ConnectPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.server.ServerStartedPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.server.ServerStoppedPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.teamchat.*;
import de.netzkronehd.hitboxutils.message.MessageBuilder;
import de.netzkronehd.translation.sender.Sender;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Objects;
import java.util.UUID;

import static de.netzkronehd.hitboxutils.bungee.manager.BroadcastManager.GLOBAL_SERVER;
import static de.netzkronehd.hitboxutils.bungee.utils.BungeeUtils.mapTeamUserModel;
import static de.netzkronehd.hitboxutils.message.MessageBuilder.*;
import static de.netzkronehd.hitboxutils.message.Placeholder.placeholder;

@AllArgsConstructor
public class BungeeSynchronizer implements Synchronizer {

    private final HitBoxUtils hitBoxUtils;
    private final Gson gson = new Gson();

    @Override
    public void preHandlePacket(HitBoxPacket packet) {
        if(hitBoxUtils.isPrintPacketDebug()) {
            hitBoxUtils.getLogger().info("Received packet '"+packet.getClass().getSimpleName()+"' from '"+packet.getSource()+"' with data: "+gson.toJson(packet));
        }
    }

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
    public void handlePacket(TeamChatReloadPacket teamChatReloadPacket) {
        hitBoxUtils.getTeamChatManager().log("Updating team members...");
        hitBoxUtils.getTeamChatManager().updateTeamUsers();
        hitBoxUtils.getTeamChatManager().log("Updated team members.");
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
        final TextComponent msg = runCommandAndShowText(
                            "§7The player "+mineMessagePacket.getUser().displayName()+"§7 has mined§e "+mineMessagePacket.getMine().mines()+"x "+mineMessagePacket.getMine().material()+"§7 in the past§b "+mineMessagePacket.getMine().minutes()+" minutes§7.",
                            "/server " + mineMessagePacket.getUser().server(),
                            "§7Join the server§e " + mineMessagePacket.getUser().server(),
                            true,
                            true).build();
        hitBoxUtils.getPlayers().stream()
                .filter(player -> player.isStaff() && player.getStaffSettings() != null && player.getStaffSettings().isMineBroadcast())
                .forEach(player -> player.sendMessage(msg));
    }

    @Override
    public void handlePacket(ServerStartedPacket serverStartedPacket) {
        if(serverStartedPacket.getServer().equalsIgnoreCase(hitBoxUtils.getRedisManager().getServerName())) return;
        final TextComponent serverComponent = MessageBuilder.prefix()
                .append("§7The server ")
                .append(
                        runCommand(
                                "§e"+serverStartedPacket.getServer(),
                                "/server "+serverStartedPacket.getServer()
                        ).showText("§7Join§e "+serverStartedPacket.getServer())
                )
                .append("§7 was§a started§7.").build();
        hitBoxUtils.getPlayers().stream()
                .filter(player -> player.hasPermission("serverinfo"))
                .forEach(player -> {
                    player.sendLine();
                    player.sendRawMessage("\n\n");
                    player.sendMessage(serverComponent);
                    player.sendRawMessage("\n\n");
                    player.sendLine();
                });
    }

    @Override
    public void handlePacket(ServerStoppedPacket serverStoppedPacket) {
        if(serverStoppedPacket.getServer().equalsIgnoreCase(hitBoxUtils.getRedisManager().getServerName())) return;
        hitBoxUtils.getPlayers().stream()
                .filter(player -> player.hasPermission("serverinfo"))
                .forEach(player -> {
                    player.sendLine();
                    player.sendRawMessage("\n\n");
                    player.sendMessage("The server§e "+serverStoppedPacket.getServer()+"§7 was§c stopped§7.");
                    player.sendRawMessage("\n\n");
                    player.sendLine();
                });

    }

    @Override
    public void handlePacket(CustomRestartPacket customRestartPacket) {
        if(!customRestartPacket.getServer().equalsIgnoreCase(hitBoxUtils.getRedisManager().getServerName())) return;
        if(hitBoxUtils.getRestartManager().isRunning()) return;
        hitBoxUtils.getRestartManager().startCountdown(customRestartPacket.getCountdown());
    }

    @Override
    public void handlePacket(CustomRestartCancelPacket customRestartCancelPacket) {
        if(!customRestartCancelPacket.getServer().equalsIgnoreCase(hitBoxUtils.getRedisManager().getServerName())) return;
        if(!hitBoxUtils.getRestartManager().isRunning()) return;
        hitBoxUtils.getRestartManager().cancel();
    }

    @Override
    public void handlePacket(FindProxyPlayerPacket findProxyPlayerPacket) {
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(findProxyPlayerPacket.getPlayerName());
        if(hp == null) return;
        hitBoxUtils.runAsync(() -> hitBoxUtils.getRedisManager().sendPacket(new FoundProxyPlayerPacket(hitBoxUtils.getRedisManager().getServerName(), mapTeamUserModel(hp), hitBoxUtils.getRedisManager().getServerName(), findProxyPlayerPacket.getRequester())));
    }

    @Override
    public void handlePacket(FoundProxyPlayerPacket foundProxyPlayerPacket) {
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(foundProxyPlayerPacket.getRequester());
        if(hp == null) return;
        hp.sendMessageColored(foundProxyPlayerPacket.getTeamUserModel().displayName()+"§7 is online on proxy§e "+foundProxyPlayerPacket.getProxy()+"§7 and on server§b "+foundProxyPlayerPacket.getTeamUserModel().server()+"§7.");
    }

    @Override
    public void handlePacket(BroadcastPacket broadcastPacket) {
        if(broadcastPacket.getServer().equalsIgnoreCase(GLOBAL_SERVER) || broadcastPacket.getServer().equalsIgnoreCase(hitBoxUtils.getRedisManager().getServerName())) {
            hitBoxUtils.getBroadcastManager().receivedBroadcast(broadcastPacket);
        }
    }

    @Override
    public void handlePacket(PlayerVotePacket playerVotePacket) {
        if(Objects.equals(playerVotePacket.getSource(), hitBoxUtils.getRedisManager().getServerName())) {
            hitBoxUtils.getLogger().info("Received vote packet '"+playerVotePacket.getVote()+"' from same instance ("+playerVotePacket.getSource()+"). Ignoring.");
            return;
        }
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(playerVotePacket.getVote().username());
        if(hp == null) return;
        hitBoxUtils.getVoteManager().voteReceived(playerVotePacket.getVote());
    }

    @Override
    public void handlePacket(FilterBroadcastPacket filterBroadcastPacket) {
        final TextComponent filterMessage = builder().append(
                        placeholder(hitBoxUtils.getChatFilterManager().getFilterMessage())
                                .replace("PLAYER", filterBroadcastPacket.getUser().displayName())
                                .replace("WORD", filterBroadcastPacket.getFilterResultModel().bannedWord())
                                .replace("MESSAGE", filterBroadcastPacket.getFilterResultModel().text())
                                .replace("SERVER", filterBroadcastPacket.getUser().server())
                                .build(),
                        true)
                .showText("§cMute")
                .suggestCommand("/tempmute " + filterBroadcastPacket.getUser().name())
                .build();

        hitBoxUtils.getPlayers().stream().filter(
                player -> (player.isStaff() || player.hasPermission("chatfilter.broadcast")) &&
                        player.getStaffSettings() != null &&
                        player.getStaffSettings().isFilterBroadcast()
        ).forEach(
                player -> player.sendMessage(filterMessage)
        );
    }

    @Override
    public void handlePacket(ConnectPacket connectPacket) {
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(connectPacket.getUuid());
        if(hp == null) return;
        final ServerInfo serverInfo = hitBoxUtils.getProxy().getServerInfo(connectPacket.getServer());
        if(serverInfo == null) return;
        hp.getPlayer().connect(serverInfo);

    }

    @Override
    public void handlePacket(PlayerCommandPacket playerCommandPacket) {
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(playerCommandPacket.getUuid());
        if(hp == null) return;
        hitBoxUtils.getProxy().getPluginManager().dispatchCommand(hp.getPlayer(), playerCommandPacket.getCommand());
    }

    @Override
    public void handlePacket(LitebansPunishPacket litebansPunishPacket) {
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(litebansPunishPacket.getUuid());
        if(hp == null) return;
        final CommandSender sender;
        if (Sender.CONSOLE_UUID.equals(litebansPunishPacket.getCommandExecutor())) {
            sender = hitBoxUtils.getProxy().getConsole();
        } else {
            sender = hp.getPlayer();
        }
        hitBoxUtils.getProxy().getPluginManager().dispatchCommand(sender, litebansPunishPacket.getCommand());
    }

    private boolean isOnline(UUID uuid) {
        return hitBoxUtils.getPlayer(uuid) != null;
    }

}
