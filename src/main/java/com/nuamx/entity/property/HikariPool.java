package com.nuamx.entity.property;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HikariPool {

    @NotNull
    private Long connectionTimeout;
    @NotNull
    private Integer minimumIdle;
    @NotNull
    private Long maxLifetime;
    @NotNull
    private Integer maximumPoolSize;
    @NotNull
    private Boolean autoCommit;
    @NotNull
    private String poolName;
    @NotNull
    private Long idleTimeout;
    @NotNull
    private Long keepaliveTime;
    @NotNull
    private String connectionTestQuery;

}
