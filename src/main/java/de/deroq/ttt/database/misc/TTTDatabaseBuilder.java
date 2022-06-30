package de.deroq.ttt.database.misc;

import de.deroq.ttt.TTT;
import de.deroq.ttt.database.TTTDatabase;

public class TTTDatabaseBuilder {

    private final TTTDatabase tttDatabase;

    public TTTDatabaseBuilder(TTT ttt) {
        this.tttDatabase = new TTTDatabase(ttt);
    }

    public TTTDatabaseBuilder setHost(String host) {
        tttDatabase.setHost(host);
        return this;
    }

    public TTTDatabaseBuilder setUsername(String username) {
        tttDatabase.setUsername(username);
        return this;
    }

    public TTTDatabaseBuilder setDatabase(String database) {
        tttDatabase.setDatabase(database);
        return this;
    }

    public TTTDatabaseBuilder setPassword(String password) {
        tttDatabase.setPassword(password);
        return this;
    }

    public TTTDatabaseBuilder setPort(int port) {
        tttDatabase.setPort(port);
        return this;
    }

    public TTTDatabase build() {
        return tttDatabase;
    }
}
