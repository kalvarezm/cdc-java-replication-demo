package com.nuamx.service;

import com.nuamx.config.property.DatabasesProperty;
import com.nuamx.config.property.JobsProperty;
import com.nuamx.entity.job.JobDefinitionRequest;
import com.nuamx.entity.job.JobMetadata;
import com.nuamx.entity.property.CacheJob;
import com.nuamx.entity.property.Database;
import com.nuamx.entity.property.PartitionJob;
import com.nuamx.entity.property.Table;
import com.nuamx.job.CleanCacheJob;
import com.nuamx.job.DemoJob;
import com.nuamx.job.ReplicationJob;
import com.nuamx.util.CommonUtil;
import com.nuamx.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class JobService {

    private final JobsProperty jobsProperty;
    private final DatabasesProperty databasesProperty;
    private final QuartzService quartzService;

    private List<JobDefinitionRequest> jobs;

    public void scheduleDemo() throws SchedulerException {
        jobs = new ArrayList<>();
        jobsProperty.getPartitions().stream()
                .filter(PartitionJob::getActive)
                .forEach(p -> p.getTables().stream()
                        .filter(Table::getActive)
                        .forEach(t -> buildJobDefinition(p, t))
                );
        log.info("TOTAL PARTITION JOBS TO BE CREATED: {}", jobs.size());
        for (JobDefinitionRequest job : jobs) {
            quartzService.addCronJob(DemoJob.class, job);
        }
    }

    public void buildJobDefinition(PartitionJob p, Table t) {
        List<Database> tableDatabases = getActiveDatabases(p, t);
        tableDatabases.forEach(d -> {

            String name = String.format("%s-%s-%s", p.getName(), t.getSource(), d.getKey());
            String group = String.format("%s-%s", p.getName(), d.getKey());
            String cron = t.getCron() != null ? t.getCron() : p.getDefaultCron();
            String description = String.format(Constants.JOB_DESCRIPTION, name);
            Integer priority = t.getPriority() != null ? t.getPriority() : p.getDefaultPriority();

            JobMetadata metadata = JobMetadata.builder()
                    .databaseKeyTarget(d.getKey())
                    .databaseNameTarget(d.getDatabaseName())
                    .schemaTarget(d.getSchema())
                    .tableTarget(t.getTarget())
                    .tableSource(t.getSource())
                    .build();
            jobs.add(
                    JobDefinitionRequest.builder()
                            .name(name)
                            .cron(cron)
                            .group(group)
                            .description(description)
                            .priority(priority)
                            .metadata(metadata)
                            .build()
            );

        });
    }

    private List<Database> getActiveDatabases(PartitionJob p, Table t) {
        List<Database> targetDatabase = databasesProperty.getTarget();
        List<String> tableDatabases = t.getDatabases();
        if (CommonUtil.isNullOrEmpty(tableDatabases)) {
            tableDatabases = p.getDefaultDatabases();
        }
        List<String> finalTableDatabases = tableDatabases;
        return targetDatabase
                .stream()
                .filter(Database::getActive)
                .filter(target -> finalTableDatabases.contains(target.getKey()))
                .toList();
    }

}
