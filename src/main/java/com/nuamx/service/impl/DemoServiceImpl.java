package com.nuamx.service.impl;

import com.nuamx.config.property.JobsProperty;
import com.nuamx.entity.database.*;
import com.nuamx.entity.enums.OperationCDCTypeEnum;
import com.nuamx.entity.property.Database;
import com.nuamx.entity.request.MetadataRequest;
import com.nuamx.entity.response.MetadataResponse;
import com.nuamx.repository.CDCTransactionDao;
import com.nuamx.repository.DatabaseDao;
import com.nuamx.service.DemoService;
import com.nuamx.util.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class DemoServiceImpl implements DemoService {

    private final CDCTransactionDao cdcTransactionDao;
    private final DatabaseDao databaseDao;
    private final TargetDatabases targetDatabases;
    private final JobsProperty jobsProperty;

    @Override
    public void execute(String jobName, JobDataMap jobDataMap) {
        ConnectionWrapper sourceConnectionWrapper = null;
        try {
            sourceConnectionWrapper = databaseDao.configureSourceConnection();
            MetadataResponse metadataResponse = getMetadata(jobName, jobDataMap, sourceConnectionWrapper);
            replicateChanges(jobName, jobDataMap, sourceConnectionWrapper, metadataResponse);
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
        // Delay to avoid overloading system
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void replicateChanges(String jobName, JobDataMap jobDataMap,
                                  ConnectionWrapper sourceConnectionWrapper, MetadataResponse metadataResponse) throws SQLException {

        // Replications params
        //String databaseKeyTarget = (String) jobDataMap.get(Constants.KEY_DATABASE_KEY_TARGET);
        // Connection params
        //DataSource targetDataSource = targetDatabases.getDatabases().get(databaseKeyTarget);
        Connection targetConnection = sourceConnectionWrapper.getConnection();
        Statement statementBatch = targetConnection.createStatement();

        List<QuerySelectionWrapper> querySelectionWrappers = new ArrayList<>();

        try {
            // Replications params
            String tableTarget = (String) jobDataMap.get(Constants.KEY_TABLE_TARGET);

            // Metadata and configuration params
            List<TableDefinition> tableColumns = metadataResponse.getTableColumns();
            //Database databaseTargetProperty = databaseDao.getDatabaseTarget(databaseKeyTarget);

            // Query
            StringBuilder insertSQLBase = QueryRandomSelectionWrapper.initInsert(tableTarget, tableColumns);
            StringBuilder deleteSQLBase = QueryRandomSelectionWrapper.initDelete(tableTarget);
            StringBuilder updateSQLBase = QueryRandomSelectionWrapper.initUpdate(tableTarget);

            // Autocommit
            targetConnection.setAutoCommit(false);

            int batch = 500;
            log.info("[{}] - Inserting {} registers", jobName, batch);

            for (int i = 0; i < batch; i++) {
                StringBuilder insertSQL = new StringBuilder(insertSQLBase);
                StringBuilder deleteSQL = new StringBuilder(deleteSQLBase);
                StringBuilder updateSQL = new StringBuilder(updateSQLBase);

                for (TableDefinition t : tableColumns) {
                    QueryRandomSelectionWrapper.modifyInsert(t, insertSQL);
                    QueryRandomSelectionWrapper.modifyDelete(t, deleteSQL);
                    QueryRandomSelectionWrapper.modifyUpdate(t, updateSQL);
                }

                QueryRandomSelectionWrapper.finishInsert(insertSQL);
                QueryRandomSelectionWrapper.finishDelete(deleteSQL);
                QueryRandomSelectionWrapper.finishUpdate(tableColumns, updateSQL);

                querySelectionWrappers.add(QuerySelectionWrapper
                        .builder()
                        .insert(insertSQL)
                        .delete(deleteSQL)
                        .update(updateSQL)
                        .cdcOperationType(OperationCDCTypeEnum.INSERT)
                        .finalOperationType(OperationCDCTypeEnum.INSERT)
                        .build());

            }
            executeBatch(statementBatch, querySelectionWrappers);
            targetConnection.commit();

            statementBatch.close();
            targetConnection.close();

        } catch (Exception e) {
            // Reset variables
            querySelectionWrappers.clear();
            // Rollback changes
            targetConnection.rollback();
            statementBatch.close();
            targetConnection.close();
            throw new RuntimeException(e);
        }
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
