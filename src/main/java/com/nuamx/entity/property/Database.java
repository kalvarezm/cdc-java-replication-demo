package com.nuamx.entity.property;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Database {

    @NotNull
    private String key;
    @NotNull
    private String description;
    @NotNull
    private String databaseName;
    private String schema;
    @NotNull
    private String url;
    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private String driverClassName;
    @NotNull
    private Boolean active;
    @NotNull
    private HikariPool hikari;
    private Batch batch;

}
