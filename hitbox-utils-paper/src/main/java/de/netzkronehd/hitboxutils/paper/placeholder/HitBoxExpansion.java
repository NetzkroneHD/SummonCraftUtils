package de.netzkronehd.hitboxutils.paper.placeholder;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static de.netzkronehd.hitboxutils.utils.Utils.getRemainingTimeInHours;

@RequiredArgsConstructor
public class HitBoxExpansion extends PlaceholderExpansion {

    private final HitBoxUtils hitBoxUtils;

    @Override
    public @NotNull String getIdentifier() {
        return "hitboxutils";
    }

    @Override
    public @NotNull String getAuthor() {
        return "NetzkroneHD";
    }

    @Override
    public @NotNull String getVersion() {
        return hitBoxUtils.getPluginMeta().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equalsIgnoreCase("console_command")) {
            return hitBoxUtils.getConsoleCommandManager().getTimeUntilNextCommandTime()
                    .map(duration -> getRemainingTimeInHours(duration.toMillis(), "h", "m", "s"))
                    .orElse(null);
        } else if(params.equalsIgnoreCase("vanished")) {
            final HitBoxPlayer hp = hitBoxUtils.getPlayer(player);
            if(hp == null) return "";
            if(!hp.isVanished()) return "";
            return hitBoxUtils.getVanishManager().getVanishedSuffix();
        } else if (params.startsWith("console_command.")) {
            final String timerId = params.split("\\.")[1];
            return hitBoxUtils.getConsoleCommandManager().getTimeUntilNextCommandTime(timerId)
                    .map(duration -> getRemainingTimeInHours(duration.toMillis(), "h", "m", "s"))
                    .orElse(null);
        } else if (params.equalsIgnoreCase("playtime")) {
            final HitBoxPlayer hp = hitBoxUtils.getPlayer(player);
            if (hp == null) return "";
            return hp.getFormattedPlaytime();
        }
        return super.onPlaceholderRequest(player, params);
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        return List.of("hitboxutils_playtime");
    }
}
