package de.netzkronehd.hitboxutils.paper.clickblock;

import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Data
@Builder
public class ClickBlockExecuteCommand implements ConfigurationSerializable {

    private final int id;
    private final String command;
    private boolean console;

    public void execute(HitBoxPlayer hp, ClickBlock clickBlock) {
        final String command = this.command
                .replace("%PLAYER%", hp.getName())
                .replace("%BLOCK%", String.valueOf(clickBlock.getId()));
        if (isConsole()) {
            hp.getHitBoxUtils().getServer().dispatchCommand(hp.getHitBoxUtils().getServer().getConsoleSender(), command);
        } else {
            hp.getPlayer().performCommand(command);
        }
    }

    @NotNull
    public static ClickBlockExecuteCommand deserialize(@NotNull Map<String, Object> args) {
        return new ClickBlockExecuteCommand(NumberConversions.toInt(args.get("id")), args.get("command").toString(), Boolean.parseBoolean(args.get("console").toString()));
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        final Map<String, Object> data = new HashMap<>();
        data.put("id", this.id);
        data.put("command", this.command);
        data.put("console", this.console);
        return data;
    }

}
