package de.netzkronehd.hitboxutils.paper.listener;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.player.PlayerPlaytime;
import de.netzkronehd.hitboxutils.utils.Constants;
import de.netzkronehd.hitboxutils.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@RequiredArgsConstructor
public class MessageListener implements PluginMessageListener {

    private final HitBoxUtils hitBoxUtils;

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
        if (!channel.equalsIgnoreCase(Constants.PluginMessage.PLUGIN_MESSAGE_CHANNEL)) return;
        final String[] data = Utils.readData(message);
        if (data.length == 0) return;

        if (data[0].equalsIgnoreCase(Constants.PluginMessage.SOUND)) {
            try {
                final HitBoxPlayer hp = hitBoxUtils.getPlayer(UUID.fromString(data[4]));
                if(hp == null) return;
                hp.playSound(Sound.valueOf(data[1].toUpperCase()), Float.parseFloat(data[2]), Float.parseFloat(data[3]));
            } catch (Exception ex) {
                hitBoxUtils.getLogger().warning("Could not play sound for '"+data[4]+"': " + ex);
            }
        } else if (data[0].equalsIgnoreCase(Constants.PluginMessage.PLAYTIME_ANSWER)) {
            final UUID uuid = UUID.fromString(data[1]);
            final long playtime = Long.parseLong(data[2]);
            final long timeJoined = Long.parseLong(data[3]);

            final HitBoxPlayer hp = hitBoxUtils.getPlayer(uuid);
            if (hp == null) return;
            if (hp.getPlayerPlaytime() == null) hp.setPlayerPlaytime(PlayerPlaytime.builder()
                    .uuid(hp.getUniqueId())
                    .name(hp.getName())
                    .build());

            hp.getPlayerPlaytime().setPlaytime(playtime);
            hp.getPlayerPlaytime().setTimeJoined(timeJoined);

        }


    }
}
