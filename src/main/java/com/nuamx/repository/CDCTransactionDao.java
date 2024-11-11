package com.nuamx.repository;

import com.nuamx.entity.database.TableDefinition;
import com.nuamx.entity.request.*;
import com.nuamx.entity.response.BeginTransactionResponse;
import com.nuamx.entity.response.MetadataResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
@Getter
@RequiredArgsConstructor
public class CDCTransactionDao {

    public static final String SQL_BEGIN_TRANSACTION = "{call dbo.SpCDC_BeginTransaction(?, ?, ?, ?)}";
    public static final String SQL_GET_METADATA = "{call dbo.SpCDC_GetMetadata(?, ?, ?)}";
    public static final String SQL_GET_CHANGES = "{call dbo.SpCDC_GetChanges(?, ?, ?, ?, ?, ?, ?, ?)}";
    public static final String SQL_FINISH_TRANSACTION = "{call dbo.SpCDC_FinishTransaction(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
    public static final String SQL_ERROR_TRANSACTION = "{call dbo.SpCDC_ErrorTransaction(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

    private final DatabaseDao databaseDao;

    public BeginTransactionResponse beginTransaction(BeginTransactionRequest request) throws SQLException {
        ResultSet rsBeginTransaction = executeBeginTransaction(request);
        BeginTransactionResponse response = BeginTransactionResponse.builder().build();
        if (rsBeginTransaction.next()) {
            response = BeginTransactionResponse.builder()
                    .minLsn(rsBeginTransaction.getString("MIN_LSN"))
                    .maxLsn(rsBeginTransaction.getString("MAX_LSN"))
                    .lastLsn(rsBeginTransaction.getString("LAST_LSN"))
                    .lastSeqVal(rsBeginTransaction.getString("LAST_SEQVAL"))
                    .build();
        }
        rsBeginTransaction.close();
        return response;
    }

    private ResultSet executeBeginTransaction(BeginTransactionRequest request) throws SQLException {
        Connection connection = request.getSourceConnection();
        CallableStatement callBeginTransaction = connection.prepareCall(SQL_BEGIN_TRANSACTION);
        callBeginTransaction.setString(1, request.getDatabaseNameTarget());
        callBeginTransaction.setString(2, request.getDatabaseSchemaSource());
        callBeginTransaction.setString(3, request.getTableSource());
        callBeginTransaction.setInt(4, request.getChangeTypeEnum().getType());
        callBeginTransaction.execute();
        return callBeginTransaction.getResultSet();
    }

    @Cacheable(value = "metadata", key = "#tableSource")
    public MetadataResponse getMetadata(String tableSource, MetadataRequest request) throws SQLException {
        Connection connection = request.getConnection();
        String databaseNameSource = request.getDatabaseNameSource();
        String schemaSource = request.getDatabaseSchemaSource();
        ResultSet rsGetMetadata = executeMetadata(connection, databaseNameSource, schemaSource, tableSource);
        List<TableDefinition> tableColumns = new ArrayList<>();
        while (rsGetMetadata.next()) {
            TableDefinition tableDefinition = TableDefinition.builder()
                    .ordinalPosition(rsGetMetadata.getInt("ORDINAL_POSITION"))
                    .tableName(rsGetMetadata.getString("TABLE_NAME"))
                    .columnName(rsGetMetadata.getString("COLUMN_NAME"))
                    .dataType(rsGetMetadata.getString("DATA_TYPE"))
                    .constraintType(rsGetMetadata.getString("CONSTRAINT_TYPE"))
                    .isPrimaryKey(rsGetMetadata.getBoolean("IS_PRIMARY_KEY"))
                    .isNullable(rsGetMetadata.getBoolean("IS_NULLABLE"))
                    .columnDefinition(rsGetMetadata.getString("COLUMN_DEFINITION"))
                    .characterMaximumLength(rsGetMetadata.getInt("CHARACTER_MAXIMUM_LENGTH"))
                    .numericPrecision(rsGetMetadata.getInt("NUMERIC_PRECISION"))
                    .numericScale(rsGetMetadata.getInt("NUMERIC_SCALE"))
                    .build();
            tableColumns.add(tableDefinition);
        }
        rsGetMetadata.close();
        return MetadataResponse.builder().tableColumns(tableColumns).build();
    }

    private static ResultSet executeMetadata(Connection connection, String databaseNameSource, String schemaSource, String tableSource) throws SQLException {
        CallableStatement callGetMetadata = null;
        callGetMetadata = connection.prepareCall(SQL_GET_METADATA);
        callGetMetadata.setString(1, databaseNameSource);
        callGetMetadata.setString(2, schemaSource);
        callGetMetadata.setString(3, tableSource);
        callGetMetadata.execute();
        return callGetMetadata.getResultSet();
    }

    public ResultSet getChanges(GetChangesRequest request) throws SQLException {
        Connection connection = request.getConnection();
        CallableStatement callGetChanges = connection.prepareCall(SQL_GET_CHANGES);
        callGetChanges.setString(1, request.getDatabaseNameTarget());
        callGetChanges.setString(2, request.getDatabaseSchemaSource());
        callGetChanges.setString(3, request.getTableSource());
        callGetChanges.setInt(4, request.getBatchSelect());
        callGetChanges.setString(5, request.getMinLsn());
        callGetChanges.setString(6, request.getMaxLsn());
        callGetChanges.setString(7, request.getLastLsn());
        callGetChanges.setString(8, request.getLastSeqVal());
        callGetChanges.execute();
        return callGetChanges.getResultSet();
    }

    public void registerControl(RegisterControlRequest request) throws SQLException {
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - request.getStart();
        log.info("[{}] - Time elapsed: {}", request.getJobName(), timeElapsed);
        CallableStatement callFinishTransaction = request.getSourceConnection().prepareCall(SQL_FINISH_TRANSACTION);
        callFinishTransaction.setString(1, request.getDatabaseNameTarget());
        callFinishTransaction.setString(2, request.getDatabaseSchemaSource());
        callFinishTransaction.setString(3, request.getTableSource());
        callFinishTransaction.setString(4, request.getTableTarget());
        callFinishTransaction.setString(5, request.getMinLsn());
        callFinishTransaction.setString(6, request.getMaxLsn());
        callFinishTransaction.setString(7, request.getLastLsn());
        callFinishTransaction.setString(8, request.getLastSeqVal());
        callFinishTransaction.setInt(9, request.getTotalCountRs());
        callFinishTransaction.setLong(10, timeElapsed);
        callFinishTransaction.setInt(11, request.getStatus().getCode());
        callFinishTransaction.execute();
    }

    public void registerError(RegisterErrorRequest request) throws SQLException {
        CallableStatement callFinishTransaction = request.getSourceConnection().prepareCall(SQL_ERROR_TRANSACTION);
        callFinishTransaction.setString(1, request.getDatabaseNameTarget());
        callFinishTransaction.setString(2, request.getDatabaseSchemaSource());
        callFinishTransaction.setString(3, request.getTableSource());
        callFinishTransaction.setString(4, request.getTableTarget());
        callFinishTransaction.setString(5, request.getMinLsn());
        callFinishTransaction.setString(6, request.getMaxLsn());
        callFinishTransaction.setString(7, request.getLastLsn());
        callFinishTransaction.setString(8, request.getLastSeqVal());
        callFinishTransaction.setInt(9, request.getTotalCountRs());
        callFinishTransaction.setInt(10, request.getFailedTransactions());
        callFinishTransaction.setString(11, request.getMessage());
        callFinishTransaction.setString(12, request.getCodeSQLState());
        callFinishTransaction.setInt(13, request.getErrorCode());
        callFinishTransaction.execute();
    }

}
