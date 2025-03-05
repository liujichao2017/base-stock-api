package com.hm.stock.domain.borrow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.borrow.entity.BorrowCoupons;
import com.hm.stock.domain.borrow.vo.BorrowBuyVo;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface BorrowCouponsService extends IService<BorrowCoupons> {
    List<BorrowCoupons> selectList(BorrowCoupons query);

    PageDate<BorrowCoupons> selectByPage(BorrowCoupons query, PageParam page);

    BorrowCoupons detail(Long id);

    Long add(BorrowCoupons body);

    boolean delete(Long id);

    boolean update(BorrowCoupons body);

    Boolean buy(BorrowBuyVo body);

    Boolean compute();
}
