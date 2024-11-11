package com.nuamx.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChangeTypeEnum {

    LAST_CHANGES(1),
    NEW_CHANGES(0);

    private final int type;

}
