package com.nuamx.entity.response;

import com.nuamx.entity.database.TableDefinition;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
public class MetadataResponse {

    private List<TableDefinition> tableColumns;

}
