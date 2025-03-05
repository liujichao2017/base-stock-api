package com.hm.stock.modules.common;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;

@Data
public class PageParam {

    @Parameter(description = "页数")
    private int pageNo = 1;

    @Parameter(description = "条数")
    private int pageSize = 20;

}
