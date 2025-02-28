package de.netzkronehd.hitboxutils.velocity.utils;

import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.ChannelMessageSink;
import de.netzkronehd.hitboxutils.database.cache.model.GroupModel;
import de.netzkronehd.hitboxutils.database.cache.model.TeamUserModel;
import de.netzkronehd.hitboxutils.utils.Utils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;

import java.util.List;
import java.util.stream.Collectors;

public class VeloUtils extends Utils {

    public static TeamUserModel mapUserModel(HitBoxPlayer hp) {
        return new TeamUserModel(hp.getUniqueId(), hp.getName(), hp.getPrefixAndName(), hp.getServerName(), hp.getProxy(), new GroupModel(hp.getPrimaryGroup().getName(), hp.getPrefix(), hp.getPrimaryGroup().getWeight().orElse(0)));
    }

    public static TeamUserModel mapUserModel(HitBoxPlayer hp, String server) {
        return new TeamUserModel(hp.getUniqueId(), hp.getName(), hp.getPrefixAndName(), server, hp.getProxy(), new GroupModel(hp.getPrimaryGroup().getName(), hp.getPrefix(), hp.getPrimaryGroup().getWeight().orElse(0)));
    }

    public static List<TeamUserModel> mapUserModel(List<HitBoxPlayer> players) {
        return players.stream().map(VeloUtils::mapUserModel).collect(Collectors.toList());
    }

    public static boolean sendPluginMessage(ChannelMessageSink sink, ChannelIdentifier identifier, String... args) {
        return sink.sendPluginMessage(identifier, output -> {
            for (String arg : args) {
                output.writeUTF(arg);
            }
        });
    }

}
