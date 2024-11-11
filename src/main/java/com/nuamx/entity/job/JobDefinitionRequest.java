package com.nuamx.entity.job;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class JobDefinitionRequest {

    private String name;
    private String group;
    private String description;
    private Integer priority;
    private String cron;
    private JobMetadata metadata;

}
