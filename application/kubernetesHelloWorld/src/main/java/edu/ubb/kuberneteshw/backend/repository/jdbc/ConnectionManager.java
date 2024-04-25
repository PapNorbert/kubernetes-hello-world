package edu.ubb.kuberneteshw.backend.repository.jdbc;

import edu.ubb.kuberneteshw.backend.config.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;


public class ConnectionManager {
    private static final Integer POOL_SIZE = 3;
    private static ConnectionManager instance;
    private final List<Connection> pool;

    private ConnectionManager() {
        try {
            pool = new LinkedList<>();
            Class.forName(Configuration.getDbDriver());
            for (int i = 0; i < 10; i++) {
                pool.add(
                        DriverManager.getConnection(Configuration.getDbUrl(), Configuration.getDbUser(),
                                Configuration.getDbPassword())
                );
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Error creating database connection pool", e);
        }
    }

    public synchronized Connection getConnection() {
        if (pool.isEmpty()) {
            return null;
        }
        Connection connection = pool.get(0);
        pool.remove(0);
        return connection;
    }

    public synchronized void returnConnection(Connection connection) {
        if (pool.size() < POOL_SIZE) {
            pool.add(connection);
        }
    }

    public static synchronized ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }


}
