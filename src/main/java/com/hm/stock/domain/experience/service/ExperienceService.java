package com.hm.stock.domain.experience.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.experience.entity.Experience;
import com.hm.stock.domain.experience.vo.ExperienceVo;
import com.hm.stock.domain.member.enums.FundsSourceEnum;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ExperienceService extends IService<Experience> {
    List<Experience> selectList(Long marketId);

    PageDate<Experience> selectByPage(Experience query, PageParam page);

    Experience detail(Long id);

    Long add(Experience body);

    boolean delete(Long id);

    boolean update(Experience body);

    Map<Long, BigDecimal> queryEnableAmt(Long memberId);

    Map<Long, ExperienceVo> queryExperience(Long memberId);

    BigDecimal use(Long marketId, Long memberId, FundsSourceEnum fundsSourceEnum, Long sourceId, BigDecimal needAmt);

    BigDecimal queryUseAmt(FundsSourceEnum fundsSourceEnum, Long sourceId);

    void rollback(FundsSourceEnum fundsSourceEnum, Long sourceId);

}
