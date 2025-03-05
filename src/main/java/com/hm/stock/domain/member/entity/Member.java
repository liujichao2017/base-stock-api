package com.hm.stock.domain.member.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.lang.Long;
import java.lang.String;

/**
* 会员 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class Member extends BaseEntity {

    @Schema(title = "用户ID")
    @Parameter(description = "用户ID")
    private Long userId;

    @Schema(title = "手机号")
    @Parameter(description = "手机号")
    private String phone;

    @Schema(title = "密码")
    @Parameter(description = "密码")
    private String password;

    @Schema(title = "提现密码")
    @Parameter(description = "提现密码")
    private String withPwd;

    @Schema(title = "真实姓名")
    @Parameter(description = "真实姓名")
    private String realName;

    @Schema(title = "头像照片")
    @Parameter(description = "头像照片")
    private String avatarImg;

    @Schema(title = "实盘-0 模拟-1")
    @Parameter(description = "实盘-0 模拟-1")
    private String accountType;

    @Schema(title = "证件号")
    @Parameter(description = "证件号")
    private String idCard;

    @Schema(title = "实名状态  0: 未实名. 1: 已实名 ,2: 审核不通过,3: 待审批")
    @Parameter(description = "实名状态  0: 未实名. 1: 已实名 ,2: 审核不通过,3: 待审批")
    private String isActive;

    @Schema(title = "视频认证状态: 0 未认证, 1: 已认证")
    @Parameter(description = "视频认证状态: 0 未认证, 1: 已认证")
    private String videoActive;

    @Schema(title = "实名不通过,原因")
    @Parameter(description = "实名不通过,原因")
    private String activeMsg;

    @Schema(title = "备用, 证件照片")
    @Parameter(description = "备用, 证件照片")
    private String img1Key;

    @Schema(title = "备用, 证件照片")
    @Parameter(description = "备用, 证件照片")
    private String img2Key;

    @Schema(title = "备用, 证件照片")
    @Parameter(description = "备用, 证件照片")
    private String img3Key;

    @Schema(title = "允许登录 see YNEnum, 0: 否. 1: 是 ")
    @Parameter(description = "允许登录 see YNEnum, 0: 否. 1: 是 ")
    private String isLogin;

    @Schema(title = "股票交易 see YNEnum, 0: 否. 1: 是 ")
    @Parameter(description = "股票交易 see YNEnum, 0: 否. 1: 是 ")
    private String isStock;

    @Schema(title = "杠杆交易 see YNEnum, 0: 否. 1: 是 ")
    @Parameter(description = "杠杆交易 see YNEnum, 0: 否. 1: 是 ")
    private String isLever;

    @Schema(title = "杠杆交易项")
    @Parameter(description = "杠杆交易项")
    private String levelItem;

    @Schema(title = "贷款额度")
    @Parameter(description = "贷款额度")
    private BigDecimal loanAmt;


    @TableField(exist = false)
    @Schema(title = "短信验证码: null 则不校验")
    @Parameter(description = "短信验证码: null 则不校验")
    private String smsCode;

}
