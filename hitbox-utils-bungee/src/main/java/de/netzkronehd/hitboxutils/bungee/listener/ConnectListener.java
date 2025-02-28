package de.netzkronehd.hitboxutils.bungee.listener;

import de.netzkronehd.hitboxutils.api.UUIDFetcher;
import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.database.cache.packet.teamchat.TeamPlayerJoinPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.teamchat.TeamPlayerLeavePacket;
import de.netzkronehd.hitboxutils.player.PlayerPlaytime;
import de.netzkronehd.hitboxutils.sound.GlobalSound;
import de.netzkronehd.hitboxutils.utils.Utils;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.net.InetSocketAddress;

import static de.netzkronehd.hitboxutils.bungee.utils.BungeeUtils.mapTeamUserModel;
import static de.netzkronehd.hitboxutils.message.HexColor.translateHexCodesInComponents;

@RequiredArgsConstructor
public class ConnectListener implements Listener {

    private final HitBoxUtils hitBoxUtils;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(LoginEvent e) {
        UUIDFetcher.putInCache(e.getConnection().getUniqueId(), e.getConnection().getName().toLowerCase());
        if (hitBoxUtils.getAltsManager().isAllowed()) return;

        if (!(e.getConnection().getSocketAddress() instanceof final InetSocketAddress address)) return;
        if (!hitBoxUtils.getAltsManager().hasAltAccount(Utils.formatIpAddress(address.toString()), e.getConnection().getUniqueId()))
            return;

        hitBoxUtils.getLogger().info(e.getConnection().getName() + " has been kicked because alt accounts have been found.");

        e.setCancelled(true);
        e.setCancelReason(translateHexCodesInComponents(hitBoxUtils.getAltsManager().getAltsDisabledMessage()));

    }

    @EventHandler
    public void onPostLogin(PostLoginEvent e) {
        final long lastJoin = System.currentTimeMillis();
        hitBoxUtils.runAsync(() -> hitBoxUtils.getPlayerManager().setName(e.getPlayer().getUniqueId(), e.getPlayer().getName(), lastJoin));

        hitBoxUtils.getPlayerCache().remove(e.getPlayer().getUniqueId());

        final HitBoxPlayer hp = new HitBoxPlayer(hitBoxUtils, e.getPlayer(), hitBoxUtils.getBungeeAudiences().sender(e.getPlayer()));
        hitBoxUtils.getPlayerCache().put(e.getPlayer().getUniqueId(), hp);

        hp.setPlayerPlaytime(PlayerPlaytime.builder()
                .uuid(hp.getUniqueId())
                .name(hp.getName())
                .timeJoined(System.currentTimeMillis()).build());

        if (hp.isTeamChatAllowed()) {
            hitBoxUtils.getTeamChatManager().sendTeamChatMessage(hp.getPrefixAndName() + "§7 is now§a online§7.");
            hitBoxUtils.getRedisManager().setTeamUser(hp);

            hitBoxUtils.runAsync(() -> hitBoxUtils.getRedisManager().sendPacket(new TeamPlayerJoinPacket(hitBoxUtils.getRedisManager().getServerName(), mapTeamUserModel(hp))));
        }

        hitBoxUtils.runAsync(() -> {
            if (hitBoxUtils.getPlaytimeManager().exists(hp.getUniqueId())) {
                hp.getPlayerPlaytime().setPlaytime(hitBoxUtils.getPlaytimeManager().getPlaytime(hp.getUniqueId()).getPlaytime());
                hitBoxUtils.getPlaytimeManager().setPlaytime(hp.getUniqueId(), hp.getPlayerPlaytime().getPlaytime(), hp.getPlayerPlaytime().getTimeJoined());
            } else {
                hp.getPlayerPlaytime().setPlaytime(0);
                hitBoxUtils.getPlaytimeManager().createPlaytime(hp.getUniqueId(), 0, hp.getPlayerPlaytime().getTimeJoined());
            }

            hp.setDiscordVerification(hitBoxUtils.getDiscordVerifyManager().loadVerification(hp.getUniqueId()));

            if (hp.isStaff()) {
                hp.setStaffSettings(hitBoxUtils.getStaffSettingManager().loadSettings(hp.getUniqueId()));
            }
            if (!hp.isVerified()) return;
            if (hitBoxUtils.getDiscordManager().isReady()) {
                final Guild guild = hitBoxUtils.getDiscordManager().getDiscordBot().getGuild();
                if (guild != null) {
                    hitBoxUtils.getDiscordVerifyManager().updateVerification(guild, hp, member -> {});
                }
            }

            if (hp.hasPermission("joinsound")) {
                if (!hitBoxUtils.getSoundManager().isCooldownOver(hp.getUniqueId())) return;
                final GlobalSound sound = hitBoxUtils.getSoundManager().getSound(e.getPlayer().getUniqueId());
                if (sound != null) {
                    hitBoxUtils.getSoundManager().setCooldown(hp.getUniqueId());
                    hitBoxUtils.getPlayers().forEach(player -> player.playSound(sound));
                    hitBoxUtils.getProxy().broadcast(hitBoxUtils.getSoundManager().getJoinMessage(hp));
                }
            }
        });
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent e) {
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(e.getPlayer());
        if (hp == null) return;
        hitBoxUtils.getPlayerCache().remove(e.getPlayer().getUniqueId());

        final long timeJoined = hp.getPlayerPlaytime().getTimeJoined();
        final long timeLeft = System.currentTimeMillis();
        hitBoxUtils.runAsync(() -> {
            if (hitBoxUtils.getPlaytimeManager().exists(hp.getUniqueId())) {
                hitBoxUtils.getPlaytimeManager().addPlaytime(hp.getUniqueId(), (timeLeft - timeJoined), 0);
            } else {
                hitBoxUtils.getPlaytimeManager().createPlaytime(hp.getUniqueId(), (timeLeft - timeJoined), 0);
            }
        });

        if (!hp.isTeamChatAllowed()) return;

        hitBoxUtils.runAsync(() -> {
            hitBoxUtils.getRedisManager().removeTeamUser(hp.getUniqueId());
            hitBoxUtils.getRedisManager().sendPacket(new TeamPlayerLeavePacket(hitBoxUtils.getRedisManager().getServerName(), mapTeamUserModel(hp)));
        });
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent e) {
        if(e.getServer() == null) return;
        if(e.getServer().getInfo() == null) return;

        final HitBoxPlayer hp = hitBoxUtils.getPlayer(e.getPlayer());
        if (hp == null) return;
        if (!hp.isTeamChatAllowed()) return;

        hitBoxUtils.getRedisManager().setTeamUser(hp, e.getServer().getInfo().getName());

    }











}
