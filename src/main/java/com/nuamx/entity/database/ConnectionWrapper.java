package com.nuamx.entity.database;

import lombok.Builder;
import lombok.Getter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Getter
@Builder
public class ConnectionWrapper {

    private DataSource datasource;
    private Connection connection;
    private String databaseName;
    private String schemaName;

    private void startConnection() throws SQLException {
        this.connection = this.datasource.getConnection();
    }

    public Connection getConnection() throws SQLException {
        if (this.connection == null) {
            this.startConnection();
        }
        return this.connection;
    }

    public void closeConnection() throws SQLException {
        if (this.connection != null) {
            this.connection.close();
        }
    }

}
