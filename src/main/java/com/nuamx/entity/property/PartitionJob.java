package com.nuamx.entity.property;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PartitionJob {

    @NotNull
    private String name;
    @NotNull
    private Integer defaultPriority;
    @NotNull
    private String defaultCron;
    @NotNull
    private List<String> defaultDatabases;
    @NotNull
    private Boolean active;
    @NotNull
    private List<Table> tables;

}
