package com.nuamx.config.quartz;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class QuartzConfig {

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    private final ApplicationContext applicationContext;
    private final DataSource jobStoreDatasource;

    @Bean
    public ClassPathResource quartzResource() {
        String fileName = String.format("quartz-%s.properties", activeProfile);
        return new ClassPathResource(fileName);
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setConfigLocation(quartzResource());
        schedulerFactory.setOverwriteExistingJobs(true);
        schedulerFactory.setJobFactory(springBeanJobFactory());
        schedulerFactory.setDataSource(jobStoreDatasource);
        schedulerFactory.setTransactionManager(new DataSourceTransactionManager(jobStoreDatasource));
        schedulerFactory.setApplicationContextSchedulerContextKey("applicationContext");
        return schedulerFactory;
    }

    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

}
