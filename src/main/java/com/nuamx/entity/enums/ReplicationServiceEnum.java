package com.nuamx.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ReplicationServiceEnum {

    SQL_SERVICE("sql", "replicationSQLServiceImpl"),
    H2_SERVICE("h2", "replicationMemoryServiceImpl");

    private final String type;
    private final String service;

    public static ReplicationServiceEnum byType(String profile) {
        return Arrays.stream(values()).filter(r -> r.getType().equals(profile)).findAny().orElse(SQL_SERVICE);
    }

}
