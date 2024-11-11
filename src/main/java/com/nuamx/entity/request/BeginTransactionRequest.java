package com.nuamx.entity.request;

import com.nuamx.entity.enums.ChangeTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.sql.Connection;

@Getter
@Setter
@SuperBuilder
public class BeginTransactionRequest extends BaseRequest {

    private ChangeTypeEnum changeTypeEnum;
    private Connection sourceConnection;

}
