package com.hm.stock.domain.stock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.dailylimit.entity.StockDailyLimit;
import com.hm.stock.domain.dailylimit.vo.DailyLimitVo;
import com.hm.stock.domain.stock.entity.MemberPosition;
import com.hm.stock.domain.stock.vo.MemberPositionVo;
import com.hm.stock.domain.stock.vo.PositionQueryVo;
import com.hm.stock.domain.stock.vo.StockBuyVo;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface MemberPositionService extends IService<MemberPosition> {
    List<MemberPositionVo> selectList(String status);

    PageDate<MemberPositionVo> selectByPage(PositionQueryVo query, PageParam page);

    MemberPosition detail(Long id);

    Long add(MemberPosition body);

    boolean delete(Long id);

    boolean update(MemberPosition body);

    Boolean buy(StockBuyVo stockBuyVo);

    Boolean sell(Long id);

    void buy(StockDailyLimit stockDailyLimit, DailyLimitVo dailyLimitVo);
}
