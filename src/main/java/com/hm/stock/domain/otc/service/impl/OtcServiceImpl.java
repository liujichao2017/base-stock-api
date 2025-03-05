package com.hm.stock.domain.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.market.mapper.MarketMapper;
import com.hm.stock.domain.member.entity.Member;
import com.hm.stock.domain.member.enums.FundsOperateTypeEnum;
import com.hm.stock.domain.member.enums.FundsSourceEnum;
import com.hm.stock.domain.member.service.MemberFundsService;
import com.hm.stock.domain.member.vo.FundsOperateVo;
import com.hm.stock.domain.otc.entity.Otc;
import com.hm.stock.domain.otc.entity.OtcRecord;
import com.hm.stock.domain.otc.mapper.OtcMapper;
import com.hm.stock.domain.otc.mapper.OtcRecordMapper;
import com.hm.stock.domain.otc.service.OtcService;
import com.hm.stock.domain.otc.vo.OtcBuyVo;
import com.hm.stock.domain.stock.entity.Stock;
import com.hm.stock.domain.stock.service.StockClosedService;
import com.hm.stock.domain.stock.service.StockService;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.execptions.ErrorResultCode;
import com.hm.stock.modules.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OtcServiceImpl extends ServiceImpl<OtcMapper, Otc> implements OtcService {
    @Autowired
    private MarketMapper marketMapper;
    @Autowired
    private StockClosedService stockClosedService;
    @Autowired
    private OtcRecordMapper otcRecordMapper;
    @Autowired
    private MemberFundsService memberFundsService;
    @Autowired
    private StockService stockService;


    @Override
    public List<Otc> selectList(Otc query) {
        QueryWrapper<Otc> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<Otc> selectByPage(Otc query, PageParam page) {
        query.setStatus(1L);
        QueryWrapper<Otc> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<Otc> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        if (LogicUtils.isNotEmpty(result.getRecords())) {
            List<Stock> stockByGids = stockService.getStockByGids(
                    result.getRecords().stream().map(Otc::getStockGid).collect(Collectors.toList()));
            Map<String, Stock> stockMap = stockByGids.stream()
                    .collect(Collectors.toMap(Stock::getGid, s->s));
            for (Otc record : result.getRecords()) {
                Stock stock = stockMap.get(record.getStockGid());
                if (stock == null) {
                    record.setLast(record.getPrice());
                    record.setLast(BigDecimal.ZERO);

                }else{
                    record.setLast(stock.getLast());
                    record.setChgPct(stock.getChgPct());
                }
            }
        }
        return PageDate.of(result);
    }

    @Override
    public Otc detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(Otc body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(Otc body) {
        return updateById(body);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean buy(OtcBuyVo buyVo) {
        LogicUtils.assertTrue(buyVo.getNums() > 0, ErrorResultCode.E000061, 0);
        Otc otc = detail(buyVo.getId());
        LogicUtils.assertNotNull(otc, ErrorResultCode.E000028);

        Market market = marketMapper.selectById(otc.getMarketId());
        LogicUtils.assertNotNull(market, ErrorResultCode.E000001);

        Member member = SessionInfo.getMember();

        // 交易时间
        LogicUtils.assertFalse(stockClosedService.isClosed(market), ErrorResultCode.E000020);
        LogicUtils.assertTrue(DateTimeUtil.isExpire(otc.getStartBuyTime(), otc.getEndBuyTime()),
                              ErrorResultCode.E000021);

        long lever = LogicUtils.isNotNull(buyVo.getLever()) ? buyVo.getLever() : 1;
        BigDecimal buyAmt = otc.getPrice()
                .multiply(BigDecimal.valueOf(buyVo.getNums()))
                .divide(BigDecimal.valueOf(lever), 2, RoundingMode.HALF_UP);


        OtcRecord otcRecord = new OtcRecord();
        otcRecord.setMarketId(market.getId());
        otcRecord.setMemberId(member.getId());
        otcRecord.setStockName(otc.getStockName());
        otcRecord.setStockCode(otc.getStockCode());
        otcRecord.setStockGid(otc.getStockGid());
        otcRecord.setPrice(otc.getPrice());
        otcRecord.setType(otc.getType());
        otcRecord.setNums(buyVo.getNums());
        otcRecord.setDirection(LogicUtils.isNotBlank(buyVo.getDirection()) ? buyVo.getDirection() : "1");
        otcRecord.setLever(lever);
        otcRecord.setBuyAmt(buyAmt);
        otcRecord.setSellTime(otc.getSellTime());
        otcRecord.setStatus("1");
        LogicUtils.assertTrue(otcRecordMapper.insert(otcRecord) == 1, ErrorResultCode.E000001);

        FundsOperateVo fundsOperateVo = new FundsOperateVo();
        fundsOperateVo.setSource(FundsSourceEnum.OTC);
        fundsOperateVo.setOperateType(FundsOperateTypeEnum.FREEZE);
        fundsOperateVo.setMember(member);
        fundsOperateVo.setMarket(market);
        fundsOperateVo.setAmt(otcRecord.getBuyAmt());
        fundsOperateVo.setNegative(false);
        fundsOperateVo.setSourceId(otcRecord.getId());
        fundsOperateVo.setName(otcRecord.getStockName());
        fundsOperateVo.setCode(otcRecord.getStockCode());
        fundsOperateVo.set(FundsOperateVo.FundsInfoKey.STOCK_NAME, otcRecord.getStockName())
                .set(FundsOperateVo.FundsInfoKey.STOCK_CODE, otcRecord.getStockCode())
                .set(FundsOperateVo.FundsInfoKey.FREEZE_AMT, otcRecord.getBuyAmt())
                .build();

        memberFundsService.addFreezeAmt(fundsOperateVo);
        return true;
    }
}
