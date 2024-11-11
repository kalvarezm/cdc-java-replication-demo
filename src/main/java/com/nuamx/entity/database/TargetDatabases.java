package com.nuamx.entity.database;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.sql.DataSource;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class TargetDatabases {

    private Map<String, DataSource> databases;

}
