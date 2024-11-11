package com.nuamx;

import com.nuamx.service.JobService;
import org.quartz.SchedulerException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
@EnableCaching
public class SpringQuartzApp {

    public static void main(String[] args) throws SchedulerException {
        ApplicationContext applicationContext = SpringApplication.run(SpringQuartzApp.class, args);
        applicationContext.getBean(JobService.class).scheduleDemo();
    }

}