package com.nuamx.repository;

import com.nuamx.config.property.DatabasesProperty;
import com.nuamx.entity.database.ConnectionWrapper;
import com.nuamx.entity.database.SourceDatabase;
import com.nuamx.entity.property.Database;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
@Getter
@RequiredArgsConstructor
public class DatabaseDao {

    private final SourceDatabase sourceDatabase;
    private final DatabasesProperty databasesProperty;

    public ConnectionWrapper configureSourceConnection() {
        return ConnectionWrapper.builder()
                .datasource(sourceDatabase.getDatabase())
                .databaseName(sourceDatabase.getDatabaseName())
                .schemaName(sourceDatabase.getSchema())
                .build();
    }

    public Database getDatabaseTarget(String key) {
        List<Database> targetDatabase = databasesProperty.getTarget();
        return targetDatabase
                .stream()
                .filter(target -> key.contains(target.getKey()))
                .findAny().orElse(null);
    }

}
