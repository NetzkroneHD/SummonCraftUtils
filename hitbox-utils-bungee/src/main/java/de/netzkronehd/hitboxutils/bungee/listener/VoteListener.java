package de.netzkronehd.hitboxutils.bungee.listener;

import com.vexsoftware.votifier.bungee.events.VotifierEvent;
import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.database.cache.model.VoteModel;
import de.netzkronehd.hitboxutils.database.cache.packet.player.PlayerVotePacket;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

@RequiredArgsConstructor
public class VoteListener implements Listener {

    private final HitBoxUtils hitBoxUtils;

    public void register() {
        try {
            Class.forName("com.vexsoftware.votifier.bungee.events.VotifierEvent");
            Class.forName("com.vexsoftware.votifier.model.Vote");
            hitBoxUtils.getProxy().getPluginManager().registerListener(hitBoxUtils, this);
            hitBoxUtils.getLogger().info("VoteListener registered successfully.");
        } catch (ClassNotFoundException e) {
            hitBoxUtils.getLogger().warning("Could not register VoteListener: "+e);
        }
    }

    @EventHandler
    public void onVote(VotifierEvent e) {
        if(hitBoxUtils.getPlayer(e.getVote().getUsername()) == null) return;
        hitBoxUtils.getLogger().info("Sending vote packet '"+e.getVote()+"' to other proxies because player '"+e.getVote().getUsername()+"' is not online on this proxy.");
        hitBoxUtils.getRedisManager().sendPacket(new PlayerVotePacket(
                hitBoxUtils.getRedisManager().getServerName(),
                new VoteModel(
                        e.getVote().getServiceName(),
                        e.getVote().getUsername(),
                        e.getVote().getAddress(),
                        e.getVote().getTimeStamp(),
                        e.getVote().getAdditionalData()
                )
        ));
    }

}
