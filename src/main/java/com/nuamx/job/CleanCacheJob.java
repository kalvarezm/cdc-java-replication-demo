package com.nuamx.job;

import com.nuamx.service.CleanCacheService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class CleanCacheJob implements Job {

    @Override
    @CacheEvict(
            value = {"metadata"},
            allEntries = true)
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            String jobName = jobExecutionContext.getJobDetail().getKey().getName();
            ApplicationContext applicationContext = getApplicationContext(jobExecutionContext);
            Map<String, CleanCacheService> beansMap = applicationContext.getBeansOfType(CleanCacheService.class);
            CleanCacheService service = beansMap.get("cleanCacheService");
            service.execute(jobName);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    private ApplicationContext getApplicationContext(JobExecutionContext jobExecutionContext) throws SchedulerException {
        return (ApplicationContext) jobExecutionContext.getScheduler().getContext().get("applicationContext");
    }

}
