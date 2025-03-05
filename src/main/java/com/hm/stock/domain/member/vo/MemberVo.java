package com.hm.stock.domain.member.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 后台会员信息封装vo
 */
@Data
public class MemberVo {

    @Schema(title = "会员id")
    @Parameter(description = "会员id")
    private Long id;

    @Schema(title = "代理ID")
    @Parameter(description = "代理ID")
    private Long userId;

    @Schema(title = "手机号")
    @Parameter(description = "手机号")
    private String phone;

    @Schema(title = "真实姓名")
    @Parameter(description = "真实姓名")
    private String realName;

    @Schema(title = "证件号")
    @Parameter(description = "证件号")
    private String idCard;

    @Schema(title = "视频认证状态: 0 未认证, 1: 已认证")
    @Parameter(description = "视频认证状态: 0 未认证, 1: 已认证")
    private String videoActive;

    @Schema(title = "头像照片")
    @Parameter(description = "头像照片")
    private String avatarImg;

    @Schema(title = "实盘-0 模拟-1")
    @Parameter(description = "实盘-0 模拟-1")
    private String accountType;

    @Schema(title = "实名状态  0: 未实名. 1: 已实名 ,2: 审核不通过,3: 待审批")
    @Parameter(description = "实名状态  0: 未实名. 1: 已实名 ,2: 审核不通过,3: 待审批")
    private String isActive;

    @Schema(title = "允许登录 see YNEnum, 0: 否. 1: 是 ")
    @Parameter(description = "允许登录 see YNEnum, 0: 否. 1: 是 ")
    private String isLogin;

    @Schema(title = "股票交易 see YNEnum, 0: 否. 1: 是 ")
    @Parameter(description = "股票交易 see YNEnum, 0: 否. 1: 是 ")
    private String isStock;

    @Schema(title = "杠杆交易 see YNEnum, 0: 否. 1: 是 ")
    @Parameter(description = "杠杆交易 see YNEnum, 0: 否. 1: 是 ")
    private String isLever;

    @Schema(title = "贷款额度")
    @Parameter(description = "贷款额度")
    private BigDecimal loanAmt;

    @Schema(title = "注册时间")
    private LocalDateTime createTime;

    @Schema(title = "备用, 证件照片")
    @Parameter(description = "备用, 证件照片")
    private String img1Key;

    @Schema(title = "备用, 证件照片")
    @Parameter(description = "备用, 证件照片")
    private String img2Key;

    @Schema(title = "备用, 证件照片")
    @Parameter(description = "备用, 证件照片")
    private String img3Key;

    private List<MemberFundsVo> memberFunds;

    @Schema(title = "客服地址")
    @Parameter(description = "客服地址")
    private String onlineService;

    @Schema(title = "入金客户地址")
    @Parameter(description = "入金客户地址")
    private String fundService;


}
