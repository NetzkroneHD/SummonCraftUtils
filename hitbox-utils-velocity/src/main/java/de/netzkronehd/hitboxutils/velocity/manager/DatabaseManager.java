package de.netzkronehd.hitboxutils.velocity.manager;

import de.netzkronehd.hitboxutils.database.persistent.Database;
import de.netzkronehd.hitboxutils.database.persistent.MySQL;
import de.netzkronehd.hitboxutils.database.persistent.SQLite;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import lombok.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

@Getter
public class DatabaseManager extends Manager {

    private Database database;
    private DatabaseManagerConfig config;

    public DatabaseManager(HitBoxUtils hitBox) {
        super(hitBox);
        setPriority(1);
        this.config = new DatabaseManagerConfig();
    }

    @Override
    public void onLoad() {
        createFile();
        readFile();
    }

    @Override
    public void createFile() {
        if (!file.exists()) {
            config = new DatabaseManagerConfig().loadDefaults(new File(file.getParentFile().getAbsolutePath(), "database.db"));
            save(config);
        }
    }

    @Override
    public void readFile() {
        config = getConfigJson(DatabaseManagerConfig.class);
        if (config.useMySQL) {
            database = new MySQL(config.host, config.database, config.username, config.password, config.port);
            try {
                log("Using MySQL...");
                Class.forName("com.mysql.cj.jdbc.Driver");
                database.connect();
                database.createTables();

            } catch (SQLException | ClassNotFoundException e) {
                log(Level.SEVERE, "Cloud not create MySQL-Connection: " + e);
            }
        } else {
            final File dbFile = new File(config.sqlLiteFile);
            if (!dbFile.exists()) {
                if (!dbFile.getParentFile().exists()) {
                    if (!dbFile.getParentFile().mkdirs()) {
                        log("Could not create File '" + dbFile.getParentFile().getAbsolutePath() + "'.");
                    }
                }
                try {
                    if (!dbFile.createNewFile()) {
                        log(Level.WARNING, "Could not create Database-File");
                    }
                } catch (IOException e) {
                    log(Level.SEVERE, "Cloud not create SQLite database-file: " + e);
                }
            }
            database = new SQLite("0", dbFile.getAbsolutePath(), "", "", 3306);
            try {
                log("Using SQLite...");
                database.connect();
                database.createTables();
            } catch (SQLException e) {
                log(Level.SEVERE, "Cloud not create SQLite-Connection: " + e);
            }
        }
        config = null;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DatabaseManagerConfig extends ManagerConfig {

        private boolean useMySQL;
        private String sqlLiteFile;
        private String host, database, username, password;
        private int port;

        @Override
        public DatabaseManagerConfig loadDefaults() {
            return this;
        }

        public DatabaseManagerConfig loadDefaults(File sqlLiteFile) {
            this.useMySQL = false;
            this.sqlLiteFile = sqlLiteFile.getAbsolutePath();
            this.host = "localhost";
            this.database = "hitbox";
            this.username = "root";
            this.password = "password";
            this.port = 3306;
            return this;
        }
    }

}
