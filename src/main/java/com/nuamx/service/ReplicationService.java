package com.nuamx.service;

import org.quartz.JobDataMap;
import org.quartz.SchedulerException;

public interface ReplicationService {

    public void execute(String jobName, JobDataMap jobDataMap) throws SchedulerException;

}
