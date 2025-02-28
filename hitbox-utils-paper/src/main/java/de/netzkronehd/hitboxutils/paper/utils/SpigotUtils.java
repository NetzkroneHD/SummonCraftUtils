package de.netzkronehd.hitboxutils.paper.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.netzkronehd.hitboxutils.database.cache.model.GroupModel;
import de.netzkronehd.hitboxutils.database.cache.model.UserModel;
import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.utils.Constants;
import de.netzkronehd.hitboxutils.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageRecipient;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

import static de.netzkronehd.translation.Message.serializeLegacySection;

public class SpigotUtils extends Utils {

    public static void sendOutgoingPluginMessage(Plugin plugin, String channel, PluginMessageRecipient source, String... data) {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        for (String datum : data) {
            out.writeUTF(datum);
        }
        source.sendPluginMessage(plugin, channel, out.toByteArray());
    }

    public static String getDisplayName(ItemStack stack) {
        if (stack == null) return "";
        if (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
            return serializeLegacySection(stack.getItemMeta().displayName());
        } else return stack.getType().name();
    }

    public static Location getTargetLocation(HitBoxPlayer ep, int range) {
        if (range > 120) range = 120;
        final Block targetBlock = ep.getPlayer().getTargetBlockExact(range);
        if (targetBlock == null) return null;
        return targetBlock.getLocation();
    }

    public static Location getTargetLocation(HitBoxPlayer ep, int range, Material... transparent) {
        return ep.getPlayer().getTargetBlock(new HashSet<>(Arrays.asList(transparent)), range).getLocation();
    }

    public static void executeBungeeCommand(HitBoxPlayer hp, String command) {
        sendOutgoingPluginMessage(HitBoxUtils.getInstance(),
                Constants.PluginMessage.BUNGEE_CORD,
                hp.getPlayer(),
                Constants.PluginMessage.PLUGIN_MESSAGE_CHANNEL,
                Constants.PluginMessage.BUNGEE_COMMAND,
                command
        );
    }

    public static boolean isLocationSimilar(Location a, Location b) {
        if(Objects.equals(a, b)) return true;
        if(!Objects.equals(a.getWorld(), b.getWorld())) return false;
        if(!Objects.equals(a.getBlockX(), b.getBlockX())) return false;
        if(!Objects.equals(a.getBlockY(), b.getBlockY())) return false;
        return Objects.equals(a.getBlockZ(), b.getBlockZ());
    }

    public static UserModel mapUserModel(HitBoxPlayer hp) {
        return mapUserModel(hp, HitBoxUtils.getInstance().getRedisManager().getServerName());
    }

    public static UserModel mapUserModel(HitBoxPlayer hp, String server) {
        return new UserModel(hp.getUniqueId(), hp.getName(), hp.getPrefixAndNameRaw(), server, new GroupModel(hp.getPrimaryGroup().getName(), hp.getPrefix(), hp.getPrimaryGroup().getWeight().orElse(0)));
    }

}
