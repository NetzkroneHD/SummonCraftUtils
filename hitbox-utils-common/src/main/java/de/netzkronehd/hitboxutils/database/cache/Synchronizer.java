package de.netzkronehd.hitboxutils.database.cache;

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
import de.netzkronehd.hitboxutils.database.cache.packet.restart.spigot.SpigotRestartCancelPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.restart.spigot.SpigotRestartPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.server.ConnectPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.server.ServerStartedPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.server.ServerStoppedPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.sound.GlobalSoundPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.support.*;
import de.netzkronehd.hitboxutils.database.cache.packet.teamchat.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface Synchronizer {

    /**
     * Refactoring:
     * Am besten noch einmal überarbeiten
     * reicht aber für den Anfang
     */

    default void packedReceived(HitBoxPacket packet) {
        ignoreException(() -> preHandlePacket(packet));
        try {
            final Method method = getClass().getMethod("handlePacket", packet.getClass());
            method.invoke(this, packet);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            handleCustomPacket(packet);
        }
        ignoreException(() -> postHandlePacket(packet));
    }
    default void preHandlePacket(HitBoxPacket packet) {}

    default void postHandlePacket(HitBoxPacket packet) {}
    default void handleCustomPacket(HitBoxPacket packet) {}

    private void ignoreException(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ignored) {}
    }

//    Team
    default void handlePacket(TeamChatPacket teamChatPacket) {}
    default void handlePacket(TeamPlayerJoinPacket teamPlayerJoinPacket) {}
    default void handlePacket(TeamPlayerLeavePacket teamPlayerLeavePacket) {}
    default void handlePacket(TeamPlayerMessagePacket teamPlayerMessagePacket) {}
    default void handlePacket(TeamChatReloadPacket teamChatReloadPacket) {}

//    Restart
    default void handlePacket(ProxyRestartPacket proxyRestartPacket) {}
    default void handlePacket(ProxyRestartCancelPacket proxyRestartCancelPacket) {}
    default void handlePacket(SpigotRestartPacket spigotRestartPacket) {}
    default void handlePacket(SpigotRestartCancelPacket spigotRestartCancelPacket) {}
    default void handlePacket(RestartPacket restartPacket) {}
    default void handlePacket(RestartCancelPacket restartCancelPacket) {}
    default void handlePacket(CustomRestartPacket customRestartPacket) {}
    default void handlePacket(CustomRestartCancelPacket customRestartCancelPacket) {}

//    Server
    default void handlePacket(ServerStartedPacket serverStartedPacket) {}
    default void handlePacket(ServerStoppedPacket serverStoppedPacket) {}
    default void handlePacket(ConnectPacket connectPacket) {}

//    MineMessage
    default void handlePacket(MineMessagePacket mineMessagePacket) {}

//    Proxy
    default void handlePacket(FindProxyPlayerPacket findProxyPlayerPacket) {}
    default void handlePacket(FoundProxyPlayerPacket foundProxyPlayerPacket) {}

//    Support
    default void handlePacket(SupportChatClosedPacket supportChatClosedPacket) {}
    default void handlePacket(SupportChatCreatedPacket supportChatCreatedPacket) {}
    default void handlePacket(SupportChatJoinPacket supportChatJoinPacket) {}
    default void handlePacket(SupportChatLeftPacket supportChatLeftPacket) {}
    default void handlePacket(SupportChatMessagePacket supportChatMessagePacket) {}

//    Broadcast
    default void handlePacket(BroadcastPacket broadcastPacket) {}

//    Sound
    default void handlePacket(GlobalSoundPacket globalSoundPacket) {}

//    Player
    default void handlePacket(PlayerVotePacket playerVotePacket) {}
    default void handlePacket(PlayerCommandPacket playerCommandPacket) {}

//    Litebans
    default void handlePacket(LitebansPunishPacket litebansPunishPacket) {}

//    Filter
    default void handlePacket(FilterBroadcastPacket filterBroadcastPacket) {}

}
