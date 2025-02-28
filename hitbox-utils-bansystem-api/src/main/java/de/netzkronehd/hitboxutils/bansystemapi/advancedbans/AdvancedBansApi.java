package de.netzkronehd.hitboxutils.bansystemapi.advancedbans;

import de.netzkronehd.hitboxutils.bansystemapi.BanSystemApi;
import me.leoko.advancedban.Universal;
import me.leoko.advancedban.manager.PunishmentManager;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class AdvancedBansApi implements BanSystemApi {

    protected AdvancedBansApi() throws ClassNotFoundException {
        Class.forName("me.leoko.advancedban.Universal");
    }

    @Override
    public boolean isBanned(UUID uuid, String ip) {
        if (uuid != null) {
            return PunishmentManager.get().isBanned(Universal.get().getMethods().getInternUUID(uuid));
        } else if (ip != null) {
            return false;
        }
        return false;

    }

    @Override
    public boolean isMuted(UUID uuid, String ip) {
        if (uuid != null) {
            return PunishmentManager.get().isMuted(Universal.get().getMethods().getInternUUID(uuid));
        } else if (ip != null) {
            return false;
        }
        return false;

    }

    @Override
    public Map<String, Integer> listBanPunishments(UUID uuid) {
        return null;
    }

    @Override
    public Map<String, Integer> listBanPunishments(String ip) {
        return null;
    }

    @Override
    public Map<String, Integer> listMutePunishments(UUID uuid) {
        return null;
    }

    @Override
    public Map<String, Integer> listMutePunishments(String ip) {
        return null;
    }

    @Override
    public Collection<UUID> getUsersByIP(String ip) {
        return null;
    }

    @Override
    public String getName() {
        return "AdvancedBans";
    }
}
