package com.hm.stock.domain.otc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.otc.entity.Otc;
import com.hm.stock.domain.otc.entity.OtcRecord;
import com.hm.stock.domain.otc.vo.OtcBuyVo;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface OtcService extends IService<Otc> {
    List<Otc> selectList(Otc query);

    PageDate<Otc> selectByPage(Otc query, PageParam page);

    Otc detail(Long id);

    Long add(Otc body);

    boolean delete(Long id);

    boolean update(Otc body);

    Boolean buy(OtcBuyVo buyVo);
}
