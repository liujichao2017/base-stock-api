package com.hm.stock.domain.ipo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.ipo.entity.Ipo;
import com.hm.stock.domain.ipo.vo.IpoBuyVo;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface IpoService extends IService<Ipo> {
    List<Ipo> selectList(Ipo query);

    PageDate<Ipo> selectByPage(Ipo query, PageParam page);

    Ipo detail(Long id);

    Long add(Ipo body);

    boolean delete(Long id);

    boolean update(Ipo body);

    Boolean buy(IpoBuyVo body);
}
