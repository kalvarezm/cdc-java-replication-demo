package com.nuamx.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum DataTypeEnum {

    CHAR("char", ResultSetTypeEnum.STRING),
    DECIMAL("decimal", ResultSetTypeEnum.DECIMAL);

    private final String type;
    private final ResultSetTypeEnum resultSetType;

    public static DataTypeEnum byType(String type) {
        return Arrays.stream(values()).filter(r -> r.getType().equals(type)).findAny().orElse(CHAR);
    }

}
