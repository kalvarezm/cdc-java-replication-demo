package com.nuamx.util;

public class Constants {

    public static final String KEY_TABLE_SOURCE = "tableSource";
    public static final String KEY_DATABASE_KEY_TARGET = "databaseKeyTarget";
    public static final String KEY_DATABASE_NAME_TARGET = "databaseNameTarget";
    public static final String KEY_SCHEMA_TARGET = "schemaTarget";
    public static final String KEY_TABLE_TARGET = "tableTarget";
    public static final String KEY_SERVICE_TYPE = "serviceType";

    public static final String BEAN_SOURCE_DATABASE = "sourceDatabase";
    public static final String BEAN_TARGET_DATABASE = "targetDatabases";

    public static final String JOB_DESCRIPTION = "Replication job for %s";

    public static final String CDC_COLUMN_START_LSN = "__$start_lsn";
    public static final String CDC_COLUMN_SEQVAL = "__$seqval";
    public static final String CDC_COLUMN_OPERATION = "__$operation";
}
