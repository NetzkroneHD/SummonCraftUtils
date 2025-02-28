package de.netzkronehd.hitboxutils.punishmentscore.api;

import de.netzkronehd.hitboxutils.punishmentscore.api.database.models.Sanction;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Setter
public class LitebansAPI {

    private Connection connection;
    private String tablePrefix;

    public LitebansAPI(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public Map<String, Integer> getAllPunishments(String name) {
        final Map<String, Integer> punishments = new HashMap<>();
        final String query = "SELECT reason, COUNT(*) as count\n" +
                "FROM (SELECT a.reason\n" +
                "      FROM "+tablePrefix+"bans a\n" +
                "      WHERE a.uuid IN (SELECT uuid FROM "+tablePrefix+"history WHERE name = ?)\n" +
                "        AND (a.removed_by_reason IS NULL OR a.removed_by_reason != 'ELIMINADA')\n" +
                "      UNION ALL\n" +
                "      SELECT b.reason\n" +
                "      FROM "+tablePrefix+"mutes b\n" +
                "      WHERE b.uuid IN (SELECT uuid FROM "+tablePrefix+"history WHERE name = ?)\n" +
                "        AND (b.removed_by_reason IS NULL OR b.removed_by_reason != 'ELIMINADA')\n" +
                "      UNION ALL\n" +
                "      SELECT c.reason\n" +
                "      FROM "+tablePrefix+"warnings c\n" +
                "      WHERE c.uuid IN (SELECT uuid FROM "+tablePrefix+"history WHERE name = ?)\n" +
                "        AND (c.removed_by_reason IS NULL OR c.removed_by_reason != 'ELIMINADA')\n" +
                "      UNION ALL\n" +
                "      SELECT d.reason\n" +
                "      FROM "+tablePrefix+"kicks d\n" +
                "      WHERE d.uuid IN (SELECT uuid FROM "+tablePrefix+"history WHERE name = ?)) as d\n" +
                "GROUP BY reason";
        try (PreparedStatement st = connection.prepareStatement(query)) {
            st.setString(1, name);
            st.setString(2, name);
            st.setString(3, name);
            st.setString(4, name);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    String reason = rs.getString("reason");
                    int count = rs.getInt("count");
                    punishments.put(reason, Integer.valueOf(count));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + " Error fetching: " + name + " from LiteBans database");
            return null;
        }
        return punishments;
    }

    public Map<String, Sanction> getLastPunishment(UUID uuid) {
        final Map<String, Sanction> sanctions = new HashMap<>();
        final String query = "SELECT 'ban' as type, id, banned_by_name, until, time, reason\n" +
                "FROM "+tablePrefix+"bans\n" +
                "WHERE uuid = ?\n" +
                "  AND time = (SELECT MAX(time) FROM "+tablePrefix+"bans WHERE uuid = ?)\n" +
                "UNION ALL\n" +
                "SELECT 'mute' as type, id, banned_by_name, until, time, reason\n" +
                "FROM "+tablePrefix+"mutes\n" +
                "WHERE uuid = ?\n" +
                "  AND time = (SELECT MAX(time) FROM "+tablePrefix+"mutes WHERE uuid = ?)\n" +
                "UNION ALL\n" +
                "SELECT 'kick' as type, id, banned_by_name, until, time, reason\n" +
                "FROM "+tablePrefix+"kicks\n" +
                "WHERE uuid = ?\n" +
                "  AND time = (SELECT MAX(time) FROM "+tablePrefix+"kicks WHERE uuid = ?)\n" +
                "UNION ALL\n" +
                "SELECT 'warn' as type, id, banned_by_name, until, time, reason\n" +
                "FROM "+tablePrefix+"warnings\n" +
                "WHERE uuid = ?\n" +
                "  AND time = (SELECT MAX(time) FROM "+tablePrefix+"warnings WHERE uuid = ?)";
        try (final PreparedStatement stm = connection.prepareStatement(query)) {
            for (int i = 1; i <= 8; i++) {
                stm.setString(i, uuid.toString());
            }
            final ResultSet resultSet = stm.executeQuery();
            while (resultSet.next()) {
                String type = resultSet.getString("type");
                int id = resultSet.getInt("id");
                String banned_by_name = resultSet.getString("banned_by_name");
                Long until = Long.valueOf(resultSet.getLong("until"));
                Long time = Long.valueOf(resultSet.getLong("time"));
                String reason = resultSet.getString("reason");
                Sanction sanction = new Sanction(id, banned_by_name, reason, time, until);
                sanctions.put(type, sanction);
            }
            return sanctions;
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + " Error fetching: " + uuid + " from LiteBans database");
            return null;
        }
    }
}
