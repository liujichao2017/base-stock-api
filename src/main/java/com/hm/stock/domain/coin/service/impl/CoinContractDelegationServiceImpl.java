package com.hm.stock.domain.coin.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.coin.entity.*;
import com.hm.stock.domain.coin.enums.DeliveryTimeUnitEnum;
import com.hm.stock.domain.coin.mapper.*;
import com.hm.stock.domain.coin.service.CoinAssetsService;
import com.hm.stock.domain.coin.service.CoinContractDelegationService;
import com.hm.stock.domain.coin.vo.BuyCoinContractVo;
import com.hm.stock.domain.coin.vo.CalculateContractParamVo;
import com.hm.stock.domain.coin.vo.CalculateContractVo;
import com.hm.stock.domain.coin.vo.UpdateContractVo;
import com.hm.stock.domain.experience.service.ExperienceService;
import com.hm.stock.domain.market.mapper.MarketMapper;
import com.hm.stock.domain.member.entity.Member;
import com.hm.stock.domain.member.enums.FundsOperateTypeEnum;
import com.hm.stock.domain.member.enums.FundsSourceEnum;
import com.hm.stock.domain.member.mapper.MemberMapper;
import com.hm.stock.domain.member.service.MemberFundsService;
import com.hm.stock.domain.member.vo.FundsOperateVo;
import com.hm.stock.modules.common.CoinDelayed;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.enums.YNEnum;
import com.hm.stock.modules.execptions.CommonResultCode;
import com.hm.stock.modules.execptions.ErrorResultCode;
import com.hm.stock.modules.utils.ExecuteUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CoinContractDelegationServiceImpl extends ServiceImpl<CoinContractDelegationMapper, CoinContractDelegation> implements CoinContractDelegationService {
    @Autowired
    private CoinSymbolsMapper coinSymbolsMapper;
    @Autowired
    private MarketMapper marketMapper;
    @Autowired
    private MemberFundsService memberFundsService;
    @Autowired
    private CoinDelegationMapper coinDelegationMapper;
    @Autowired
    private CoinDeliveryMapper coinDeliveryMapper;
    @Autowired
    private CoinContractMapper coinContractMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private CoinAssetsService coinAssetsService;
    @Autowired
    private ExperienceService experienceService;

    @Override
    public List<CoinContractDelegation> selectList(CoinContractDelegation query) {
        QueryWrapper<CoinContractDelegation> ew = new QueryWrapper<>();
        query.setMemberId(SessionInfo.getInstance().getId());
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<CoinContractDelegation> selectByPage(CoinContractDelegation query, PageParam page) {
        QueryWrapper<CoinContractDelegation> ew = new QueryWrapper<>();
        query.setMemberId(SessionInfo.getInstance().getId());
        ew.setEntity(query);
        ew.in(LogicUtils.isNotEmpty(query.getStatusList()), "status", query.getStatusList());
        ew.orderByDesc("create_time");
        PageDTO<CoinContractDelegation> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public CoinContractDelegation detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(CoinContractDelegation body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(CoinContractDelegation body) {
        return updateById(body);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean buy(BuyCoinContractVo body) {
        CoinSymbols symbol = coinSymbolsMapper.selectBySymbol(body.getSymbol());
        LogicUtils.assertNotNull(symbol, ErrorResultCode.E000039);
        LogicUtils.assertEquals(1L, symbol.getTradeState(), ErrorResultCode.E000039);
        Member member = SessionInfo.getMember();

        CoinContractDelegation delegation = buildDelegation(body, symbol, member);
        LogicUtils.assertEquals(baseMapper.insert(delegation), 1, CommonResultCode.INTERNAL_ERROR);

        if ("1".equals(delegation.getPriceType())) {
            return submitDelegation(symbol, member, delegation, body.getLimitPriceDirection());
        }
        return createContract(symbol, member, delegation, symbol.getPrice());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean sell(Long id) {
        CoinContract coinContract = coinContractMapper.selectById(id);
        LogicUtils.assertNotNull(coinContract, ErrorResultCode.E000048);

        CoinSymbols symbol = coinSymbolsMapper.selectBySymbol(coinContract.getSymbol());
        LogicUtils.assertNotNull(symbol, ErrorResultCode.E000039);
        closeContract(symbol, coinContract, symbol.getPrice());
        coinDelegationMapper.deleteByContractLimit(coinContract.getId());
        return true;
    }

    @Override
    @Transactional
    public Boolean updateProfitLossLimit(UpdateContractVo body) {
        CoinContract coinContract = coinContractMapper.selectById(body.getId());
        LogicUtils.assertNotNull(coinContract, ErrorResultCode.E000048);
        coinContract.setStopLossAmt(body.getStopLossAmt());
        coinContract.setStopProfitAmt(body.getStopProfitAmt());
        coinContractMapper.updateStopAmt(coinContract);
        return submitDelegation(coinContract);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handle(CoinDelegation body) {
        CoinSymbols symbol = coinSymbolsMapper.selectBySymbol(body.getSymbol());
        LogicUtils.assertNotNull(symbol, ErrorResultCode.E000039);
        if ("1".equals(body.getOperateType())) {
            createContract(symbol, body);
        } else {
            CoinContract coinContract = coinContractMapper.selectById(body.getContractId());
            closeContract(symbol, coinContract, body.getPrice());
        }
        if (LogicUtils.isNotNull(body.getContractId())) {
            LogicUtils.assertTrue(coinDelegationMapper.deleteByContractLimit(body.getContractId()) > 0,
                                  ErrorResultCode.E000001);
        }
        if (LogicUtils.isNotNull(body.getContractDelegationId())) {
            LogicUtils.assertTrue(coinDelegationMapper.deleteByContractDelegation(body.getContractDelegationId()) > 0,
                                  ErrorResultCode.E000001);
        }


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancel(Long id) {
        CoinContractDelegation coinContractDelegation = baseMapper.selectById(id);
        LogicUtils.assertNotNull(coinContractDelegation, ErrorResultCode.E000049);
        LogicUtils.assertEquals(coinContractDelegation.getStatus(), "1", ErrorResultCode.E000051);

        CoinSymbols symbol = coinSymbolsMapper.selectBySymbol(coinContractDelegation.getSymbol());
        LogicUtils.assertNotNull(symbol, ErrorResultCode.E000039);
        coinContractDelegation.setStatus("2");
        baseMapper.updateById(coinContractDelegation);

        Member member = SessionInfo.getMember();
        FundsOperateVo fundsOperateVo = new FundsOperateVo();
        fundsOperateVo.setSource(FundsSourceEnum.U_COIN);
        fundsOperateVo.setOperateType(FundsOperateTypeEnum.ROLLBACK);
        fundsOperateVo.setMemberId(member.getId());
        fundsOperateVo.setMarketId(marketMapper.selectByUsdtCoinMarketId());
        fundsOperateVo.setAmt(coinContractDelegation.getMarginAmt());
        fundsOperateVo.setSourceId(coinContractDelegation.getId());
        fundsOperateVo.setName(symbol.getBcdn());
        fundsOperateVo.setCode(symbol.getSymbol());
        fundsOperateVo.set(FundsOperateVo.FundsInfoKey.COIN_NAME, symbol.getBcdn())
                .set(FundsOperateVo.FundsInfoKey.ROLLBACK_AMT, coinContractDelegation.getMarginAmt())
                .build();
        memberFundsService.subFreezeAmt(fundsOperateVo);

        experienceService.rollback(FundsSourceEnum.U_COIN, coinContractDelegation.getId());

        coinDelegationMapper.deleteByContractDelegation(coinContractDelegation.getId());

        return true;
    }

    @Override
    public CalculateContractVo calculate(CalculateContractParamVo body) {
        CoinSymbols symbol = coinSymbolsMapper.selectBySymbol(body.getSymbol());
        LogicUtils.assertNotNull(symbol, ErrorResultCode.E000039);
        return calculate(symbol, body);
    }

    @PostConstruct
    public void initDelivery() {
        if (Boolean.FALSE.toString().equals(SpringUtil.getProperty("coin.enable"))){
            return;
        }
        new Thread(this::monitorDeliveryByDb).start();
        new Thread(this::delayedDeliveryTask).start();
    }

    private static final DelayQueue<CoinDelayed> DELAY_QUEUE = new DelayQueue<>();

    @Override
    public void monitorDeliveryByDb() {
        while (true) {
            try {
                List<CoinDelegation> coinDelegations = coinDelegationMapper.selectDelivery(System.currentTimeMillis());
                for (CoinDelegation coinDelegation : coinDelegations) {
                    ExecuteUtil.COIN_TRADE.execute(() -> {
                        SpringUtil.getBean(CoinContractDelegationService.class).closeDelivery(coinDelegation);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeDelivery(CoinDelegation coinDelegation) {
        CoinSymbols symbol = coinSymbolsMapper.selectBySymbol(coinDelegation.getSymbol());
        LogicUtils.assertNotNull(symbol, ErrorResultCode.E000039);
        CoinContract coinContract = coinContractMapper.selectById(coinDelegation.getContractId());
        closeContract(symbol, coinContract, symbol.getPrice());
        LogicUtils.assertTrue(coinDelegationMapper.deleteByContractLimit(coinContract.getId()) > 0,
                              ErrorResultCode.E000001);
    }

    @Override
    public void delayedDeliveryTask() {
        while (true) {
            try {
                CoinDelayed take = null;
                while ((take = DELAY_QUEUE.take()) != null) {
                    CoinDelegation coinDelegation = coinDelegationMapper.selectById(take.getDelegationId());
                    ExecuteUtil.COIN_TRADE.execute(() -> {
                        SpringUtil.getBean(CoinContractDelegationService.class).closeDelivery(coinDelegation);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static CalculateContractVo calculate(CoinSymbols symbol, CalculateContractParamVo body) {
        Long sheetNum = body.getSheetNum();
        sheetNum = LogicUtils.isNotNull(sheetNum) ? sheetNum : 0;
        BigDecimal level = BigDecimal.valueOf(body.getLevel());
        BigDecimal coinNum = BigDecimal.valueOf(sheetNum).multiply(symbol.getSheet());

        BigDecimal price = "2".equals(body.getPriceType()) ? symbol.getPrice() : body.getLimitPrice();

        BigDecimal totalAmt = coinNum.multiply(price);
        BigDecimal marginAmt = totalAmt.equals(BigDecimal.ZERO) ?
                BigDecimal.ZERO :
                totalAmt.divide(level, 30, RoundingMode.HALF_UP);
        BigDecimal feeAmt = totalAmt.multiply(symbol.getFeeAmt());

        BigDecimal sellTotalAmt = totalAmt.subtract(marginAmt);
        BigDecimal forcedSellAmt = sellTotalAmt.divide(coinNum, 30, RoundingMode.HALF_UP);

        CalculateContractVo calculateContractVo = new CalculateContractVo();
        calculateContractVo.setSymbol(body.getSymbol());
        calculateContractVo.setTotalAmt(totalAmt);
        calculateContractVo.setMarginAmt(marginAmt);
        calculateContractVo.setFeeAmt(feeAmt);
        calculateContractVo.setForcedSellAmt(forcedSellAmt);
        return calculateContractVo;
    }

    private void createContract(CoinSymbols symbol, CoinDelegation body) {
        CoinContractDelegation contractDelegation = baseMapper.selectById(body.getContractDelegationId());
        Member member = memberMapper.selectById(contractDelegation.getMemberId());

        Long marketId = marketMapper.selectByUsdtCoinMarketId();

        FundsOperateVo fundsOperateVo = new FundsOperateVo();
        fundsOperateVo.setSource(FundsSourceEnum.U_COIN);
        fundsOperateVo.setOperateType(FundsOperateTypeEnum.UNFREEZE);
        fundsOperateVo.setMemberId(member.getId());
        fundsOperateVo.setMarketId(marketId);
        fundsOperateVo.setAmt(contractDelegation.getMarginAmt());
        fundsOperateVo.setNegative(false);
        fundsOperateVo.setSourceId(contractDelegation.getId());
        fundsOperateVo.setName(symbol.getBcdn());
        fundsOperateVo.setCode(symbol.getSymbol());
        fundsOperateVo.setVisible(YNEnum.NO.getType());
        memberFundsService.subFreezeAmt(fundsOperateVo);

        try {
            createContract(symbol, member, contractDelegation, body.getTriggerPrice());
        } catch (Exception e) {
            log.error("创建合约失败: 合约委托ID: {}, 异常: ", contractDelegation.getId(), e);
            contractDelegation.setStatus("2");
            baseMapper.updateById(contractDelegation);
            coinDelegationMapper.deleteByContractDelegation(contractDelegation.getId());
        }
    }

    private Boolean createContract(CoinSymbols symbol, Member member, CoinContractDelegation delegation,
            BigDecimal buyAmt) {
        CoinContract coinContract = new CoinContract();
        coinContract.setMemberId(delegation.getMemberId());
        coinContract.setContractDelegationId(delegation.getId());
        coinContract.setSymbol(delegation.getSymbol());
        coinContract.setBcdn(symbol.getBcdn());
        coinContract.setQcdn(symbol.getQcdn());
        coinContract.setBuyAmt(buyAmt);
        coinContract.setBuyTime(new Date());

        coinContract.setCoinNum(BigDecimal.valueOf(delegation.getSheetNum()).multiply(symbol.getSheet()));
        coinContract.setSheetNum(delegation.getSheetNum());
        coinContract.setLevel(delegation.getLevel());
        coinContract.setDirection(delegation.getDirection());

        CalculateContractParamVo contractParamVo = new CalculateContractParamVo();
        contractParamVo.setPriceType("1");
        contractParamVo.setLimitPrice(buyAmt);
        contractParamVo.setSheetNum(delegation.getSheetNum());
        contractParamVo.setLevel(delegation.getLevel());
        CalculateContractVo calculate = calculate(symbol, contractParamVo);

        coinContract.setTotalAmt(calculate.getTotalAmt());
        coinContract.setMarginAmt(calculate.getMarginAmt());
        coinContract.setFeeAmt(calculate.getFeeAmt());
        coinContract.setProfitAmt(calculate.getFeeAmt().negate());
        coinContract.setStopProfitAmt(delegation.getStopProfitAmt());
        coinContract.setStopLossAmt(delegation.getStopLossAmt());


        coinContract.setForcedSellAmt(calculate.getForcedSellAmt());

        if (LogicUtils.isNotNull(delegation.getDeliveryId())) {
            CoinDelivery coinDelivery = coinDeliveryMapper.selectById(delegation.getDeliveryId());
            coinContract.setDeliveryId(coinDelivery.getId());
            coinContract.setDeliveryTime(DeliveryTimeUnitEnum.getDate(coinDelivery.getUnit(), coinDelivery.getTime()));
        }

        coinContract.setStatus("1");

        LogicUtils.assertEquals(coinContractMapper.insert(coinContract), 1, ErrorResultCode.E000045);

        Long marketId = marketMapper.selectByUsdtCoinMarketId();
        FundsOperateVo fundsOperateVo = new FundsOperateVo();
        fundsOperateVo.setSource(FundsSourceEnum.U_COIN);
        fundsOperateVo.setOperateType(FundsOperateTypeEnum.BUY);
        fundsOperateVo.setMemberId(member.getId());
        fundsOperateVo.setMarketId(marketId);
        fundsOperateVo.setAmt(coinContract.getMarginAmt());
        fundsOperateVo.setNegative(false);
        fundsOperateVo.setSourceId(delegation.getId());
        fundsOperateVo.setName(symbol.getBcdn());
        fundsOperateVo.setCode(symbol.getSymbol());
        setFundsInfo(fundsOperateVo, symbol, delegation, coinContract);
        memberFundsService.addOccupancyAmt(fundsOperateVo);

        delegation.setStatus("3");
        if ("2".equals(delegation.getPriceType())) {
            delegation.setLimitPrice(coinContract.getBuyAmt());
        }
        delegation.setStatus("3");
        baseMapper.updateById(delegation);

        CoinAssets coinAssets = coinAssetsService.get(marketId, member.getId(), coinContract.getBcdn());
        coinAssetsService.plus(coinAssets.getId(), coinContract.getCoinNum());
        return submitDelegation(coinContract);
    }

    private static void setFundsInfo(FundsOperateVo fundsOperateVo, CoinSymbols symbol,
            CoinContractDelegation delegation, CoinContract coinContract) {

        fundsOperateVo.set(FundsOperateVo.FundsInfoKey.COIN_NAME, symbol.getBcdn());
        fundsOperateVo.set(FundsOperateVo.FundsInfoKey.OCCUPANCY_AMT, coinContract.getMarginAmt());
        if (delegation.getMarginAmt().compareTo(coinContract.getMarginAmt()) < 0) {
            fundsOperateVo.set(FundsOperateVo.FundsInfoKey.BACK_PAYMENT_AMT,
                               coinContract.getMarginAmt().subtract(delegation.getMarginAmt()));
        }
        if (delegation.getMarginAmt().compareTo(coinContract.getMarginAmt()) > 0) {
            fundsOperateVo.set(FundsOperateVo.FundsInfoKey.ROLLBACK_AMT,
                               delegation.getMarginAmt().subtract(coinContract.getMarginAmt()));
        }
        fundsOperateVo.build();
    }

    private Boolean submitDelegation(CoinContract coinContract) {
        coinDelegationMapper.deleteByContractLimit(coinContract.getId());

        boolean more = "1".equals(coinContract.getDirection().toString());

        BigDecimal stopProfitAmt = coinContract.getStopProfitAmt();
        if (LogicUtils.isNotNull(stopProfitAmt)) {
            CoinDelegation coinDelegation = new CoinDelegation();
            coinDelegation.setContractId(coinContract.getId());
            coinDelegation.setSymbol(coinContract.getSymbol());
            coinDelegation.setPrice(stopProfitAmt);
            coinDelegation.setTriggerModel(more ? "1" : "2");
            coinDelegation.setOperateType("2");
            LogicUtils.assertEquals(coinDelegationMapper.insert(coinDelegation), 1, ErrorResultCode.E000044);
        }


        BigDecimal stopLossAmt = coinContract.getStopLossAmt();
        BigDecimal forcedSellAmt = coinContract.getForcedSellAmt();
        if (LogicUtils.isNull(stopLossAmt)) {
            stopLossAmt = forcedSellAmt;
        } else {
            if (more) {
                stopLossAmt = stopLossAmt.compareTo(forcedSellAmt) > 0 ? stopLossAmt : forcedSellAmt;
            } else {
                stopLossAmt = stopLossAmt.compareTo(forcedSellAmt) < 0 ? stopLossAmt : forcedSellAmt;
            }
        }

        if (stopLossAmt.compareTo(BigDecimal.ZERO) >= 0) {
            CoinDelegation coinDelegation = new CoinDelegation();
            coinDelegation.setContractId(coinContract.getId());
            coinDelegation.setSymbol(coinContract.getSymbol());
            coinDelegation.setPrice(stopLossAmt);
            coinDelegation.setTriggerModel(more ? "2" : "1");
            coinDelegation.setOperateType("2");
            LogicUtils.assertEquals(coinDelegationMapper.insert(coinDelegation), 1, ErrorResultCode.E000044);
        }


        if (LogicUtils.isNotNull(coinContract.getDeliveryTime())) {
            CoinDelegation coinDelegation = new CoinDelegation();
            coinDelegation.setContractId(coinContract.getId());
            coinDelegation.setSymbol(coinContract.getSymbol());
            coinDelegation.setDeliveryTime(coinContract.getDeliveryTime().getTime());
            coinDelegation.setTriggerModel("3");
            coinDelegation.setOperateType("2");
            LogicUtils.assertEquals(coinDelegationMapper.insert(coinDelegation), 1, ErrorResultCode.E000044);
            DELAY_QUEUE.add(new CoinDelayed(coinDelegation.getId(), coinDelegation.getDeliveryTime()));
        }
        return true;
    }

    private Boolean submitDelegation(CoinSymbols symbol, Member member, CoinContractDelegation delegation,
            String limitPriceDirection) {
        CoinDelegation coinDelegation = new CoinDelegation();
        coinDelegation.setContractDelegationId(delegation.getId());
        coinDelegation.setSymbol(delegation.getSymbol());
        coinDelegation.setPrice(delegation.getLimitPrice());
        coinDelegation.setTriggerModel(limitPriceDirection);
        coinDelegation.setOperateType("1");
        LogicUtils.assertEquals(coinDelegationMapper.insert(coinDelegation), 1, ErrorResultCode.E000044);

        Long marketId = marketMapper.selectByUsdtCoinMarketId();

        FundsOperateVo fundsOperateVo = new FundsOperateVo();
        fundsOperateVo.setSource(FundsSourceEnum.U_COIN);
        fundsOperateVo.setOperateType(FundsOperateTypeEnum.FREEZE);
        fundsOperateVo.setMemberId(member.getId());
        fundsOperateVo.setMarketId(marketId);
        fundsOperateVo.setAmt(delegation.getMarginAmt());
        fundsOperateVo.setNegative(false);
        fundsOperateVo.setSourceId(delegation.getId());
        fundsOperateVo.setName(symbol.getBcdn());
        fundsOperateVo.setCode(symbol.getSymbol());
        fundsOperateVo.set(FundsOperateVo.FundsInfoKey.COIN_NAME, symbol.getBcdn())
                .set(FundsOperateVo.FundsInfoKey.FREEZE_AMT, delegation.getMarginAmt())
                .build();
        memberFundsService.addFreezeAmt(fundsOperateVo);
        return true;
    }

    private CoinContractDelegation buildDelegation(BuyCoinContractVo body, CoinSymbols symbol, Member member) {

        LogicUtils.assertTrue("2".equals(body.getPriceType()) || LogicUtils.isNotNull(body.getLimitPrice()),
                              ErrorResultCode.E000046);

        Long sheetNum = body.getSheetNum();
        BigDecimal level = BigDecimal.valueOf(body.getLevel());
        BigDecimal coinNum = BigDecimal.valueOf(sheetNum).multiply(symbol.getSheet());

        BigDecimal price = "2".equals(body.getPriceType()) ? symbol.getPrice() : body.getLimitPrice();

        CalculateContractParamVo contractParamVo = new CalculateContractParamVo();
        contractParamVo.setPriceType(body.getPriceType());
        contractParamVo.setLimitPrice(body.getLimitPrice());
        contractParamVo.setSheetNum(body.getSheetNum());
        contractParamVo.setLevel(body.getLevel());
        CalculateContractVo calculate = calculate(symbol, contractParamVo);

        Long direction = body.getDirection();

        BigDecimal stopProfitAmt = body.getStopProfitAmt();
        checkStopProfitAmt(direction, stopProfitAmt, price);

        BigDecimal stopLossAmt = body.getStopLossAmt();
        checkStopLossAmt(direction, stopLossAmt, price);

        CoinContractDelegation coinContractDelegation = new CoinContractDelegation();
        coinContractDelegation.setMemberId(member.getId());
        coinContractDelegation.setSymbol(body.getSymbol());
        coinContractDelegation.setPriceType(body.getPriceType());
        coinContractDelegation.setDirection(direction);
        coinContractDelegation.setLimitPrice(body.getLimitPrice());
        coinContractDelegation.setDeliveryType(body.getDeliveryType());
        coinContractDelegation.setDeliveryId(body.getDeliveryId());
        coinContractDelegation.setCoinNum(coinNum);
        coinContractDelegation.setSheetNum(sheetNum);
        coinContractDelegation.setLevel(level.longValue());
        coinContractDelegation.setTotalAmt(calculate.getTotalAmt());
        coinContractDelegation.setMarginAmt(calculate.getMarginAmt());
        coinContractDelegation.setFeeAmt(calculate.getFeeAmt());

        coinContractDelegation.setStopProfitAmt(stopProfitAmt);
        coinContractDelegation.setStopLossAmt(stopLossAmt);
        coinContractDelegation.setStatus("1");
        return coinContractDelegation;
    }

    private static void checkStopLossAmt(Long direction, BigDecimal stopLossAmt, BigDecimal price) {
        if (LogicUtils.isNotNull(stopLossAmt)) {
            // 开多
            if ("1".equals(direction.toString())) {
                LogicUtils.assertTrue(stopLossAmt.compareTo(price) < 0, ErrorResultCode.E000041);
            } else {
                LogicUtils.assertTrue(stopLossAmt.compareTo(price) > 0, ErrorResultCode.E000043);
            }
        }
    }

    private static void checkStopProfitAmt(Long direction, BigDecimal stopProfitAmt, BigDecimal price) {
        if (LogicUtils.isNotNull(stopProfitAmt)) {
            // 开多
            if ("1".equals(direction.toString())) {
                LogicUtils.assertTrue(stopProfitAmt.compareTo(price) > 0, ErrorResultCode.E000040);
            } else {
                LogicUtils.assertTrue(stopProfitAmt.compareTo(price) < 0, ErrorResultCode.E000042);
            }
        }
    }


    private void closeContract(CoinSymbols symbol, CoinContract coinContract, BigDecimal last) {
        Member member = memberMapper.selectById(coinContract.getMemberId());
        LogicUtils.assertEquals(coinContract.getStatus(), "1", ErrorResultCode.E000050);

        BigDecimal profitAmt = profitCalculation(coinContract, last);
        coinContract.setProfitAmt(profitAmt);
        coinContract.setStatus("2");

        coinContract.setSellAmt(last);
        coinContract.setSellTime(new Date());
        coinContractMapper.updateById(coinContract);


        // 保证金亏完了
        if (profitAmt.compareTo(BigDecimal.ZERO) <= 0 && coinContract.getMarginAmt().subtract(profitAmt.negate())
                .compareTo(BigDecimal.ZERO) <= 0) {
            profitAmt = coinContract.getMarginAmt();
        }

        Long marketId = marketMapper.selectByUsdtCoinMarketId();
        FundsOperateVo fundsOperateVo = new FundsOperateVo();
        fundsOperateVo.setSource(FundsSourceEnum.U_COIN);
        fundsOperateVo.setOperateType(FundsOperateTypeEnum.SELL);
        fundsOperateVo.setMemberId(member.getId());
        fundsOperateVo.setMarketId(marketId);
        fundsOperateVo.setAmt(coinContract.getMarginAmt());
        fundsOperateVo.setNegative(false);
        fundsOperateVo.setSourceId(coinContract.getId());
        fundsOperateVo.setName(symbol.getBcdn());
        fundsOperateVo.setCode(symbol.getSymbol());
        fundsOperateVo.set(FundsOperateVo.FundsInfoKey.COIN_NAME, symbol.getBcdn())
                .set(FundsOperateVo.FundsInfoKey.OCCUPANCY_AMT, coinContract.getMarginAmt())
                .set(FundsOperateVo.FundsInfoKey.PROFIT_LOSS_AMT, profitAmt)
                .build();
        memberFundsService.subOccupancyAmt(fundsOperateVo);


        fundsOperateVo = new FundsOperateVo();
        fundsOperateVo.setSource(FundsSourceEnum.U_COIN);
        fundsOperateVo.setOperateType(FundsOperateTypeEnum.PROFIT_LOSS);
        fundsOperateVo.setMemberId(member.getId());
        fundsOperateVo.setMarketId(marketId);
        fundsOperateVo.setAmt(profitAmt);
        fundsOperateVo.setNegative(false);
        fundsOperateVo.setSourceId(coinContract.getId());
        fundsOperateVo.setName(symbol.getBcdn());
        fundsOperateVo.setCode(symbol.getSymbol());
        fundsOperateVo.setVisible(YNEnum.NO.getType());
        memberFundsService.addProfitAmt(fundsOperateVo);

        CoinAssets coinAssets = coinAssetsService.get(marketId, member.getId(), coinContract.getBcdn());
        coinAssetsService.reduce(coinAssets.getId(), coinContract.getCoinNum());
    }

    public BigDecimal profitCalculation(CoinContract coinContract, BigDecimal last) {
        BigDecimal coinNum = coinContract.getCoinNum();
        BigDecimal buyTotal = coinContract.getTotalAmt();
        BigDecimal sellTotal = coinNum.multiply(last);

        BigDecimal profitAmt = "1".equals(coinContract.getDirection().toString()) ?
                sellTotal.subtract(buyTotal) : buyTotal.subtract(sellTotal);
        profitAmt = profitAmt.subtract(coinContract.getFeeAmt());
        return profitAmt;
    }
}
