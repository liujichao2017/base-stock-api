package com.hm.stock.domain.coin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.coin.entity.CoinContract;
import com.hm.stock.domain.coin.entity.CoinSymbols;
import com.hm.stock.domain.coin.mapper.CoinContractMapper;
import com.hm.stock.domain.coin.mapper.CoinSymbolsMapper;
import com.hm.stock.domain.coin.service.CoinContractDelegationService;
import com.hm.stock.domain.coin.service.CoinContractService;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CoinContractServiceImpl extends ServiceImpl<CoinContractMapper, CoinContract> implements CoinContractService {

    @Autowired
    private CoinContractDelegationService contractDelegationService;
    @Autowired
    private CoinSymbolsMapper coinSymbolsMapper;

    @Override
    public List<CoinContract> selectList(CoinContract query) {
        QueryWrapper<CoinContract> ew = new QueryWrapper<>();
        query.setMemberId(SessionInfo.getInstance().getId());
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        List<CoinContract> result = list(ew);
        Map<String, CoinSymbols> lastMap = new HashMap<>();
        if (LogicUtils.isNotEmpty(result)) {
            for (CoinContract record : result) {
                CoinSymbols coinSymbols =
                        lastMap.computeIfAbsent(record.getSymbol(), coinSymbolsMapper::selectBySymbol);
                if (LogicUtils.isNotNull(coinSymbols)) {
                    record.setProfitAmt(contractDelegationService.profitCalculation(record, coinSymbols.getPrice()));
                }
            }
        }
        return result;
    }

    @Override
    public PageDate<CoinContract> selectByPage(CoinContract query, PageParam page) {
        QueryWrapper<CoinContract> ew = new QueryWrapper<>();
        query.setMemberId(SessionInfo.getInstance().getId());
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<CoinContract> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        Map<String, CoinSymbols> lastMap = new HashMap<>();
        if (LogicUtils.isNotEmpty(result.getRecords())) {
            for (CoinContract record : result.getRecords()) {
                CoinSymbols coinSymbols =
                        lastMap.computeIfAbsent(record.getSymbol(), coinSymbolsMapper::selectBySymbol);
                if (LogicUtils.isNotNull(coinSymbols)) {
                    record.setProfitAmt(contractDelegationService.profitCalculation(record, coinSymbols.getPrice()));
                }
            }
        }
        return PageDate.of(result);
    }

    @Override
    public CoinContract detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(CoinContract body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(CoinContract body) {
        return updateById(body);
    }
}
