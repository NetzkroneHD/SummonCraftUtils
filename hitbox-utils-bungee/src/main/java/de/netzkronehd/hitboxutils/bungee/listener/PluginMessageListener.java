package de.netzkronehd.hitboxutils.bungee.listener;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.bungee.utils.BungeeUtils;
import de.netzkronehd.hitboxutils.player.StaffSettings;
import de.netzkronehd.hitboxutils.utils.Utils;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import static de.netzkronehd.hitboxutils.utils.Constants.PluginMessage.*;

@RequiredArgsConstructor
public class PluginMessageListener implements Listener {

    private final HitBoxUtils hitBoxUtils;

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) {
        if(!e.getTag().equalsIgnoreCase(BUNGEE_CORD)) return;
        final String[] data = Utils.readData(e.getData());
        if(data.length == 0) return;
        if(!data[0].equalsIgnoreCase(PLUGIN_MESSAGE_CHANNEL)) return;

        if (data[1].equalsIgnoreCase(BUNGEE_COMMAND)) {
            if (!(e.getSender() instanceof final ProxiedPlayer p)) return;
            if (!PLUGIN_MESSAGE_CHANNEL.equalsIgnoreCase(e.getTag())) return;
            final HitBoxPlayer hp = hitBoxUtils.getPlayer(p);
            if (hp == null) return;
            hitBoxUtils.getProxy().getPluginManager().dispatchCommand(hp.getPlayer(), data[2]);

        } else if (data[1].equalsIgnoreCase(PLAYTIME_REQUEST)) {
            if (!(e.getSender() instanceof final ProxiedPlayer p)) return;
            if (!PLUGIN_MESSAGE_CHANNEL.equalsIgnoreCase(e.getTag())) return;
            final HitBoxPlayer hp = hitBoxUtils.getPlayer(p);
            if (hp == null) return;

            BungeeUtils.sendOutGoingData(hp.getPlayer().getServer().getInfo(), PLUGIN_MESSAGE_CHANNEL,
                    PLAYTIME_ANSWER,
                    hp.getUniqueId().toString(),
                    String.valueOf(hp.getPlayerPlaytime().getPlaytime()),
                    String.valueOf(hp.getPlayerPlaytime().getTimeJoined())
            );
        } else if (data[1].equalsIgnoreCase(STAFF_SETTINGS)) {
            final StaffSettings staffSettings = Utils.GSON.fromJson(data[2], StaffSettings.class);
            final HitBoxPlayer hp = hitBoxUtils.getPlayer(staffSettings.getUuid());
            if(hp == null) return;
            hp.setStaffSettings(staffSettings);
        }
    }

}
