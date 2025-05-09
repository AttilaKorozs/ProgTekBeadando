package org.rssreader.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.rssreader.App;

public class DatabaseConnection {

    private static Connection connection;

    public static Connection getConnection() throws SQLException, IOException {
        if (connection == null || connection.isClosed()) {
            Properties props = new Properties();
            try (InputStream input = App.class.getClassLoader().getResourceAsStream("dbConfig.properties")) {
                if (input == null) {
                    System.out.println("Nem található a konfigurációs fájl.");
                    return null;
                }
                props.load(input);
            }
            connection = DriverManager.getConnection(props.getProperty("db.url"), props.getProperty("db.username"), props.getProperty("db.password"));
        }
        return connection;
    }

}
