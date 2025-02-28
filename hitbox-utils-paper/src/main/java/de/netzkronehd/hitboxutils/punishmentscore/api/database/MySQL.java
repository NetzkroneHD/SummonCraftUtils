package de.netzkronehd.hitboxutils.punishmentscore.api.database;


import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL extends Database {

    public MySQL(String host, String database, String user, String password, int port) {
        super(host, database, user, password, port);
    }

    @Override
    public void connect() throws SQLException {
        setConnection(DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", user, password));
    }

}
