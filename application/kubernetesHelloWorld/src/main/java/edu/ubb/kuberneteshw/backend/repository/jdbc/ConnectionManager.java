package edu.ubb.kuberneteshw.backend.repository.jdbc;

import org.springframework.beans.factory.annotation.Value;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;


public class ConnectionManager {
    private static final Integer POOL_SIZE = 3;

    private static ConnectionManager instance;
    private final List<Connection> pool;
    @Value("${DB_DRIVER}")
    private final String DB_DRIVER = "com.mysql.jdbc.Driver";
    @Value("${DB_URL}")
    private final String DB_URL = "jdbc:mysql://localhost:3306/kubernetes?allowPublicKeyRetrieval=true&useSSL=false";
    @Value("${DB_USER}")
    private final String DB_USER = "kub";
    @Value("${DB_PASSWORD}")

    private final String DB_PASSWORD = "Kuber123";

    private ConnectionManager() {
        try {
            pool = new LinkedList<>();
            Class.forName(DB_DRIVER);
            for (int i = 0; i < 10; i++) {
                pool.add(
                        DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)
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
