package de.netzkronehd.hitboxutils.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import de.netzkronehd.hitboxutils.api.UUIDFetcher;
import de.netzkronehd.hitboxutils.database.cache.packet.teamchat.TeamPlayerJoinPacket;
import de.netzkronehd.hitboxutils.database.cache.packet.teamchat.TeamPlayerLeavePacket;
import de.netzkronehd.hitboxutils.player.PlayerPlaytime;
import de.netzkronehd.hitboxutils.sound.GlobalSound;
import de.netzkronehd.hitboxutils.utils.Utils;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;

import static com.velocitypowered.api.event.ResultedEvent.ComponentResult.denied;
import static de.netzkronehd.hitboxutils.velocity.message.MessageColor.formatColoredValue;
import static de.netzkronehd.hitboxutils.velocity.utils.VeloUtils.mapUserModel;

@RequiredArgsConstructor
public class ConnectListener {

    private final HitBoxUtils hitBoxUtils;

    @Subscribe
    public void onLogin(LoginEvent e) {
        UUIDFetcher.putInCache(e.getPlayer().getUniqueId(), e.getPlayer().getUsername().toLowerCase());
        if (hitBoxUtils.getAltsManager().getConfig().isAllowed()) return;

        if (!hitBoxUtils.getAltsManager().hasAltAccount(Utils.formatIpAddress(e.getPlayer().getRemoteAddress().toString()), e.getPlayer().getUniqueId())) return;

        hitBoxUtils.getLogger().info("{} has been kicked because alt accounts have been found.", e.getPlayer().getUsername());

        e.setResult(denied(formatColoredValue(hitBoxUtils.getAltsManager().getAltsDisabledMessage())));

    }

    @Subscribe
    public void onPostLogin(PostLoginEvent e) {
        final long lastJoin = System.currentTimeMillis();
        hitBoxUtils.runAsync(() -> hitBoxUtils.getPlayerManager().setName(e.getPlayer().getUniqueId(), e.getPlayer().getUsername(), lastJoin));

        hitBoxUtils.getPlayerCache().remove(e.getPlayer().getUniqueId());

        final HitBoxPlayer hp = new HitBoxPlayer(hitBoxUtils, e.getPlayer());
        hitBoxUtils.getPlayerCache().put(e.getPlayer().getUniqueId(), hp);

        hp.setPlayerPlaytime(PlayerPlaytime.builder()
                .uuid(hp.getUniqueId())
                .name(hp.getName())
                .timeJoined(System.currentTimeMillis()).build());

        if (hp.isTeamChatAllowed()) {
            hitBoxUtils.getTeamChatManager().sendTeamChatMessage(hp.getPrefixAndName() + "§7 is now§a online§7.");
            hitBoxUtils.getRedisManager().setTeamUser(hp);
            hitBoxUtils.getRedisManager().sendPacket(new TeamPlayerJoinPacket(hitBoxUtils.getRedisManager().getConfig().getServerName(), mapUserModel(hp)));
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
                    hitBoxUtils.getProxyServer().sendMessage(hitBoxUtils.getSoundManager().getJoinMessage(hp));
                }
            }
        });
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent e) {
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
//        hitBoxUtils.getTeamChatManager().sendTeamChatMessage(hp.getPrefixAndName() + "§7 is now§c offline§7.");
        hitBoxUtils.getRedisManager().sendPacket(new TeamPlayerLeavePacket(hitBoxUtils.getRedisManager().getConfig().getServerName(), mapUserModel(hp)));
        hitBoxUtils.getRedisManager().removeTeamUser(hp.getUniqueId());
    }

}
