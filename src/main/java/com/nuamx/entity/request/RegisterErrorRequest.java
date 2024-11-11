package com.nuamx.entity.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class RegisterErrorRequest extends BaseRegisterControlRequest {

    private int failedTransactions;
    private String message;
    private String codeSQLState;
    private int errorCode;

}
