package com.nuamx.entity.property;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Table {

    @NotNull
    private String source;
    @NotNull
    private String target;
    private Integer priority;
    private String cron;
    private List<String> databases;
    @NotNull
    private Boolean active;

}
