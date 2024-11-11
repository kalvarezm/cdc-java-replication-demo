package com.nuamx.entity.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.sql.Connection;

@Getter
@Setter
@SuperBuilder
public class BaseRegisterControlRequest extends BaseRequest {

    private Connection sourceConnection;
    private long start;
    private String minLsn;
    private String maxLsn;
    private String lastLsn;
    private String lastSeqVal;
    private int totalCountRs;

}
