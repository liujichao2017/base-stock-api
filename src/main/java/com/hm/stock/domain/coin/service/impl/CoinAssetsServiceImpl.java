package com.hm.stock.domain.coin.service.impl;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.coin.entity.CoinAssets;
import com.hm.stock.domain.coin.mapper.CoinAssetsMapper;
import com.hm.stock.domain.coin.service.CoinAssetsService;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.execptions.CommonResultCode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoinAssetsServiceImpl extends ServiceImpl<CoinAssetsMapper, CoinAssets> implements CoinAssetsService {

    @Override
    public List<CoinAssets> my(Long marketId) {
        CoinAssets query = new CoinAssets();
        query.setMemberId(SessionInfo.getInstance().getId());
        query.setMarketId(marketId);
        return selectList(query);
    }

    @Override
    public List<CoinAssets> selectList(CoinAssets query) {
        QueryWrapper<CoinAssets> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<CoinAssets> selectByPage(CoinAssets query, PageParam page) {
        QueryWrapper<CoinAssets> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<CoinAssets> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public CoinAssets detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(CoinAssets body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(CoinAssets body) {
        return updateById(body);
    }

    @Override
    public CoinAssets get(Long marketId, Long memberId, String coin) {
        QueryWrapper<CoinAssets> ew = new QueryWrapper<>();
        ew.eq("member_id", memberId);
        ew.eq("coin", coin);
        ew.eq("market_id", marketId);
        CoinAssets coinAssets = getOne(ew);
        if (LogicUtils.isNull(coinAssets)) {
            coinAssets = new CoinAssets();
            coinAssets.setType("1");
            coinAssets.setMarketId(marketId);
            coinAssets.setMemberId(memberId);
            coinAssets.setCoin(coin);
            coinAssets.setNum(new BigDecimal("0"));
            LogicUtils.assertTrue(save(coinAssets), CommonResultCode.INTERNAL_ERROR);
        }
        return coinAssets;
    }

    @Override
    public void plus(Long id, BigDecimal num) {
        LogicUtils.assertEquals(baseMapper.add(id, num), 1, CommonResultCode.INTERNAL_ERROR);
    }

    @Override
    public void reduce(Long id, BigDecimal num) {
        LogicUtils.assertEquals(baseMapper.sub(id, num), 1, CommonResultCode.INTERNAL_ERROR);
    }
}
