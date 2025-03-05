package com.hm.stock.domain.ipo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.ipo.entity.Ipo;
import com.hm.stock.domain.ipo.entity.IpoRecord;
import com.hm.stock.domain.ipo.mapper.IpoMapper;
import com.hm.stock.domain.ipo.mapper.IpoRecordMapper;
import com.hm.stock.domain.ipo.service.IpoService;
import com.hm.stock.domain.ipo.vo.IpoBuyVo;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.market.mapper.MarketMapper;
import com.hm.stock.domain.member.entity.Member;
import com.hm.stock.domain.stock.service.StockClosedService;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.execptions.ErrorResultCode;
import com.hm.stock.modules.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class IpoServiceImpl extends ServiceImpl<IpoMapper, Ipo> implements IpoService {
    @Autowired
    private MarketMapper marketMapper;
    @Autowired
    private StockClosedService stockClosedService;
    @Autowired
    private IpoRecordMapper ipoRecordMapper;

    @Override
    public List<Ipo> selectList(Ipo query) {
        QueryWrapper<Ipo> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<Ipo> selectByPage(Ipo query, PageParam page) {
        QueryWrapper<Ipo> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<Ipo> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public Ipo detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(Ipo body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(Ipo body) {
        return updateById(body);
    }

    @Override
    public Boolean buy(IpoBuyVo body) {
        Ipo ipo = detail(body.getId());
        LogicUtils.assertNotNull(ipo, ErrorResultCode.E000028);

        Market market = marketMapper.selectById(ipo.getMarketId());
        LogicUtils.assertNotNull(market, ErrorResultCode.E000001);

        Member member = SessionInfo.getMember();

        // 交易时间
        LogicUtils.assertFalse(stockClosedService.isClosed(market), ErrorResultCode.E000020);
        LogicUtils.assertTrue(DateTimeUtil.isExpire(ipo.getStartBuyTime(), ipo.getEndBuyTime()),
                              ErrorResultCode.E000021);


        IpoRecord ipoRecord = new IpoRecord();
        ipoRecord.setMarketId(market.getId());
        ipoRecord.setMemberId(member.getId());
        ipoRecord.setIpoId(ipo.getId());
        ipoRecord.setType(ipo.getType());
        ipoRecord.setName(ipo.getName());
        ipoRecord.setCode(ipo.getCode());
        ipoRecord.setPrice(ipo.getPrice());
        ipoRecord.setNums(body.getNum());
        ipoRecord.setStatus("1");
        ipoRecord.setNotifyStatus("0");
        ipoRecord.setTotalAmt(new BigDecimal("0"));
        ipoRecord.setTransferAmt(new BigDecimal("0"));

        ipoRecordMapper.insert(ipoRecord);
        return true;
    }
}
