package com.nuamx.entity.request;

import com.nuamx.entity.enums.TransactionStatusEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class RegisterControlRequest extends BaseRegisterControlRequest {

    private TransactionStatusEnum status;

}
