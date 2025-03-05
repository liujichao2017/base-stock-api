package com.hm.stock.domain.member.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MemberCancelWithdrawVo {

    @Schema(title = "提现记录id")
    @Parameter(description = "提现记录id")
    private Long id;

}
