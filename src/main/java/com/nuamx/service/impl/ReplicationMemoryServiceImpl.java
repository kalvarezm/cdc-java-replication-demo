package com.nuamx.service.impl;

import com.nuamx.entity.database.SourceDatabase;
import com.nuamx.service.ReplicationService;
import com.nuamx.util.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ReplicationMemoryServiceImpl implements ReplicationService {

    private final SourceDatabase sourceDatabase;

    @Override
    public void execute(String jobName, JobDataMap jobDataMap) {
        String databaseKeySource = sourceDatabase.getDatabaseKey();
        String databaseNameSource = sourceDatabase.getDatabaseName();
        String schemaSource = sourceDatabase.getSchema();
        String tableSource = (String) jobDataMap.get(Constants.KEY_TABLE_SOURCE);

        String databaseKeyTarget = (String) jobDataMap.get(Constants.KEY_DATABASE_KEY_TARGET);
        String databaseNameTarget = (String) jobDataMap.get(Constants.KEY_DATABASE_NAME_TARGET);
        String schemaTarget = (String) jobDataMap.get(Constants.KEY_SCHEMA_TARGET);
        String tableTarget = (String) jobDataMap.get(Constants.KEY_TABLE_TARGET);

        log.info("Job Memory: {}, Database: {}, Schema: {}", jobName, databaseKeyTarget, schemaTarget);
    }

}
