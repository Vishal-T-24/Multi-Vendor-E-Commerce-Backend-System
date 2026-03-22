package org.example.Util;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {

    private static HikariDataSource dataSource;

    static {
        HikariConfig config=new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/Clothing_Store");
        config.setUsername("postgres");
        config.setPassword("Vishal@2004");

       config.setMaximumPoolSize(10);
       config.setMinimumIdle(5);
       config.setConnectionTimeout(30000);
       config.setIdleTimeout(300000);
       config.setMaxLifetime(3000000);

       dataSource=new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
