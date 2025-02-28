package de.netzkronehd.hitboxutils.punishmentscore.api.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.netzkronehd.hitboxutils.punishmentscore.PunishmentsCore;

import java.sql.Connection;
import java.sql.SQLException;

public class HikariConnection {
    private final HikariDataSource ds;

    public HikariConnection() {
        final PunishmentsCore plugin = PunishmentsCore.getInstance();
        final String ip = plugin.getConfig().getString("Database.ip");
        final String port = plugin.getConfig().getString("Database.port");
        final String user = plugin.getConfig().getString("Database.user");
        final String password = plugin.getConfig().getString("Database.password");
        final String database = plugin.getConfig().getString("Database.database");
        final int poolSize = plugin.getConfig().getInt("Database.poolSize", 10);
        final boolean useSSL = plugin.getConfig().getBoolean("Database.useSSL");
        final HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:mysql://" + ip + ":" + port + "/" + database);
        config.setUsername(user);
        config.setPassword(password);
        config.addDataSourceProperty("autoReconnect", "true");
        config.addDataSourceProperty("verifyServerCertificate", "false");
        config.addDataSourceProperty("leakDetectionThreshold", "true");
        config.addDataSourceProperty("useSSL", useSSL);
        config.setMaximumPoolSize(poolSize);
        config.setConnectionTimeout(5000L);
        this.ds = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return this.ds.getConnection();
    }

    public void close() {
        if (this.ds != null)
            this.ds.close();
    }
}
