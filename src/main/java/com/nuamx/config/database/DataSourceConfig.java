package com.nuamx.config.database;

import com.nuamx.config.property.DatabasesProperty;
import com.nuamx.entity.database.SourceDatabase;
import com.nuamx.entity.database.TargetDatabases;
import com.nuamx.entity.property.Database;
import com.nuamx.entity.property.HikariPool;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class DataSourceConfig {

    private final DatabasesProperty databasesProperty;

    @Bean
    public DataSource jobStoreDatasource() {
        Database database = databasesProperty.getJobStore();
        HikariPool hikariPool = database.getHikari();
        HikariConfig config = getHikariConfig(database, hikariPool);
        return new HikariDataSource(config);
    }

    @Bean
    public SourceDatabase sourceDatabase() {
        Database database = databasesProperty.getSource();
        HikariPool hikariPool = database.getHikari();
        HikariConfig config = getHikariConfig(database, hikariPool);
        return SourceDatabase.builder()
                .databaseKey(database.getKey())
                .databaseName(database.getDatabaseName())
                .schema(database.getSchema())
                .database(new HikariDataSource(config))
                .build();
    }

    @Bean
    public TargetDatabases targetDatabases() {
        Map<String, DataSource> targetDatabases = new HashMap<>();
        List<Database> database = databasesProperty.getTarget();
        database.stream().filter(Database::getActive).forEach(d -> {
            HikariPool hikariPool = d.getHikari();
            HikariConfig config = getHikariConfig(d, hikariPool);
            targetDatabases.put(d.getKey(), new HikariDataSource(config));
        });
        return new TargetDatabases(targetDatabases);
    }

    public static Connection getConnection(HikariDataSource ds) throws SQLException {
        return ds.getConnection();
    }

    private static HikariConfig getHikariConfig(Database database, HikariPool hikariPool) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(database.getUrl());
        config.setUsername(database.getUsername());
        config.setPassword(database.getPassword());
        config.setDriverClassName(database.getDriverClassName());
        config.setConnectionTimeout(hikariPool.getConnectionTimeout());
        config.setMaxLifetime(hikariPool.getMaxLifetime());
        config.setKeepaliveTime(hikariPool.getKeepaliveTime());
        config.setMaximumPoolSize(hikariPool.getMaximumPoolSize());
        config.setMinimumIdle(hikariPool.getMinimumIdle());
        config.setIdleTimeout(hikariPool.getIdleTimeout());
        config.setAutoCommit(hikariPool.getAutoCommit());
        config.setConnectionTestQuery(hikariPool.getConnectionTestQuery());
        config.setPoolName(hikariPool.getPoolName());
        return config;
    }

}
