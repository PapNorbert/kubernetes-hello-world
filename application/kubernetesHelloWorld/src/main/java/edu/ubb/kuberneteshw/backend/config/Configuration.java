package edu.ubb.kuberneteshw.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Configuration {

    private static String DB_DRIVER;
    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;


    @Value("${DB_DRIVER}")
    public void setDbDriver(String dbDriver) {
        DB_DRIVER = dbDriver;
    }

    @Value("${DB_URL}")
    public void setDbUrl(String dbUrl) {
        DB_URL = dbUrl;
    }

    @Value("${DB_USER}")
    public void setDbUser(String dbUser) {
        DB_USER = dbUser;
    }

    @Value("${DB_PASSWORD}")
    public void setDbPassword(String dbPassword) {
        DB_PASSWORD = dbPassword;
    }

    public static String getDbDriver() {
        return DB_DRIVER;
    }

    public static String getDbUrl() {
        return DB_URL;
    }

    public static String getDbUser() {
        return DB_USER;
    }

    public static String getDbPassword() {
        return DB_PASSWORD;
    }
}
