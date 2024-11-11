package com.nuamx.entity.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class BaseRequest {

    private String jobName;
    private String databaseKeyTarget;
    private String databaseNameTarget;
    private String databaseSchemaTarget;
    private String tableTarget;
    private String databaseKeySource;
    private String databaseNameSource;
    private String databaseSchemaSource;
    private String tableSource;

}
