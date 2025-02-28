package de.netzkronehd.hitboxutils.paper.manager;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.paper.utils.Items;
import de.netzkronehd.hitboxutils.utils.Constants;
import lombok.Getter;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.UUID;

import static de.netzkronehd.translation.Message.formatColoredValue;
import static de.netzkronehd.translation.Message.serializeLegacySection;

@Getter
public class VanishManager extends Manager {

    private final HashMap<UUID, HitBoxPlayer> vanishedPlayers;
    private String vanishedSuffix;

    public VanishManager(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils);
        vanishedPlayers = new HashMap<>();
    }

    @Override
    public void onLoad() {
        createFile();
        readFile();
    }

    @Override
    public void createFile() {
        if(!file.exists()) {
            cfg.set("vanished-suffix", "&8[&#ff00a6Vanished&8]");
            save();
        }
    }

    @Override
    public void readFile() {
        vanishedSuffix = serializeLegacySection(formatColoredValue(cfg.getString("vanished-suffix", "&8[&#ff00a6Vanished&8]")));
    }

    public boolean isVanished(HitBoxPlayer ep) {
        return vanishedPlayers.containsKey(ep.getUniqueId());
    }

    public void vanishPlayer(HitBoxPlayer ep) {
        for (HitBoxPlayer player : hitBox.getPlayers()) {
            if (player.isStaff()) {
                player.getPlayer().showPlayer(hitBox, ep.getPlayer());
            } else {
                player.getPlayer().hidePlayer(hitBox, ep.getPlayer());
            }
        }
        if (ep.isSpectating()) {
            ep.getSpectateMode().setVanished(true);
            ep.getPlayer().getInventory().setItem(Items.SpectateMode.VANISHED.slot(), Items.SpectateMode.VANISHED.item());
        }
        vanishedPlayers.put(ep.getUniqueId(), ep);
        ep.getPlayer().setMetadata(Constants.META_DATA_KEY_VANISHED, new FixedMetadataValue(hitBox, true));
        ep.getPlayer().setInvisible(true);
        ep.getPlayer().setGlowing(true);
        ep.getPlayer().setCollidable(false);
        ep.getPlayer().setCustomNameVisible(true);
    }

    public void showPlayer(HitBoxPlayer ep) {
        for (HitBoxPlayer player : hitBox.getPlayers()) {
            player.getPlayer().showPlayer(hitBox, ep.getPlayer());
        }
        if (ep.isSpectating()) ep.getSpectateMode().setVanished(false);
        vanishedPlayers.remove(ep.getUniqueId());
        ep.getPlayer().removeMetadata(Constants.META_DATA_KEY_VANISHED, hitBox);
        ep.getPlayer().setCollidable(true);
        ep.getPlayer().setInvisible(false);
        ep.getPlayer().setGlowing(false);
    }

    public HashMap<UUID, HitBoxPlayer> getVanishedPlayers() {
        return vanishedPlayers;
    }
}
