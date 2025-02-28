package de.netzkronehd.hitboxutils.bungee.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import de.netzkronehd.hitboxutils.database.cache.model.GroupModel;
import de.netzkronehd.hitboxutils.database.cache.model.TeamUserModel;
import de.netzkronehd.hitboxutils.utils.Utils;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.List;
import java.util.stream.Collectors;

public class BungeeUtils extends Utils {

    public static void sendOutGoingData(ServerInfo server, String channel, String... args) {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        for (String arg : args) {
            out.writeUTF(arg);
        }
        server.sendData(channel, out.toByteArray());
    }

    public static TeamUserModel mapTeamUserModel(HitBoxPlayer hp) {
        return new TeamUserModel(hp.getUniqueId(), hp.getName(), hp.getPrefixAndName(), hp.getServerName(), hp.getProxy(), new GroupModel(hp.getPrimaryGroup().getName(), hp.getPrefix(), hp.getPrimaryGroup().getWeight().orElse(0)));
    }

    public static TeamUserModel mapTeamUserModel(HitBoxPlayer hp, String server) {
        return new TeamUserModel(hp.getUniqueId(), hp.getName(), hp.getPrefixAndName(), server, hp.getProxy(), new GroupModel(hp.getPrimaryGroup().getName(), hp.getPrefix(), hp.getPrimaryGroup().getWeight().orElse(0)));
    }

    public static List<TeamUserModel> mapTeamUserModel(List<HitBoxPlayer> players) {
        return players.stream().map(BungeeUtils::mapTeamUserModel).collect(Collectors.toList());
    }


}
