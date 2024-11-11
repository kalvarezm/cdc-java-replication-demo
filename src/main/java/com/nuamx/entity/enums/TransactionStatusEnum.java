package com.nuamx.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TransactionStatusEnum {

    OK(1);

    private final int code;

    public static TransactionStatusEnum byType(int code) {
        return Arrays.stream(values()).filter(r -> r.getCode() == code).findAny().orElse(OK);
    }

}
