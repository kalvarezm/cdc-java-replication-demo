package com.nuamx.entity.job;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class JobMetadata {

    private String tableSource;
    private String databaseKeyTarget;
    private String databaseNameTarget;
    private String schemaTarget;
    private String tableTarget;

}
