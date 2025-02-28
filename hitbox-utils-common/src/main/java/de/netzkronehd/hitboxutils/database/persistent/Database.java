package de.netzkronehd.hitboxutils.database.persistent;

import de.netzkronehd.hitboxutils.discord.DiscordBoost;
import de.netzkronehd.hitboxutils.discord.verification.DiscordVerification;
import de.netzkronehd.hitboxutils.player.ChatLogEntry;
import de.netzkronehd.hitboxutils.player.PlayerInfo;
import de.netzkronehd.hitboxutils.player.PlayerPlaytime;
import de.netzkronehd.hitboxutils.player.StaffSettings;
import de.netzkronehd.hitboxutils.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.javatuples.KeyValue;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Setter
public abstract class Database {

    protected String host, database, user, password;
    protected int port;
    @Getter
    protected Connection connection;

    public Database(String host, String database, String user, String password, int port) {
        this.host = host;
        this.database = database;
        this.user = user;
        this.password = password;
        this.port = port;
    }

    public void createTables() throws SQLException {
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS hit_box_players(uuid VARCHAR(64), name TEXT, lastJoin BIGINT, PRIMARY KEY (uuid))").executeUpdate();
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS hit_box_join_sounds(uuid VARCHAR(64), sound TEXT, PRIMARY KEY (uuid))").executeUpdate();
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS hit_box_boosts(memberId BIGINT, timestamp BIGINT, uuid VARCHAR(64), accepted BOOLEAN, PRIMARY KEY (memberId, timestamp))").executeUpdate();
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS hit_box_playtime(uuid VARCHAR(64), playtime BIGINT, timeJoined BIGINT, PRIMARY KEY (uuid))").executeUpdate();
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS hit_box_punishments(uuid VARCHAR(64), reason TEXT)").executeUpdate();
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS hit_box_staff_settings(uuid VARCHAR(64), settings TEXT, PRIMARY KEY (uuid))").executeUpdate();
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS hit_box_chat_logs(uuid VARCHAR(64), name TEXT, timestamp BIGINT, server TEXT, message TEXT, PRIMARY KEY(uuid, timestamp))").executeUpdate();
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS hit_box_discord_verification(uuid VARCHAR(64), discordId  VARCHAR(128) UNIQUE, discordRoleId TEXT, minecraftRoleId TEXT, PRIMARY KEY (uuid))").executeUpdate();
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS hit_box_player_info(uuid VARCHAR(64), lastJoin INTEGER, lastQuit INTEGER, lastProxy TEXT, lastIp TEXT, PRIMARY KEY (uuid))").executeUpdate();

    }

    public boolean existsPlayerInfo(UUID uuid) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT * FROM hit_box_player_info WHERE uuid = ?");
        ps.setString(1, uuid.toString());
        return ps.executeQuery().next();
    }

    public void setPlayerInfo(UUID uuid, PlayerInfo info) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("UPDATE hit_box_player_info SET lastJoin=?, lastQuit=?, lastProxy=?, lastIp=? WHERE uuid=?");
        ps.setLong(1, info.getLastJoin());
        ps.setLong(2, info.getLastQuit());
        ps.setString(3, info.getLastProxy());
        ps.setString(4, Base64.getEncoder().encodeToString(info.getLastIp().getBytes(StandardCharsets.UTF_8)));
        ps.setString(5, uuid.toString());
        ps.executeUpdate();
    }

    public Optional<PlayerInfo> getPlayerInfo(UUID uuid) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT * FROM hit_box_player_info WHERE uuid=?");
        ps.setString(1, uuid.toString());
        final ResultSet rs = ps.executeQuery();
        if(!rs.next()) return Optional.empty();
        return Optional.of(new PlayerInfo(uuid, rs.getLong("lastJoin"), rs.getLong("lastQuit"), rs.getString("lastProxy"), new String(Base64.getDecoder().decode(rs.getString("lastIp")))));
    }


    public boolean existsDiscordVerification(UUID uuid) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM hit_box_discord_verification WHERE uuid=?");
        ps.setString(1, uuid.toString());
        final ResultSet rs = ps.executeQuery();
        return rs.next();
    }

    public boolean existsDiscordVerification(String discordId) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT discordId FROM hit_box_discord_verification WHERE discordId=?");
        ps.setString(1, discordId);
        final ResultSet rs = ps.executeQuery();
        return rs.next();
    }

    public void createDiscordVerification(DiscordVerification discordVerification) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("INSERT INTO hit_box_discord_verification(uuid, discordId, discordRoleId, minecraftRoleId) VALUES (?, ?, ?, ?)");
        ps.setString(1, discordVerification.getUuid().toString());
        ps.setString(2, discordVerification.getDiscordId());
        ps.setString(3, discordVerification.getDiscordRoleId());
        ps.setString(4, discordVerification.getMinecraftRoleId());
        ps.executeUpdate();
    }

    public void updateDiscordVerification(DiscordVerification discordVerification) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("UPDATE hit_box_discord_verification SET discordId=?, discordRoleId=?, minecraftRoleId=? WHERE uuid=?");
        ps.setString(1, discordVerification.getDiscordId());
        ps.setString(2, discordVerification.getDiscordRoleId());
        ps.setString(3, discordVerification.getMinecraftRoleId());
        ps.setString(4, discordVerification.getUuid().toString());
        ps.executeUpdate();
    }

    public void deleteVerification(DiscordVerification discordVerification) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("DELETE FROM hit_box_discord_verification WHERE uuid=?");
        ps.setString(1, discordVerification.getUuid().toString());
        ps.executeUpdate();
    }


    public DiscordVerification getDiscordVerification(String discordId) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("""
                SELECT hit_box_discord_verification.uuid,
                       hit_box_discord_verification.discordId,
                       hit_box_discord_verification.discordRoleId,
                       hit_box_discord_verification.minecraftRoleId,
                       hit_box_players.name

                FROM hit_box_discord_verification

                INNER JOIN hit_box_players on hit_box_discord_verification.uuid = hit_box_players.uuid

                WHERE hit_box_discord_verification.discordId = ?""");

        ps.setString(1, discordId);

        final ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new DiscordVerification(
                    UUID.fromString(rs.getString("uuid")),
                    rs.getString("name"),
                    rs.getString("discordId"),
                    rs.getString("discordRoleId"),
                    rs.getString("minecraftRoleId")
            );
        }
        return null;
    }

    public DiscordVerification getDiscordVerification(UUID uuid) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("""
                SELECT hit_box_discord_verification.uuid,
                       hit_box_discord_verification.discordId,
                       hit_box_discord_verification.discordRoleId,
                       hit_box_discord_verification.minecraftRoleId,
                       hit_box_players.name

                FROM hit_box_discord_verification

                INNER JOIN hit_box_players on hit_box_discord_verification.uuid = hit_box_players.uuid

                WHERE hit_box_discord_verification.uuid = ?""");

        ps.setString(1, uuid.toString());

        final ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new DiscordVerification(
                    UUID.fromString(rs.getString("uuid")),
                    rs.getString("name"),
                    rs.getString("discordId"),
                    rs.getString("discordRoleId"),
                    rs.getString("minecraftRoleId")
            );
        }
        return null;
    }

    public void createChatLog(UUID uuid, String name, long timestamp, String server, String message) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("INSERT INTO hit_box_chat_logs(uuid, name, timestamp, server, message) VALUES (?, ?, ?, ?, ?)");
        ps.setString(1, uuid.toString());
        ps.setString(2, name);
        ps.setLong(3, timestamp);
        ps.setString(4, server);
        ps.setString(5, message);
        ps.executeUpdate();
    }

    public List<ChatLogEntry> getChatLogsFindMessages(String server, String message) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT uuid, name, timestamp, server, message FROM hit_box_chat_logs WHERE lower(server)=? AND lower(message) LIKE ? ORDER BY timestamp DESC");
        ps.setString(1, server);
        ps.setString(2, message);
        final ResultSet rs = ps.executeQuery();

        final List<ChatLogEntry> logs = new ArrayList<>();
        while (rs.next()) {
            logs.add(ChatLogEntry.builder()
                    .uuid(UUID.fromString(rs.getString("uuid")))
                    .name(rs.getString("name"))
                    .timestamp(rs.getLong("timestamp"))
                    .server(rs.getString("server"))
                    .message(rs.getString("message"))
                    .build());
        }
        return logs;
    }

    public List<ChatLogEntry> getChatLogsFindMessages(UUID uuid, String message) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT name, timestamp, server, message FROM hit_box_chat_logs WHERE uuid=? AND lower(message) LIKE ? ORDER BY timestamp DESC");
        ps.setString(1, uuid.toString());
        ps.setString(2, message);
        final ResultSet rs = ps.executeQuery();

        final List<ChatLogEntry> logs = new ArrayList<>();
        while (rs.next()) {
            logs.add(ChatLogEntry.builder()
                    .uuid(uuid)
                    .name(rs.getString("name"))
                    .timestamp(rs.getLong("timestamp"))
                    .server(rs.getString("server"))
                    .message(rs.getString("message"))
                    .build());
        }
        return logs;
    }

    public List<ChatLogEntry> getChatLogsBetweenTime(String server, long from, long to) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT uuid, name, timestamp, server, message FROM hit_box_chat_logs WHERE lower(server)=? AND timestamp < ? AND timestamp > ? ORDER BY timestamp DESC");
        ps.setString(1, server);
        ps.setLong(2, to);
        ps.setLong(3, from);
        final ResultSet rs = ps.executeQuery();

        final List<ChatLogEntry> logs = new ArrayList<>();
        while (rs.next()) {
            logs.add(ChatLogEntry.builder()
                    .uuid(UUID.fromString(rs.getString("uuid")))
                    .name(rs.getString("name"))
                    .timestamp(rs.getLong("timestamp"))
                    .server(rs.getString("server"))
                    .message(rs.getString("message"))
                    .build());
        }
        return logs;
    }

    public List<ChatLogEntry> getChatLogsBetweenTime(UUID uuid, long from, long to) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT name, timestamp, server, message FROM hit_box_chat_logs WHERE uuid=? AND timestamp < ? AND timestamp > ? ORDER BY timestamp DESC");
        ps.setString(1, uuid.toString());
        ps.setLong(2, to);
        ps.setLong(3, from);
        final ResultSet rs = ps.executeQuery();

        final List<ChatLogEntry> logs = new ArrayList<>();
        while (rs.next()) {
            logs.add(ChatLogEntry.builder()
                    .uuid(uuid)
                    .name(rs.getString("name"))
                    .timestamp(rs.getLong("timestamp"))
                    .server(rs.getString("server"))
                    .message(rs.getString("message"))
                    .build());
        }
        return logs;
    }

    public ChatLogEntry getChatLog(UUID uuid, long timestamp) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT uuid, name, timestamp, server, message FROM hit_box_chat_logs WHERE uuid=? AND timestamp=? ORDER BY timestamp DESC");
        ps.setString(1, uuid.toString());
        ps.setLong(2, timestamp);
        final ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return ChatLogEntry.builder()
                    .uuid(uuid)
                    .name(rs.getString("name"))
                    .timestamp(timestamp)
                    .server(rs.getString("server"))
                    .message(rs.getString("message"))
                    .build();
        }
        return null;
    }

    public int createPunishment(UUID uuid, String type, String reason) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("INSERT INTO hit_box_punishments(uuid, reason) VALUES (?, ?)");
        ps.setString(1, uuid.toString());
        ps.setString(2, reason);
        return ps.executeUpdate();
    }

    public boolean hasSettings(UUID uuid) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM hit_box_staff_settings WHERE uuid =?");
        ps.setString(1, uuid.toString());
        return ps.executeQuery().next();
    }

    public StaffSettings getSettings(UUID uuid) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT settings FROM hit_box_staff_settings WHERE uuid =?");
        ps.setString(1, uuid.toString());

        final ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return Utils.GSON.fromJson(rs.getString("settings"), StaffSettings.class);
        } else return new StaffSettings(uuid).reset();

    }

    public StaffSettings saveSettings(UUID uuid, StaffSettings staffSettings) throws SQLException {
        final PreparedStatement ps;
        if (hasSettings(uuid)) {
            ps = connection.prepareStatement("UPDATE hit_box_staff_settings SET settings=? WHERE uuid=?");
            ps.setString(1, Utils.GSON.toJson(staffSettings));
            ps.setString(2, uuid.toString());
        } else {
            ps = connection.prepareStatement("INSERT INTO hit_box_staff_settings(uuid, settings) values (?, ?)");
            ps.setString(1, uuid.toString());
            ps.setString(2, Utils.GSON.toJson(staffSettings));
        }
        ps.executeUpdate();
        return staffSettings;
    }


    public int getPunishments(UUID uuid, String reason) throws SQLException {
        int punishments = 0;

        final PreparedStatement ps = connection.prepareStatement("SELECT reason FROM hit_box_punishments WHERE uuid=? AND lower(reason)=?");
        ps.setString(1, uuid.toString());
        ps.setString(2, reason.toLowerCase());

        final ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            punishments++;
        }
        return punishments;
    }

    public DiscordBoost createBoost(long memberId, long timestamp, UUID uuid, boolean accepted) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("INSERT INTO hit_box_boosts(memberId, timestamp, uuid, accepted) VALUES (?, ?, ?, ?)");
        ps.setLong(1, memberId);
        ps.setLong(2, timestamp);
        ps.setObject(2, uuid);
        ps.setBoolean(3, accepted);
        ps.executeUpdate();
        return new DiscordBoost(memberId, timestamp, uuid, accepted);
    }

    public boolean hasUnclaimedDiscordBoosts(long memberId) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT memberId FROM hit_box_boosts WHERE memberId=? AND accepted=?");
        ps.setLong(1, memberId);
        ps.setBoolean(2, false);
        return ps.executeQuery().next();
    }

    public boolean existsDiscordBoost(long memberId) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT memberId FROM hit_box_boosts WHERE memberId=?");
        ps.setLong(1, memberId);
        return ps.executeQuery().next();
    }

    public boolean existsDiscordBoost(long memberId, long timestamp) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT memberId FROM hit_box_boosts WHERE memberId=? AND timestamp=?");
        ps.setLong(1, memberId);
        ps.setLong(2, timestamp);
        return ps.executeQuery().next();
    }

    public void setDiscordBoost(long memberId, DiscordBoost boost) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("UPDATE hit_box_boosts SET accepted=?, uuid=?, timestamp=? WHERE memberId=?");
        ps.setBoolean(1, boost.isAccepted());
        ps.setString(2, boost.getUuid().toString());
        ps.setLong(3, boost.getTimestamp());
        ps.setLong(4, memberId);
        ps.executeUpdate();
    }

    @SuppressWarnings("DuplicatedCode")
    public List<DiscordBoost> getUnclaimedDiscordBoosts(UUID uuid) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT timestamp, uuid, accepted FROM hit_box_boosts WHERE uuid=? AND accepted=?");
        ps.setString(1, uuid.toString());
        ps.setBoolean(2, false);

        final ResultSet rs = ps.executeQuery();
        final List<DiscordBoost> boosts = new ArrayList<>();
        while (rs.next()) {
            boosts.add(new DiscordBoost(rs.getLong("memberId"), rs.getLong("timestamp"),
                    UUID.fromString(rs.getString("uuid")), rs.getBoolean("accepted")));
        }
        return boosts;
    }

    @SuppressWarnings("DuplicatedCode")
    public List<DiscordBoost> getDiscordBoosts(UUID uuid) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT timestamp, uuid, accepted FROM hit_box_boosts WHERE memberId=?");
        ps.setString(1, uuid.toString());

        final List<DiscordBoost> boosts = new ArrayList<>();
        final ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            boosts.add(new DiscordBoost(rs.getLong("memberId"), rs.getLong("timestamp"),
                    UUID.fromString(rs.getString("uuid")), rs.getBoolean("accepted")));
        }
        return boosts;
    }

    @SuppressWarnings("DuplicatedCode")
    public List<DiscordBoost> getUnclaimedDiscordBoosts(long memberId) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT timestamp, uuid, accepted FROM hit_box_boosts WHERE memberId=? AND accepted=?");
        ps.setLong(1, memberId);
        ps.setBoolean(2, false);

        final ResultSet rs = ps.executeQuery();
        final List<DiscordBoost> boosts = new ArrayList<>();
        while (rs.next()) {
            boosts.add(new DiscordBoost(rs.getLong("memberId"), rs.getLong("timestamp"),
                    UUID.fromString(rs.getString("uuid")), rs.getBoolean("accepted")));
        }
        return boosts;
    }

    @SuppressWarnings("DuplicatedCode")
    public List<DiscordBoost> getDiscordBoosts(long memberId) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT timestamp, uuid, accepted FROM hit_box_boosts WHERE memberId=?");
        ps.setLong(1, memberId);

        final List<DiscordBoost> boosts = new ArrayList<>();
        final ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            boosts.add(new DiscordBoost(rs.getLong("memberId"), rs.getLong("timestamp"),
                    UUID.fromString(rs.getString("uuid")), rs.getBoolean("accepted")));
        }
        return boosts;
    }


    public void createPlayerSound(UUID uuid, String sound) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("INSERT INTO hit_box_join_sounds(uuid, sound) VALUES (?, ?)");
        ps.setString(1, uuid.toString());
        ps.setString(2, sound);
        ps.executeUpdate();
    }

    public void setPlayerSound(UUID uuid, String sound) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("UPDATE hit_box_join_sounds SET sound=? WHERE uuid=?");
        ps.setString(1, sound);
        ps.setString(2, uuid.toString());
        ps.executeUpdate();
    }

    public String getPlayerSound(UUID uuid) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT sound FROM hit_box_join_sounds WHERE uuid=?");
        ps.setString(1, uuid.toString());
        final ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getString("sound");
        }
        return null;
    }

    public boolean existsPlayerSound(UUID uuid) throws SQLException {
        return getPlayerSound(uuid) != null;
    }

    public void createPlayer(UUID uuid, String name, long lastJoin) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("INSERT INTO hit_box_players (uuid, name, lastJoin) VALUES (?, ?, ?)");
        ps.setString(1, uuid.toString());
        ps.setString(2, name);
        ps.setLong(3, lastJoin);
        ps.executeUpdate();
    }

    public void setName(UUID uuid, String name, long lastJoin) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("UPDATE hit_box_players SET name=?, lastJoin=? WHERE uuid=?");
        ps.setString(1, name);
        ps.setLong(2, lastJoin);
        ps.setString(3, uuid.toString());
        ps.executeUpdate();
    }

    public Long getLastJoin(UUID uuid) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT lastJoin FROM hit_box_players WHERE uuid=?");
        ps.setString(1, uuid.toString());

        final ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getLong("lastJoin");
        }
        return null;
    }

    public boolean existsName(UUID uuid) throws SQLException {
        return getName(uuid) != null;
    }

    public UUID getUuid(String name) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM hit_box_players WHERE lower(name)=?");
        ps.setString(1, name.toLowerCase());

        final ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return UUID.fromString(rs.getString("uuid"));
        }
        return null;
    }

    public KeyValue<UUID, String> getUuidAndName(String name) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT uuid, name FROM hit_box_players WHERE lower(name)=?");
        ps.setString(1, name.toLowerCase());

        final ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return KeyValue.with(UUID.fromString(rs.getString("uuid")), rs.getString("name"));
        }
        return null;
    }

    public String getName(UUID uuid) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT name FROM hit_box_players WHERE uuid=?");
        ps.setString(1, uuid.toString());

        final ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getString("name");
        }
        return null;
    }

    public boolean existsPlaytime(UUID uuid) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM hit_box_playtime WHERE uuid=?");
        ps.setString(1, uuid.toString());
        return ps.executeQuery().next();
    }

    public void createPlaytime(UUID uuid, long playtime, long timeJoined) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("INSERT INTO hit_box_playtime(uuid, playtime, timeJoined) VALUES (?, ?, ?)");
        ps.setString(1, uuid.toString());
        ps.setLong(2, playtime);
        ps.setLong(3, timeJoined);
        ps.executeUpdate();
    }

    public void setPlaytime(UUID uuid, long playtime, long timeJoined) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("UPDATE hit_box_playtime SET playtime=?, timeJoined=? WHERE uuid=?");
        ps.setLong(1, playtime);
        ps.setLong(2, timeJoined);
        ps.setString(3, uuid.toString());
        ps.executeUpdate();
    }

    public PlayerPlaytime getPlaytime(UUID uuid) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT playtime, timeJoined FROM hit_box_playtime WHERE uuid=?");
        ps.setString(1, uuid.toString());

        final ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return PlayerPlaytime.builder()
                    .uuid(uuid)
                    .playtime(rs.getLong("playtime"))
                    .timeJoined(rs.getLong("timeJoined"))
                    .build();
        }
        return PlayerPlaytime.builder().uuid(uuid).playtime(0).timeJoined(System.currentTimeMillis()).build();
    }

    public List<PlayerPlaytime> listPlaytimeDesc(int limit) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("""
                SELECT hit_box_playtime.uuid, hit_box_playtime.playtime, hit_box_playtime.timeJoined, hit_box_players.name
                FROM hit_box_playtime
                         INNER JOIN hit_box_players ON hit_box_playtime.uuid = hit_box_players.uuid
                ORDER BY playtime DESC
                LIMIT ?;
                """);

        ps.setInt(1, limit);

        final ResultSet rs = ps.executeQuery();

        final List<PlayerPlaytime> playtimeRank = new ArrayList<>(rs.getFetchSize());
        while (rs.next()) {
            playtimeRank.add(PlayerPlaytime.builder()
                    .uuid(UUID.fromString(rs.getString("uuid")))
                    .name(rs.getString("name"))
                    .playtime(rs.getLong("playtime"))
                    .timeJoined(rs.getLong("timeJoined"))
                    .build());
        }
        return playtimeRank;
    }

    public abstract void connect() throws SQLException;

    public boolean isConnected() {
        return connection != null;
    }


}
