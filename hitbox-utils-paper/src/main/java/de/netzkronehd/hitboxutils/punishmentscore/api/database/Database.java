package de.netzkronehd.hitboxutils.punishmentscore.api.database;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class Database {

    protected String host, database, user, password;
    protected int port;
    protected Connection connection;

    public Database(String host, String database, String user, String password, int port) {
        this.host = host;
        this.database = database;
        this.user = user;
        this.password = password;
        this.port = port;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public abstract void connect() throws SQLException;

    public boolean isConnected() {
        return connection != null;
    }


}
