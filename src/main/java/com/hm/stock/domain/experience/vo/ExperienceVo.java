package com.hm.stock.domain.experience.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class ExperienceVo {
    @Schema(title = "总体验金")
    @Parameter(description = "总体验金")
    private BigDecimal totalAmt = BigDecimal.ZERO;

    @Schema(title = "可使用的体验金")
    @Parameter(description = "可使用的体验金")
    private BigDecimal enableAmt = BigDecimal.ZERO;

    @Schema(title = "锁定的体验金")
    @Parameter(description = "锁定的体验金")
    private BigDecimal lockAmt = BigDecimal.ZERO;

    @Schema(title = "未解锁的体验金")
    @Parameter(description = "未解锁的体验金")
    private List<ExperienceInfo> locks = new ArrayList<>();

    @Data
    public static class ExperienceInfo {
        @Schema(title = "体验金")
        @Parameter(description = "体验金")
        private BigDecimal experienceAmt = BigDecimal.ZERO;

        @Schema(title = "需要充值多少钱才能解锁")
        @Parameter(description = "需要充值多少钱才能解锁")
        private BigDecimal unlockAmt = BigDecimal.ZERO;
    }

    public void addTotalAmt(BigDecimal amt){
        this.totalAmt = this.totalAmt.add(amt);
    }

    public void addEnableAmt(BigDecimal amt){
        this.enableAmt = this.enableAmt.add(amt);
    }

    public void addLockAmt(BigDecimal amt){
        this.lockAmt = this.lockAmt.add(amt);
    }

    public void addInfo(BigDecimal experienceAmt, BigDecimal unlockAmt) {
        ExperienceInfo experienceInfo = new ExperienceInfo();
        experienceInfo.setExperienceAmt(experienceAmt);
        experienceInfo.setUnlockAmt(unlockAmt);
        this.locks.add(experienceInfo);
    }
}
