package com.nuamx.job;

import com.nuamx.entity.database.TargetDatabases;
import com.nuamx.entity.enums.ReplicationServiceEnum;
import com.nuamx.service.DemoService;
import com.nuamx.service.ReplicationService;
import com.nuamx.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;

@Slf4j
@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class DemoJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            ApplicationContext applicationContext = getApplicationContext(jobExecutionContext);
            Map<String, DemoService> sourceDatabasesMap = applicationContext.getBeansOfType(DemoService.class);
            JobDataMap params = jobExecutionContext.getMergedJobDataMap();
            String jobName = jobExecutionContext.getJobDetail().getKey().getName();
            DemoService service = sourceDatabasesMap.get("demoServiceImpl");
            service.execute(jobName, params);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    private static DataSource getTargetDataSource(ApplicationContext applicationContext, String databaseKeyTarget) {
        Map<String, TargetDatabases> targetDatabasesMap = applicationContext.getBeansOfType(TargetDatabases.class);
        Map<String, DataSource> targetMap = targetDatabasesMap.get(Constants.BEAN_TARGET_DATABASE).getDatabases();
        return targetMap.get(databaseKeyTarget);
    }

    private ApplicationContext getApplicationContext(JobExecutionContext jobExecutionContext) throws SchedulerException {
        return (ApplicationContext) jobExecutionContext.getScheduler().getContext().get("applicationContext");
    }

}