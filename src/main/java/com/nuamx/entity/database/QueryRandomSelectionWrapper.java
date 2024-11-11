package com.nuamx.entity.database;

import com.nuamx.entity.enums.DataTypeEnum;
import com.nuamx.entity.enums.OperationCDCTypeEnum;
import com.nuamx.entity.enums.ResultSetTypeEnum;
import com.nuamx.util.RandomUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Getter
@Setter
@Builder
public class QueryRandomSelectionWrapper {

    private OperationCDCTypeEnum cdcOperationType;
    private OperationCDCTypeEnum finalOperationType;
    private StringBuilder insert;
    private StringBuilder update;
    private StringBuilder delete;

    public static StringBuilder initInsert(String tableTarget, List<TableDefinition> tableColumns) {
        StringBuilder insertSQLBase = new StringBuilder("INSERT INTO " + tableTarget + " (");
        tableColumns.forEach(tc -> {
            insertSQLBase.append(tc.getColumnName()).append(",");
        });
        return new StringBuilder(insertSQLBase.substring(0, insertSQLBase.length() - 1)).append(") VALUES (");
    }

    public static StringBuilder initDelete(String tableTarget) {
        return new StringBuilder("DELETE FROM " + tableTarget + " WHERE ");
    }

    public static StringBuilder initUpdate(String tableTarget) {
        return new StringBuilder("UPDATE " + tableTarget + " SET ");
    }

    public static void modifyInsert(TableDefinition t, StringBuilder insertSQL) throws SQLException {
        ResultSetTypeEnum typeEnum = DataTypeEnum.byType(t.getDataType()).getResultSetType();
        switch (typeEnum) {
            case STRING -> insertSQL
                    .append("'")
                    .append(RandomUtil.randomGenerator(t.getCharacterMaximumLength()))
                    .append("'");
            case DECIMAL -> insertSQL
                    .append(RandomUtil.randomBigDecimal(t.getNumericPrecision(), t.getNumericScale())
                            .toPlainString());
            default -> throw new IllegalStateException("Unexpected value: " + typeEnum);
        }
        insertSQL.append(",");
    }

    public static void modifyDelete(TableDefinition t, StringBuilder deleteSQL) throws SQLException {
        ResultSetTypeEnum typeEnum = DataTypeEnum.byType(t.getDataType()).getResultSetType();
        if (t.isPrimaryKey()) {
            switch (typeEnum) {
                case STRING -> deleteSQL
                        .append(t.getColumnName())
                        .append("=")
                        .append("'")
                        .append(RandomUtil.randomGenerator(t.getCharacterMaximumLength()))
                        .append("'");
                case DECIMAL -> deleteSQL
                        .append(t.getColumnName())
                        .append("=")
                        .append(RandomUtil.randomBigDecimal(t.getNumericPrecision(), t.getNumericScale())
                                .toPlainString());
                default -> throw new IllegalStateException("Unexpected value: " + typeEnum);
            }
            deleteSQL.append(" AND ");
        }
    }

    public static void modifyUpdate(TableDefinition t, StringBuilder updateSQL) throws SQLException {
        ResultSetTypeEnum typeEnum = DataTypeEnum.byType(t.getDataType()).getResultSetType();
        switch (typeEnum) {
            case STRING -> updateSQL
                    .append(t.getColumnName())
                    .append("=")
                    .append("'")
                    .append(RandomUtil.randomGenerator(t.getCharacterMaximumLength()))
                    .append("'");
            case DECIMAL -> updateSQL
                    .append(t.getColumnName())
                    .append("=")
                    .append(RandomUtil.randomBigDecimal(t.getNumericPrecision(), t.getNumericScale())
                            .toPlainString());
            default -> throw new IllegalStateException("Unexpected value: " + typeEnum);
        }
        updateSQL.append(",");
    }

    public static void finishInsert(StringBuilder insertSQL) {
        insertSQL.replace(insertSQL.length() - 1, insertSQL.length(), "");
        insertSQL.append(");");
    }

    public static void finishDelete(StringBuilder deleteSQL) {
        deleteSQL.replace(deleteSQL.length() - 5, deleteSQL.length(), "");
        deleteSQL.append(";");
    }

    public static void finishUpdate(List<TableDefinition> tableColumns, StringBuilder updateSQL) throws SQLException {
        updateSQL.replace(updateSQL.length() - 1, updateSQL.length(), "");
        updateSQL.append(" WHERE ");
        for (TableDefinition t : tableColumns) {
            if (t.isPrimaryKey()) {
                ResultSetTypeEnum typeEnum = DataTypeEnum.byType(t.getDataType()).getResultSetType();
                switch (typeEnum) {
                    case STRING -> updateSQL
                            .append(t.getColumnName())
                            .append("=")
                            .append("'")
                            .append(RandomUtil.randomGenerator(t.getCharacterMaximumLength()))
                            .append("'");
                    case DECIMAL -> updateSQL
                            .append(t.getColumnName())
                            .append("=")
                            .append(RandomUtil.randomBigDecimal(t.getNumericPrecision(), t.getNumericScale())
                                    .toPlainString());
                    default -> throw new IllegalStateException("Unexpected value: " + typeEnum);
                }
                updateSQL.append(" AND ");
            }
        }
        updateSQL.replace(updateSQL.length() - 5, updateSQL.length(), "");
        updateSQL.append(";");
    }

}
