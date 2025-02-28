package de.netzkronehd.hitboxutils.bansystemapi.litebans;

import de.netzkronehd.hitboxutils.bansystemapi.BanSystemApi;
import litebans.api.Database;
import litebans.api.Events;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LiteBansApi implements BanSystemApi {

    protected LiteBansApi() throws ClassNotFoundException {
        Class.forName("litebans.api.Database");
    }

    public void registerEvents(Events.Listener listener) {
        Events.get().register(listener);
    }

    @Override
    public boolean isBanned(UUID uuid, String ip) {
        return getDatabase().isPlayerBanned(uuid, ip);
    }

    @Override
    public boolean isMuted(UUID uuid, String ip) {
        return getDatabase().isPlayerMuted(uuid, ip);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public Map<String, Integer> listBanPunishments(UUID uuid) throws SQLException {
        // language=SQL
        final PreparedStatement ps = getDatabase().prepareStatement("SELECT reason, COUNT(reason) as count FROM litebans_bans WHERE uuid=? GROUP BY reason");
        ps.setString(1, uuid.toString());
        final ResultSet rs = ps.executeQuery();

        final Map<String, Integer> punishments = new HashMap<>();
        while (rs.next()) {
            punishments.put(rs.getString("reason").toLowerCase(), rs.getInt("count"));
        }
        ps.close();
        rs.close();
        return punishments;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public Map<String, Integer> listBanPunishments(String ip) throws SQLException {
        // language=SQL
        final PreparedStatement ps = getDatabase().prepareStatement("SELECT reason, COUNT(reason) as count FROM litebans_bans WHERE ip=? GROUP BY reason");
        ps.setString(1, ip);
        final ResultSet rs = ps.executeQuery();
        final Map<String, Integer> punishments = new HashMap<>();
        while (rs.next()) {
            punishments.put(rs.getString("reason").toLowerCase(), rs.getInt("count"));
        }
        ps.close();
        rs.close();
        return punishments;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public Map<String, Integer> listMutePunishments(UUID uuid) throws SQLException {
        // language=SQL
        final PreparedStatement ps = getDatabase().prepareStatement("SELECT reason, COUNT(reason) as count FROM litebans_mutes WHERE uuid=? GROUP BY reason");
        ps.setString(1, uuid.toString());
        final ResultSet rs = ps.executeQuery();

        final Map<String, Integer> punishments = new HashMap<>();
        while (rs.next()) {
            punishments.put(rs.getString("reason").toLowerCase(), rs.getInt("count"));
        }
        ps.close();
        rs.close();
        return punishments;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public Map<String, Integer> listMutePunishments(String ip) throws SQLException {
        // language=SQL
        final PreparedStatement ps = getDatabase().prepareStatement("SELECT reason, COUNT(reason) as count FROM litebans_mutes WHERE ip=? GROUP BY reason");
        ps.setString(1, ip);
        final ResultSet rs = ps.executeQuery();
        final Map<String, Integer> punishments = new HashMap<>();
        while (rs.next()) {
            punishments.put(rs.getString("reason").toLowerCase(), rs.getInt("count"));
        }
        ps.close();
        rs.close();
        return punishments;
    }

    @Override
    public Collection<UUID> getUsersByIP(String ip) {
        return getDatabase().getUsersByIP(ip);
    }

    @Override
    public String getName() {
        return "LiteBans";
    }

    public Database getDatabase() {
        return Database.get();
    }
}
