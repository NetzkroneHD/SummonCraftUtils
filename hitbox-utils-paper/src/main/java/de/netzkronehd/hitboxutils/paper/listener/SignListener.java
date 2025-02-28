package de.netzkronehd.hitboxutils.paper.listener;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import static de.netzkronehd.translation.Message.formatColoredValue;
import static de.netzkronehd.translation.Message.serializeLegacySection;

@AllArgsConstructor
public class SignListener implements Listener {

    private final HitBoxUtils hitBoxUtils;

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        final HitBoxPlayer hp = hitBoxUtils.getPlayer(e.getPlayer());
        if(hp == null) return;
        if(!hp.hasPermission("sign.color")) return;

        for (int i = 0; i < e.lines().size(); i++) {
            final String text = serializeLegacySection(e.line(i));
            e.line(i, formatColoredValue(text));
        }
    }


}
