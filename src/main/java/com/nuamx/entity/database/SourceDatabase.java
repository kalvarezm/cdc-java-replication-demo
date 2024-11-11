package com.nuamx.entity.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.sql.DataSource;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SourceDatabase {

    private String databaseKey;
    private String databaseName;
    private String schema;
    private DataSource database;

}
