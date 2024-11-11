package com.nuamx.entity.property;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Batch {

    private Integer select;
    private Integer chunk;
    private Integer chunksByJob;

}
