package com.nuamx.service;

import com.nuamx.entity.job.JobDefinitionRequest;
import com.nuamx.entity.job.JobMetadata;
import com.nuamx.util.Constants;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuartzService {

    private final Scheduler scheduler;

    @PostConstruct
    public void init() throws SchedulerException {
        scheduler.clear();
    }

    public <T extends Job> void addCronJob(Class<? extends Job> job, JobDefinitionRequest request)
            throws SchedulerException {
        Map<String, Object> params = getObjectMap(request);
        final JobDetail jobDetail = buildJobDetail(job, request.getName(), request.getGroup(), request.getDescription(), params);
        final Trigger trigger;
        try {
            trigger = buildCronTrigger(request.getCron(), request.getPriority());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        registerJobInScheduler(jobDetail, trigger);
    }

    private static Map<String, Object> getObjectMap(JobDefinitionRequest request) {
        Map<String, Object> params = new HashMap<>();
        JobMetadata jobMetadata = request.getMetadata();
        if (jobMetadata == null) {
            return params;
        }
        params.put(Constants.KEY_DATABASE_KEY_TARGET, jobMetadata.getDatabaseKeyTarget());
        params.put(Constants.KEY_DATABASE_NAME_TARGET, jobMetadata.getDatabaseNameTarget());
        params.put(Constants.KEY_SCHEMA_TARGET, jobMetadata.getSchemaTarget());
        params.put(Constants.KEY_TABLE_TARGET, jobMetadata.getTableTarget());
        params.put(Constants.KEY_TABLE_SOURCE, jobMetadata.getTableSource());
        return params;
    }

    public <T extends Job> JobDetail buildJobDetail(
            Class<? extends Job> job, final String jobName, final String group,
            String jobDescription, Map<String, Object> params) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.putAll(params);
        return JobBuilder.newJob(job)
                .withIdentity(jobName, group)
                .withDescription(jobDescription)
                .usingJobData(jobDataMap)
                .build();
    }

    private Trigger buildCronTrigger(String cronExp, Integer priority) throws ParseException {
        CronTriggerFactoryBean cronTriggerFactory = new CronTriggerFactoryBean();
        cronTriggerFactory.setCronExpression(cronExp);
        cronTriggerFactory.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        cronTriggerFactory.afterPropertiesSet();
        cronTriggerFactory.setPriority(priority);
        return cronTriggerFactory.getObject();
    }

    private void registerJobInScheduler(final JobDetail jobDetail, final Trigger trigger) throws SchedulerException {
        if (scheduler.checkExists(jobDetail.getKey())) {
            scheduler.deleteJob(jobDetail.getKey());
            scheduler.scheduleJob(jobDetail, trigger);
        } else {
            scheduler.scheduleJob(jobDetail, trigger);
        }
    }

}
