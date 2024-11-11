package com.nuamx.entity.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BeginTransactionResponse {

    private String minLsn;
    private String maxLsn;
    private String lastLsn;
    private String lastSeqVal;

}
