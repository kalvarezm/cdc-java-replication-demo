package com.nuamx.entity.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.sql.Connection;

@Getter
@Setter
@SuperBuilder
public class GetChangesRequest extends BaseRequest {

    private Connection connection;
    private int batchSelect;
    private String minLsn;
    private String maxLsn;
    private String lastLsn;
    private String lastSeqVal;

}
