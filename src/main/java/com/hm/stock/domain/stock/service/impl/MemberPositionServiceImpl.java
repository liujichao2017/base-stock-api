package com.hm.stock.domain.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.dailylimit.entity.StockDailyLimit;
import com.hm.stock.domain.dailylimit.vo.DailyLimitVo;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.market.mapper.MarketMapper;
import com.hm.stock.domain.member.entity.Member;
import com.hm.stock.domain.member.enums.FundsOperateTypeEnum;
import com.hm.stock.domain.member.enums.FundsSourceEnum;
import com.hm.stock.domain.member.service.MemberFundsService;
import com.hm.stock.domain.member.service.MemberService;
import com.hm.stock.domain.member.vo.FundsOperateVo;
import com.hm.stock.domain.stock.entity.MemberPosition;
import com.hm.stock.domain.stock.entity.Stock;
import com.hm.stock.domain.stock.mapper.MemberPositionMapper;
import com.hm.stock.domain.stock.service.MemberPositionService;
import com.hm.stock.domain.stock.service.StockClosedService;
import com.hm.stock.domain.stock.service.StockService;
import com.hm.stock.domain.stock.vo.MemberPositionVo;
import com.hm.stock.domain.stock.vo.PositionQueryVo;
import com.hm.stock.domain.stock.vo.StockBuyVo;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.enums.YNEnum;
import com.hm.stock.modules.execptions.ErrorResultCode;
import com.hm.stock.modules.utils.DateTimeUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MemberPositionServiceImpl extends ServiceImpl<MemberPositionMapper, MemberPosition> implements MemberPositionService {


    @Autowired
    private StockService stockService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberFundsService memberFundsService;
    @Autowired
    private MarketMapper marketMapper;
    @Autowired
    private StockClosedService stockClosedService;


    /**
     * @param status 持仓状态(必传): 1, 持仓. 2.平仓
     * @return
     */
    @Override
    public List<MemberPositionVo> selectList(String status) {
        QueryWrapper<MemberPosition> ew = new QueryWrapper<>();
        ew.eq("member_id", SessionInfo.getInstance().getId());
        ew.isNull("1".equals(status), "sell_order_time");
        ew.isNotNull("2".equals(status), "sell_order_time");

        List<MemberPosition> res = list(ew);
        if (LogicUtils.isEmpty(res)) {
            return Collections.emptyList();
        }
        List<MemberPositionVo> list = new ArrayList<>();
        Map<String, Stock> stockByGidMap = stockService.getStockByGids(
                res.stream().map(MemberPosition::getStockGid).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(Stock::getGid, s -> s));
        Map<Long, Market> marketMap = new HashMap<>();

        for (MemberPosition record : res) {
            MemberPositionVo memberPositionVo = new MemberPositionVo();
            Market market = marketMap.computeIfAbsent(record.getMarketId(), marketMapper::selectById);
            LogicUtils.assertNotNull(market, ErrorResultCode.E000001);
            Stock stock = stockByGidMap.get(record.getStockGid());
            LogicUtils.assertNotNull(stock, ErrorResultCode.E000001);
            if (LogicUtils.isNull(record.getSellOrderTime())) {
                queryProfitAndLose(market, record, stock.getLast());
            }
            BeanUtils.copyProperties(record, memberPositionVo);
            memberPositionVo.setCurrency(market.getCurrency());
            list.add(memberPositionVo);
        }
        return list;
    }


    @Override
    public PageDate<MemberPositionVo> selectByPage(PositionQueryVo query, PageParam page) {
        LogicUtils.assertNotBlank(query.getStatus(), ErrorResultCode.E000001);

        QueryWrapper<MemberPosition> ew = new QueryWrapper<>();
        ew.eq("member_id", SessionInfo.getInstance().getId());
        ew.isNull("1".equals(query.getStatus()), "sell_order_time");
        ew.isNotNull("2".equals(query.getStatus()), "sell_order_time");

        ew.orderByDesc("create_time");
        PageDTO<MemberPosition> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        List<MemberPositionVo> list = new ArrayList<>();
        if (LogicUtils.isNotEmpty(result.getRecords())) {
            Map<String, Stock> stockByGidMap = stockService.getStockByGids(
                    result.getRecords().stream().map(MemberPosition::getStockGid).collect(Collectors.toSet())
            ).stream().collect(Collectors.toMap(Stock::getGid, s -> s));
            Map<Long, Market> marketMap = new HashMap<>();

            for (MemberPosition record : result.getRecords()) {
                MemberPositionVo memberPositionVo = new MemberPositionVo();
                Market market = marketMap.computeIfAbsent(record.getMarketId(), marketMapper::selectById);
                LogicUtils.assertNotNull(market, ErrorResultCode.E000001);
                Stock stock = stockByGidMap.get(record.getStockGid());
                LogicUtils.assertNotNull(stock, ErrorResultCode.E000001);
                if (LogicUtils.isNull(record.getSellOrderTime())) {
                    queryProfitAndLose(market, record, stock.getLast());
                }

                BeanUtils.copyProperties(record, memberPositionVo);
                memberPositionVo.setCurrency(market.getCurrency());
                list.add(memberPositionVo);
            }

        }
        return PageDate.of(result.getTotal(), list);
    }

    @Override
    public MemberPosition detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(MemberPosition body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(MemberPosition body) {
        return updateById(body);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean buy(StockBuyVo body) {
        Member member = memberService.getAndCheck(FundsSourceEnum.STOCK);

        Stock stock = stockService.getStock(body.getStockGid());
        LogicUtils.assertNotNull(stock, ErrorResultCode.E000015);
        LogicUtils.assertTrue(YNEnum.no(stock.getIsLock()), ErrorResultCode.E000016);

        Market market = marketMapper.selectById(stock.getMarketId());
        LogicUtils.assertNotNull(market, ErrorResultCode.E000001);
        // 最新的股票价格
        stock = stockService.getStockByLatest(body.getStockGid(), market);
        // 校验交易时间
        checkMarketByBuy(market, stock, body.getNum(), true);

        BigDecimal totalAmt = stock.getLast().multiply(new BigDecimal(body.getNum()));
        BigDecimal principalAmt = totalAmt.divide(new BigDecimal(body.getLever()), 2, 4);

        MemberPosition memberPosition = createPosition(body, member, market, stock, totalAmt, principalAmt,
                                                       FundsSourceEnum.STOCK, null);

        LogicUtils.assertTrue(save(memberPosition), ErrorResultCode.E000001);

        FundsOperateVo fundsOperateVo = new FundsOperateVo();
        fundsOperateVo.setSource(FundsSourceEnum.STOCK);
        fundsOperateVo.setOperateType(FundsOperateTypeEnum.BUY);
        fundsOperateVo.setMemberId(member.getId());
        fundsOperateVo.setMarketId(market.getId());
        fundsOperateVo.setAmt(principalAmt);
        fundsOperateVo.setSourceId(memberPosition.getId());
        fundsOperateVo.setName(stock.getName());
        fundsOperateVo.setCode(stock.getCode());
        fundsOperateVo.set(FundsOperateVo.FundsInfoKey.STOCK_NAME, memberPosition.getStockName())
                .set(FundsOperateVo.FundsInfoKey.STOCK_CODE, memberPosition.getStockCode())
                .set(FundsOperateVo.FundsInfoKey.OCCUPANCY_AMT, principalAmt)
                .set(FundsOperateVo.FundsInfoKey.FEE_AMT, memberPosition.getAllProfitAndLose().negate())
                .build();
        memberFundsService.addOccupancyAmt(fundsOperateVo);
        return true;
    }

    @Override
    public void buy(StockDailyLimit stockDailyLimit, DailyLimitVo dailyLimitVo) {
        Member member = memberService.getAndCheck(FundsSourceEnum.STOCK);

        Stock stock = stockService.getStock(stockDailyLimit.getStockGid());
        LogicUtils.assertNotNull(stock, ErrorResultCode.E000015);
        LogicUtils.assertTrue(YNEnum.no(stock.getIsLock()), ErrorResultCode.E000016);

        Market market = marketMapper.selectById(stock.getMarketId());
        LogicUtils.assertNotNull(market, ErrorResultCode.E000001);
        // 校验交易时间
        checkMarketByBuy(market, stock, dailyLimitVo.getNum(), false);

        BigDecimal totalAmt = stockDailyLimit.getPrice().multiply(new BigDecimal(dailyLimitVo.getNum()));
        Long level = dailyLimitVo.getLevel();
        level = LogicUtils.isNotNull(level) ? level : 1;
        BigDecimal principalAmt = totalAmt.divide(new BigDecimal(level), 2, 4);

        StockBuyVo body = new StockBuyVo();
        body.setStockGid(stock.getGid());
        body.setDirection("1");
        body.setNum(dailyLimitVo.getNum());
        body.setLever(level);
        stock.setLast(stockDailyLimit.getPrice());
        MemberPosition memberPosition = createPosition(body, member, market, stock, totalAmt, principalAmt,
                                                       FundsSourceEnum.DAILY_LIMIT, stockDailyLimit.getId());
        LogicUtils.assertTrue(save(memberPosition), ErrorResultCode.E000001);


        FundsOperateVo fundsOperateVo = new FundsOperateVo();
        fundsOperateVo.setSource(FundsSourceEnum.DAILY_LIMIT);
        fundsOperateVo.setOperateType(FundsOperateTypeEnum.BUY);
        fundsOperateVo.setMemberId(member.getId());
        fundsOperateVo.setMarketId(market.getId());
        fundsOperateVo.setAmt(principalAmt);
        fundsOperateVo.setSourceId(memberPosition.getSourceId());
        fundsOperateVo.setName(stock.getName());
        fundsOperateVo.setCode(stock.getCode());
        fundsOperateVo.set(FundsOperateVo.FundsInfoKey.STOCK_NAME, memberPosition.getStockName())
                .set(FundsOperateVo.FundsInfoKey.STOCK_CODE, memberPosition.getStockCode())
                .set(FundsOperateVo.FundsInfoKey.OCCUPANCY_AMT, principalAmt)
                .set(FundsOperateVo.FundsInfoKey.FEE_AMT, memberPosition.getAllProfitAndLose().negate())
                .build();
        memberFundsService.addOccupancyAmt(fundsOperateVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean sell(Long id) {
        Member member = memberService.getAndCheck(FundsSourceEnum.STOCK);

        MemberPosition memberPosition = getById(id);
        LogicUtils.assertNotNull(memberPosition, ErrorResultCode.E000024);
        LogicUtils.assertTrue(YNEnum.no(memberPosition.getIsLock()), ErrorResultCode.E000016);
        LogicUtils.assertTrue(memberPosition.getSellOrderTime() == null, ErrorResultCode.E000001);

        Market market = marketMapper.selectById(memberPosition.getMarketId());
        LogicUtils.assertNotNull(market, ErrorResultCode.E000001);
        // 最新的股票价格
        Stock stock = stockService.getStockByLatest(memberPosition.getStockGid(), market);
        // 校验
        checkMarketBySell(market, stock, memberPosition);

        // 设置费用
        setProfitAndLose(market, memberPosition, stock.getLast());
        memberPosition.setSellOrderTime(new Date());

        FundsOperateVo fundsOperateVo = new FundsOperateVo();
        fundsOperateVo.setSource(FundsSourceEnum.STOCK);
        fundsOperateVo.setOperateType(FundsOperateTypeEnum.SELL);
        fundsOperateVo.setMemberId(member.getId());
        fundsOperateVo.setAmt(memberPosition.getPrincipalAmt());
        fundsOperateVo.setMarketId(market.getId());
        fundsOperateVo.setSourceId(memberPosition.getId());
        fundsOperateVo.setName(memberPosition.getStockName());
        fundsOperateVo.setCode(memberPosition.getStockCode());
        fundsOperateVo.set(FundsOperateVo.FundsInfoKey.STOCK_NAME, memberPosition.getStockName())
                .set(FundsOperateVo.FundsInfoKey.STOCK_CODE, memberPosition.getStockCode())
                .set(FundsOperateVo.FundsInfoKey.OCCUPANCY_AMT, memberPosition.getPrincipalAmt())
                .set(FundsOperateVo.FundsInfoKey.PROFIT_LOSS_AMT, memberPosition.getProfitAndLose())
                .set(FundsOperateVo.FundsInfoKey.ALL_PROFIT_LOSS_AMT, memberPosition.getAllProfitAndLose())
                .set(FundsOperateVo.FundsInfoKey.FEE_AMT,
                     memberPosition.getProfitAndLose().subtract(memberPosition.getAllProfitAndLose()))
                .build();
        memberFundsService.subOccupancyAmt(fundsOperateVo);

        fundsOperateVo = new FundsOperateVo();
        fundsOperateVo.setSource(FundsSourceEnum.STOCK);
        fundsOperateVo.setOperateType(FundsOperateTypeEnum.PROFIT_LOSS);
        fundsOperateVo.setMemberId(member.getId());
        fundsOperateVo.setAmt(memberPosition.getAllProfitAndLose());
        fundsOperateVo.setMarketId(market.getId());
        fundsOperateVo.setSourceId(memberPosition.getId());
        fundsOperateVo.setName(memberPosition.getStockName());
        fundsOperateVo.setCode(memberPosition.getStockCode());
        fundsOperateVo.setVisible(YNEnum.NO.getType());
        memberFundsService.addProfitAmt(fundsOperateVo);

        QueryWrapper<MemberPosition> ew = new QueryWrapper<>();
        ew.eq("id", memberPosition.getId());
        ew.isNull("sell_order_time");
        LogicUtils.assertTrue(update(memberPosition, ew), ErrorResultCode.E000001);
        return true;
    }


    private void queryProfitAndLose(Market market, MemberPosition memberPosition, BigDecimal last) {
        BigDecimal sellAmt = last.multiply(new BigDecimal(memberPosition.getNum()));
        BigDecimal profitAndLose = sellAmt.subtract(memberPosition.getTotalAmt());
        BigDecimal totalFee = memberPosition.getBuyFee().add(memberPosition.getOtherFee());

        memberPosition.setSellOrderPrice(last);
        memberPosition.setProfitAndLose(profitAndLose);
        memberPosition.setAllProfitAndLose(profitAndLose.subtract(totalFee));
    }

    private static void setProfitAndLose(Market market, MemberPosition memberPosition, BigDecimal last) {
        BigDecimal sellAmt = last.multiply(new BigDecimal(memberPosition.getNum()));
        BigDecimal profitAndLose = sellAmt.subtract(memberPosition.getTotalAmt());
        BigDecimal totalFee = memberPosition.getBuyFee().add(memberPosition.getOtherFee());

        // 卖出
        BigDecimal sellFee = sellAmt.multiply(market.getSellFee());
        sellFee = sellFee.setScale(2, 4);
        totalFee = totalFee.add(sellFee);

        // 其他费用-印花税
        BigDecimal otherFee = sellAmt.multiply(market.getSellOtherFee());
        otherFee = otherFee.setScale(2, 4);
        totalFee = totalFee.add(otherFee);

        memberPosition.setSellFee(sellFee);
        memberPosition.setOtherFee(memberPosition.getOtherFee().add(otherFee));
        memberPosition.setSellOrderPrice(last);
        memberPosition.setProfitAndLose(profitAndLose);
        memberPosition.setAllProfitAndLose(profitAndLose.subtract(totalFee));
    }


    private static MemberPosition createPosition(StockBuyVo body, Member member, Market market,
            Stock stock, BigDecimal totalAmt, BigDecimal principalAmt,
            FundsSourceEnum fundsSourceEnum, Long sourceId) {
        MemberPosition memberPosition = new MemberPosition();
        memberPosition.setPositionSn(LogicUtils.getOrderSn("S"));
        memberPosition.setMemberId(member.getId());
        memberPosition.setMarketId(market.getId());
        memberPosition.setStockName(stock.getName());
        memberPosition.setStockCode(stock.getCode());
        memberPosition.setStockGid(stock.getGid());
        memberPosition.setBuyOrderTime(new Date());
        memberPosition.setBuyOrderPrice(stock.getLast());
        memberPosition.setDirection(body.getDirection());
        memberPosition.setNum(body.getNum());
        memberPosition.setLever(body.getLever());
        memberPosition.setTotalAmt(totalAmt);
        memberPosition.setPrincipalAmt(principalAmt);

        BigDecimal totalFee = BigDecimal.ZERO;

        BigDecimal buyFee = market.getBuyFee();
        buyFee = totalAmt.multiply(buyFee);
        totalFee = totalFee.add(buyFee);

        // 印花税
        BigDecimal otherFee = market.getOtherFee();
        otherFee = totalAmt.multiply(otherFee);
        totalFee = totalFee.add(otherFee);

        memberPosition.setBuyFee(buyFee);
        memberPosition.setOtherFee(otherFee);
        memberPosition.setAllProfitAndLose(totalFee.negate());
        memberPosition.setSource(fundsSourceEnum.getType());
        memberPosition.setSourceId(sourceId);
        return memberPosition;
    }

    private void checkMarketByBuy(Market market, Stock stock, Long num, boolean chgPctLimit) {
        LogicUtils.assertTrue(stock.getLast().compareTo(BigDecimal.ZERO) > 0, ErrorResultCode.E000001);
        // 数量限制
        LogicUtils.assertTrue(num > market.getMinNum(), ErrorResultCode.E000017);
        LogicUtils.assertTrue(num < market.getMaxNum(), ErrorResultCode.E000018);

        // 交易时间
        checkTradingHours(market);

        // 涨停
        if (chgPctLimit && LogicUtils.isNotNull(stock.getChgPct()) && stock.getChgPct().compareTo(BigDecimal.ZERO) > 0) {
            LogicUtils.assertTrue(stock.getChgPct().compareTo(market.getChgPctLimit()) < 0, ErrorResultCode.E000062);
        }
    }

    private void checkMarketBySell(Market market, Stock stock, MemberPosition memberPosition) {
        LogicUtils.assertTrue(stock.getLast().compareTo(BigDecimal.ZERO) > 0, ErrorResultCode.E000001);

        checkTradingHours(market);

        // 限制的平仓时间
        if (LogicUtils.isNotNull(memberPosition.getSellTimeLimit())) {
            LogicUtils.assertTrue(DateTimeUtil.beforeCurrentTime(memberPosition.getSellTimeLimit()),
                                  ErrorResultCode.E000055);
        } else {
            LogicUtils.assertTrue(DateTimeUtil.isCanSell(memberPosition.getBuyOrderTime(), market.getSellTime()),
                                  ErrorResultCode.E000055);
        }

//        // 涨停
//        if (LogicUtils.isNotNull(stock.getChgPct()) && stock.getChgPct().compareTo(BigDecimal.ZERO) > 0) {
//            LogicUtils.assertTrue(stock.getChgPct().compareTo(market.getChgPctLimit()) < 0, ErrorResultCode.E000062);
//        }
    }

    private void checkTradingHours(Market market) {
        // 休市
        LogicUtils.assertFalse(stockClosedService.isClosed(market), ErrorResultCode.E000020);


        // 交易时间
        LogicUtils.assertTrue(
                DateTimeUtil.isExpire(market.getTimeZone(), market.getTransAmBegin(), market.getTransAmEnd()) ||
                        DateTimeUtil.isExpire(market.getTimeZone(), market.getTransPmBegin(), market.getTransPmEnd()),
                ErrorResultCode.E000021);
    }

}
