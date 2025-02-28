package de.netzkronehd.hitboxutils.bungee.listener;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

@RequiredArgsConstructor
public class ChatListener implements Listener {

    private final HitBoxUtils hitBoxUtils;

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(ChatEvent e) {
        if (!(e.getSender() instanceof final ProxiedPlayer p)) return;
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(p);
        if (hp == null) return;

        if (e.isCommand()) {
            return;
        }

        if (hp.isTeamChat()) {
            e.setCancelled(true);
            hitBoxUtils.getTeamChatManager().sendTeamChatMessage(hp, e.getMessage());
            hitBoxUtils.getDiscordTeamChatManager().sendMessageInDiscord(hp, e.getMessage());
            return;
        }

        if(e.isCancelled()) return;

        hitBoxUtils.runAsync(() -> hitBoxUtils.getChatFilterManager().createChatLog(hp.getUniqueId(), hp.getName(), System.currentTimeMillis(), hp.getServerName(), e.getMessage()));
    }


}
