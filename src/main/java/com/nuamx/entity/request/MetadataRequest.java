package com.nuamx.entity.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.sql.Connection;

@Getter
@Setter
@SuperBuilder
public class MetadataRequest extends BaseRequest {

    private Connection connection;

}
