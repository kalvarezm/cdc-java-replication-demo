package com.nuamx.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum OperationCDCTypeEnum {

    DELETE("1"),
    INSERT("2"),
    UPDATE("4"),
    SKIP("-1");

    private final String type;

    public static OperationCDCTypeEnum byType(String type) {
        return Arrays.stream(values()).filter(r -> r.getType().equals(type)).findAny().orElse(INSERT);
    }

}
