package com.nuamx.entity.property;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CacheJob {

    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Integer priority;
    @NotNull
    private String cron;
    @NotNull
    private Boolean active;

}
