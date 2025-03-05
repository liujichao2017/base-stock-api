package com.hm.stock.domain.coin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.coin.entity.CoinDelegation;
import com.hm.stock.domain.coin.mapper.CoinDelegationMapper;
import com.hm.stock.domain.coin.service.CoinContractDelegationService;
import com.hm.stock.domain.coin.service.CoinDelegationService;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.utils.ExecuteUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CoinDelegationServiceImpl extends ServiceImpl<CoinDelegationMapper, CoinDelegation> implements CoinDelegationService {
    @Autowired
    private CoinContractDelegationService coinContractDelegationService;

    @Override
    public List<CoinDelegation> selectList(CoinDelegation query) {
        QueryWrapper<CoinDelegation> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<CoinDelegation> selectByPage(CoinDelegation query, PageParam page) {
        QueryWrapper<CoinDelegation> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<CoinDelegation> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public CoinDelegation detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(CoinDelegation body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(CoinDelegation body) {
        return updateById(body);
    }

    @Override
    public void trigger(String symbol, BigDecimal last) {
        // 触发模式: 1: 现价大于等于委托价. 2: 现价小于等于委托价
        List<CoinDelegation> geLastPriceDelegation = baseMapper.selectGePrice(symbol,  last);
        List<CoinDelegation> leLastPriceDelegation = baseMapper.selectLePrice(symbol,  last);

        geLastPriceDelegation.addAll(leLastPriceDelegation);
        for (CoinDelegation coinDelegation : geLastPriceDelegation) {
            coinDelegation.setTriggerPrice(last);
            baseMapper.updateById(coinDelegation);
            ExecuteUtil.COIN_TRADE.execute(() -> coinContractDelegationService.handle(coinDelegation));
        }
    }





}
