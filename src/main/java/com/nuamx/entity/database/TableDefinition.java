package com.nuamx.entity.database;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TableDefinition {

    private Integer ordinalPosition;
    private String tableName;
    private String columnName;
    private String dataType;
    private String constraintType;
    private boolean isPrimaryKey;
    private boolean isNullable;
    private String columnDefinition;
    private Integer characterMaximumLength;
    private Integer numericPrecision;
    private Integer numericScale;

}
