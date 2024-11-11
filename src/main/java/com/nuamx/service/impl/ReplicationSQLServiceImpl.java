package com.nuamx.service.impl;

import com.nuamx.entity.database.ConnectionWrapper;
import com.nuamx.entity.database.QuerySelectionWrapper;
import com.nuamx.entity.database.TableDefinition;
import com.nuamx.entity.database.TargetDatabases;
import com.nuamx.entity.enums.ChangeTypeEnum;
import com.nuamx.entity.enums.OperationCDCTypeEnum;
import com.nuamx.entity.enums.TransactionStatusEnum;
import com.nuamx.entity.property.Batch;
import com.nuamx.entity.property.Database;
import com.nuamx.entity.request.*;
import com.nuamx.entity.response.BeginTransactionResponse;
import com.nuamx.entity.response.MetadataResponse;
import com.nuamx.repository.CDCTransactionDao;
import com.nuamx.repository.DatabaseDao;
import com.nuamx.service.ReplicationService;
import com.nuamx.util.CommonUtil;
import com.nuamx.util.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ReplicationSQLServiceImpl implements ReplicationService {

    private final CDCTransactionDao cdcTransactionDao;
    private final DatabaseDao databaseDao;
    private final TargetDatabases targetDatabases;

    @Override
    public void execute(String jobName, JobDataMap jobDataMap) {
        ConnectionWrapper sourceConnectionWrapper = null;
        try {
            sourceConnectionWrapper = databaseDao.configureSourceConnection();
            process(jobName, jobDataMap, sourceConnectionWrapper, ChangeTypeEnum.LAST_CHANGES);
            process(jobName, jobDataMap, sourceConnectionWrapper, ChangeTypeEnum.NEW_CHANGES);
        } catch (Exception e) {
            log.error("[{}] {}", jobName, e.getMessage());
        } finally {
            try {
                if (sourceConnectionWrapper != null) {
                    sourceConnectionWrapper.closeConnection();
                }
            } catch (SQLException e) {
                log.error("[{}] {}", jobName, e.getMessage());
            }
        }
    }

    private void process(String jobName, JobDataMap jobDataMap, ConnectionWrapper sourceConnectionWrapper,
                         ChangeTypeEnum changeTypeEnum) throws SQLException {
        BeginTransactionResponse beginTransactionResponse = beginTransaction(jobName, jobDataMap, sourceConnectionWrapper, changeTypeEnum);
        String seqVal = (ChangeTypeEnum.LAST_CHANGES == changeTypeEnum)
                ? beginTransactionResponse.getLastSeqVal() : beginTransactionResponse.getMinLsn();
        boolean isThereChanges = seqVal != null;
        if (isThereChanges) {
            MetadataResponse metadataResponse = getMetadata(jobName, jobDataMap, sourceConnectionWrapper);
            replicateChanges(jobName, jobDataMap, beginTransactionResponse, sourceConnectionWrapper, metadataResponse);
        }
    }

    private BeginTransactionResponse beginTransaction(String jobName, JobDataMap jobDataMap, ConnectionWrapper sourceConnectionWrapper,
                                                      ChangeTypeEnum changeTypeEnum) throws SQLException {
        String databaseNameTarget = (String) jobDataMap.get(Constants.KEY_DATABASE_NAME_TARGET);
        String databaseSchemaSource = sourceConnectionWrapper.getSchemaName();
        String tableSource = (String) jobDataMap.get(Constants.KEY_TABLE_SOURCE);
        BeginTransactionRequest beginTransactionRequest = BeginTransactionRequest.builder()
                .jobName(jobName)
                .databaseNameTarget(databaseNameTarget)
                .databaseSchemaSource(databaseSchemaSource)
                .tableSource(tableSource)
                .sourceConnection(sourceConnectionWrapper.getConnection())
                .changeTypeEnum(changeTypeEnum)
                .build();
        return cdcTransactionDao.beginTransaction(beginTransactionRequest);
    }

    private MetadataResponse getMetadata(String jobName, JobDataMap jobDataMap, ConnectionWrapper sourceConnectionWrapper) throws SQLException {
        String databaseNameSource = sourceConnectionWrapper.getDatabaseName();
        String databaseSchemaSource = sourceConnectionWrapper.getSchemaName();
        String tableSource = (String) jobDataMap.get(Constants.KEY_TABLE_SOURCE);
        Connection connection = sourceConnectionWrapper.getConnection();
        return cdcTransactionDao.getMetadata(
                tableSource,
                MetadataRequest.builder()
                        .jobName(jobName)
                        .databaseNameSource(databaseNameSource)
                        .databaseSchemaSource(databaseSchemaSource)
                        .connection(connection)
                        .build()
        );
    }

    private void replicateChanges(String jobName, JobDataMap jobDataMap, BeginTransactionResponse beginTransactionResponse,
                                  ConnectionWrapper sourceConnectionWrapper, MetadataResponse metadataResponse) throws SQLException {
        // Replications params
        String databaseKeyTarget = (String) jobDataMap.get(Constants.KEY_DATABASE_KEY_TARGET);
        String databaseNameTarget = (String) jobDataMap.get(Constants.KEY_DATABASE_NAME_TARGET);
        String tableTarget = (String) jobDataMap.get(Constants.KEY_TABLE_TARGET);
        String tableSource = (String) jobDataMap.get(Constants.KEY_TABLE_SOURCE);
        String schemaSource = sourceConnectionWrapper.getSchemaName();
        String minLsn = beginTransactionResponse.getMinLsn();
        String maxLsn = beginTransactionResponse.getMaxLsn();
        String lastLsn = beginTransactionResponse.getLastLsn() != null ? beginTransactionResponse.getLastLsn() : minLsn;
        String lastSeqVal = beginTransactionResponse.getLastSeqVal() != null ? beginTransactionResponse.getLastSeqVal() : null;

        // Metadata and configuration params
        List<TableDefinition> tableColumns = metadataResponse.getTableColumns();
        Database databaseTargetProperty = databaseDao.getDatabaseTarget(databaseKeyTarget);
        Batch batch = databaseTargetProperty.getBatch();
        int batchChunksByJob = batch.getChunksByJob();
        int batchSelect = batch.getSelect();
        int batchExecute = batch.getChunk();

        // Connection params
        DataSource targetDataSource = targetDatabases.getDatabases().get(databaseKeyTarget);
        Connection targetConnection = targetDataSource.getConnection();
        Connection sourceConnection = sourceConnectionWrapper.getConnection();
        Statement statementBatch = targetConnection.createStatement();

        // Autocommit
        targetConnection.setAutoCommit(false);
        sourceConnection.setAutoCommit(false);

        // Query
        StringBuilder insertSQLBase = QuerySelectionWrapper.initInsert(tableTarget, tableColumns);
        StringBuilder deleteSQLBase = QuerySelectionWrapper.initDelete(tableTarget);
        StringBuilder updateSQLBase = QuerySelectionWrapper.initUpdate(tableTarget);

        // Local variables
        long start = System.currentTimeMillis();
        int totalCountRs = 0;
        int totalChunks = 0;
        boolean nextBatch = true;

        // Iterating by batch
        while (nextBatch) {
            int countRs = 0;
            int countBatch = 0;
            List<QuerySelectionWrapper> querySelectionWrappers = new ArrayList<>();

            // Get changes from CDC
            ResultSet rsGetChanges = cdcTransactionDao.getChanges(GetChangesRequest.builder()
                    .connection(sourceConnection)
                    .databaseNameTarget(databaseNameTarget)
                    .databaseSchemaSource(schemaSource)
                    .tableSource(tableSource)
                    .batchSelect(batchSelect)
                    .minLsn(minLsn)
                    .maxLsn(maxLsn)
                    .lastLsn(lastLsn)
                    .lastSeqVal(lastSeqVal)
                    .build());

            // Iterating changes
            while (rsGetChanges.next()) {
                StringBuilder insertSQL = new StringBuilder(insertSQLBase);
                StringBuilder deleteSQL = new StringBuilder(deleteSQLBase);
                StringBuilder updateSQL = new StringBuilder(updateSQLBase);

                OperationCDCTypeEnum cdcOperationTypeEnum = OperationCDCTypeEnum.byType(
                        rsGetChanges.getString(Constants.CDC_COLUMN_OPERATION));
                lastLsn = rsGetChanges.getString(Constants.CDC_COLUMN_START_LSN);
                lastSeqVal = rsGetChanges.getString(Constants.CDC_COLUMN_SEQVAL);

                for (TableDefinition t : tableColumns) {
                    QuerySelectionWrapper.modifyInsert(t, insertSQL, rsGetChanges);
                    QuerySelectionWrapper.modifyDelete(t, deleteSQL, rsGetChanges);
                    QuerySelectionWrapper.modifyUpdate(t, updateSQL, rsGetChanges);
                }

                QuerySelectionWrapper.finishInsert(insertSQL);
                QuerySelectionWrapper.finishDelete(deleteSQL);
                QuerySelectionWrapper.finishUpdate(tableColumns, updateSQL, rsGetChanges);

                querySelectionWrappers.add(QuerySelectionWrapper
                        .builder()
                        .insert(insertSQL)
                        .delete(deleteSQL)
                        .update(updateSQL)
                        .cdcOperationType(cdcOperationTypeEnum)
                        .finalOperationType(cdcOperationTypeEnum)
                        .build());

                countRs++;
                countBatch++;
                totalCountRs++;

                // When we have enough transactions, we will start replication
                if (countBatch == batchExecute) {
                    executeReplication(jobName, statementBatch, totalCountRs,
                            lastLsn, lastSeqVal, sourceConnection, start, databaseNameTarget, schemaSource, tableSource,
                            tableTarget, minLsn, maxLsn, countBatch, targetConnection, querySelectionWrappers);
                    // Reset variables
                    countBatch = 0;
                    querySelectionWrappers.clear();
                }

            }
            // Replicating remaining transactions
            if (countBatch > 0) {
                executeReplication(jobName, statementBatch, totalCountRs,
                        lastLsn, lastSeqVal, sourceConnection, start, databaseNameTarget, schemaSource, tableSource,
                        tableTarget, minLsn, maxLsn, countBatch, targetConnection, querySelectionWrappers);
                // Reset variables
                querySelectionWrappers.clear();
            }

            // If there is not new changes, finish iteration
            if (countRs == 0) {
                nextBatch = false;
            }

            // If limit of chunks by iteration is higher, finish iteration
            totalChunks++;
            if (totalChunks >= batchChunksByJob) {
                break;
            }

            // Closing result set
            rsGetChanges.close();

        }

        if (totalCountRs > 0) {
            log.info("[{}] - Total registers: {}", jobName, totalCountRs);
        }
        statementBatch.close();
        targetConnection.close();
    }

    private void executeReplication(String jobName, Statement statementBatch,
                                    int totalCountRs, String lastLsn, String lastSeqVal, Connection sourceConnection, long start,
                                    String databaseNameTarget, String schemaSource, String tableSource, String tableTarget,
                                    String minLsn, String maxLsn, int countBatch, Connection targetConnection,
                                    List<QuerySelectionWrapper> querySelectionWrappers) throws SQLException {
        // First attempt
        log.info("[{}] - Executing batch {}/{} - Last lsn {} - Last seqval {}", jobName, countBatch, totalCountRs, lastLsn, lastSeqVal);
        List<QuerySelectionWrapper> querySelectionWrapperErrorList = new ArrayList<>();
        try {
            start = System.currentTimeMillis();
            executeBatch(statementBatch, querySelectionWrappers);

            // Register control
            cdcTransactionDao.registerControl(
                    RegisterControlRequest.builder()
                            .jobName(jobName)
                            .sourceConnection(sourceConnection)
                            .start(start)
                            .databaseNameTarget(databaseNameTarget)
                            .databaseSchemaSource(schemaSource)
                            .tableSource(tableSource)
                            .tableTarget(tableTarget)
                            .minLsn(minLsn)
                            .maxLsn(maxLsn)
                            .lastLsn(lastLsn)
                            .lastSeqVal(lastSeqVal)
                            .totalCountRs(countBatch)
                            .status(TransactionStatusEnum.OK)
                            .build());

            // Commit changes
            targetConnection.commit();
            sourceConnection.commit();

        } catch (BatchUpdateException ex) {
            log.error("[{}] {}", jobName, ex.getMessage());
            // Rollback changes
            targetConnection.rollback();
            sourceConnection.rollback();
            int failedTransactions = 0;
            for (int i = 0; i < ex.getUpdateCounts().length; i++) {
                if (ex.getUpdateCounts()[i] == Statement.EXECUTE_FAILED) {
                    QuerySelectionWrapper errorQuery = querySelectionWrappers.get(i);
                    QuerySelectionWrapper finalQuery = QuerySelectionWrapper
                            .builder()
                            .insert(errorQuery.getInsert())
                            .delete(errorQuery.getDelete())
                            .update(errorQuery.getUpdate())
                            .cdcOperationType(errorQuery.getCdcOperationType())
                            .build();
                    switch (errorQuery.getCdcOperationType()) {
                        case INSERT -> finalQuery.setFinalOperationType(OperationCDCTypeEnum.UPDATE);
                        case UPDATE -> finalQuery.setFinalOperationType(OperationCDCTypeEnum.INSERT);
                        case DELETE -> finalQuery.setFinalOperationType(OperationCDCTypeEnum.SKIP);
                    }
                    querySelectionWrapperErrorList.add(finalQuery);
                    failedTransactions++;
                } else {
                    querySelectionWrapperErrorList.add(querySelectionWrappers.get(i));
                }
            }
            // Register error
            cdcTransactionDao.registerError(
                    RegisterErrorRequest.builder()
                            .jobName(jobName)
                            .sourceConnection(sourceConnection)
                            .start(start)
                            .databaseNameTarget(databaseNameTarget)
                            .databaseSchemaSource(schemaSource)
                            .tableSource(tableSource)
                            .tableTarget(tableTarget)
                            .minLsn(minLsn)
                            .maxLsn(maxLsn)
                            .lastLsn(lastLsn)
                            .lastSeqVal(lastSeqVal)
                            .totalCountRs(countBatch)
                            .failedTransactions(failedTransactions)
                            .message(CommonUtil.customSubstring(ex.getMessage(), 1000))
                            .codeSQLState(ex.getSQLState() != null ? ex.getSQLState() : "000000")
                            .errorCode(ex.getErrorCode())
                            .build());
            sourceConnection.commit();

            // Retry transaction
            retryExecution(jobName, querySelectionWrapperErrorList, statementBatch, totalCountRs,
                    lastLsn, lastSeqVal, sourceConnection, start, databaseNameTarget, schemaSource, tableSource,
                    tableTarget, minLsn, maxLsn, countBatch, targetConnection);

        } catch (Exception e) {
            // Reset variables
            querySelectionWrappers.clear();
            // Rollback changes
            targetConnection.rollback();
            sourceConnection.rollback();
            throw new RuntimeException(e);
        }
    }

    private void retryExecution(String jobName, List<QuerySelectionWrapper> querySelectionWrapperErrorList, Statement statementBatch,
                                int totalCountRs, String lastLsn, String lastSeqVal, Connection sourceConnection, long start,
                                String databaseNameTarget, String schemaSource, String tableSource, String tableTarget,
                                String minLsn, String maxLsn, int countBatch, Connection targetConnection) throws SQLException {
        // Second attempt
        for (QuerySelectionWrapper q : querySelectionWrapperErrorList) {
            switch (q.getFinalOperationType()) {
                case INSERT -> statementBatch.addBatch(q.getInsert().toString());
                case UPDATE -> statementBatch.addBatch(q.getUpdate().toString());
                case DELETE -> statementBatch.addBatch(q.getDelete().toString());
            }
        }
        if (!querySelectionWrapperErrorList.isEmpty()) {
            statementBatch.executeBatch();
            log.info("[{}] - Retry batch {}/{} - Last lsn {} - Last seqval {}", jobName, countBatch, totalCountRs, lastLsn, lastSeqVal);
            // Finish transaction and register control
            cdcTransactionDao.registerControl(
                    RegisterControlRequest.builder()
                            .jobName(jobName)
                            .sourceConnection(sourceConnection)
                            .start(start)
                            .databaseNameTarget(databaseNameTarget)
                            .databaseSchemaSource(schemaSource)
                            .tableSource(tableSource)
                            .tableTarget(tableTarget)
                            .minLsn(minLsn)
                            .maxLsn(maxLsn)
                            .lastLsn(lastLsn)
                            .lastSeqVal(lastSeqVal)
                            .totalCountRs(countBatch)
                            .status(TransactionStatusEnum.OK)
                            .build());

            targetConnection.commit();
            sourceConnection.commit();
        }
    }

    private void executeBatch(Statement statementBatch, List<QuerySelectionWrapper> querySelectionWrappers) throws SQLException {
        for (QuerySelectionWrapper q : querySelectionWrappers) {
            switch (q.getFinalOperationType()) {
                case INSERT -> statementBatch.addBatch(q.getInsert().toString());
                case UPDATE -> statementBatch.addBatch(q.getUpdate().toString());
                case DELETE -> statementBatch.addBatch(q.getDelete().toString());
            }
        }
        statementBatch.executeBatch();
    }

}
