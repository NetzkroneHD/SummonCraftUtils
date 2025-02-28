package de.netzkronehd.hitboxutils.bungee.manager;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.database.persistent.Database;
import de.netzkronehd.hitboxutils.database.persistent.MySQL;
import de.netzkronehd.hitboxutils.database.persistent.SQLite;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;

@Getter
public class DatabaseManager extends Manager {

    private Database database;

    public DatabaseManager(HitBoxUtils hitBox) {
        super(hitBox);
        setPriority(1);
    }

    @Override
    public void onLoad() {
        createFile();
        readFile();
    }

    @Override
    public void createFile() {
        if (!file.exists()) {
            final File dbFile = new File(file.getParentFile().getAbsolutePath(), "database.db");
            cfg.set("Use-MySQL", false);
            cfg.set("SQLite-File", dbFile.getAbsolutePath());
            cfg.set("MySQL.host", "localhost");
            cfg.set("MySQL.database", "hitbox");
            cfg.set("MySQL.user", "root");
            cfg.set("MySQL.password", "password");
            cfg.set("MySQL.port", 3306);
            save();
        }
    }

    @Override
    public void readFile() {
        if (cfg.getBoolean("Use-MySQL")) {
            database = new MySQL(cfg.getString("MySQL.host"), cfg.getString("MySQL.database"), cfg.getString("MySQL.user"), cfg.getString("MySQL.password"), cfg.getInt("MySQL.port"));
            try {
                log("Using MySQL...");
                database.connect();
                database.createTables();

            } catch (SQLException e) {
                log(Level.SEVERE, "Cloud not create MySQL-Connection: " + e);
            }
        } else {
            final File dbFile = new File(Objects.requireNonNull(cfg.getString("SQLite-File")));
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
    }

}
