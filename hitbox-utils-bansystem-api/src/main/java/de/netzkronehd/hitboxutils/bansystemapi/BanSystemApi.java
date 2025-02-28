package de.netzkronehd.hitboxutils.bansystemapi;

import de.netzkronehd.hitboxutils.bansystemapi.litebans.LiteBansApi;
import de.netzkronehd.hitboxutils.bansystemapi.litebans.LiteBansApiHolder;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface BanSystemApi {

    static BanSystemApi getApi() {
        return BanSystemApiHolder.getApi();
    }

    static LiteBansApi getLiteBansApi() {
        return LiteBansApiHolder.getApi();
    }

    static LiteBansApi getAdvancedBansApi() {
        return LiteBansApiHolder.getApi();
    }


    default boolean isPunished(PunishmentType type, UUID uuid) {
        if (type == PunishmentType.BAN) {
            return isBanned(uuid);
        } else if (type == PunishmentType.MUTE) {
            return isMuted(uuid);
        } else return false;
    }

    default boolean isBanned(UUID uuid) {
        return isBanned(uuid, null);
    }

    default boolean isBanned(String ip) {
        return isBanned(null, ip);
    }

    boolean isBanned(UUID uuid, String ip);

    default boolean isMuted(UUID uuid) {
        return isMuted(uuid, null);
    }

    default boolean isMuted(String ip) {
        return isMuted(null, ip);
    }

    boolean isMuted(UUID uuid, String ip);

    Map<String, Integer> listBanPunishments(UUID uuid) throws SQLException;

    Map<String, Integer> listBanPunishments(String ip) throws SQLException;

    Map<String, Integer> listMutePunishments(UUID uuid) throws SQLException;

    Map<String, Integer> listMutePunishments(String ip) throws SQLException;

    Collection<UUID> getUsersByIP(String ip);

    String getName();

}
