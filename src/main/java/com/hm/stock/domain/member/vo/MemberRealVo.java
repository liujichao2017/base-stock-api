package com.hm.stock.domain.member.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 会员实名vo
 */
@Data
public class MemberRealVo {
    @Schema(title = "真实姓名")
    @Parameter(description = "真实姓名")
    @NotNull
    private String realName;

    @Schema(title = "证件号")
    @Parameter(description = "证件号")
    @NotNull
    private String idCard;

    @Schema(title = "备用, 证件照片")
    @Parameter(description = "备用, 证件照片")
    private String img1Key;

    @Schema(title = "备用, 证件照片")
    @Parameter(description = "备用, 证件照片")
    private String img2Key;

    @Schema(title = "备用, 证件照片")
    @Parameter(description = "备用, 证件照片")
    private String img3Key;
}
