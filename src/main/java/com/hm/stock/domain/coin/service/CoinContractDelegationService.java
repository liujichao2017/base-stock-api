package com.hm.stock.domain.coin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.coin.entity.CoinContract;
import com.hm.stock.domain.coin.entity.CoinContractDelegation;
import com.hm.stock.domain.coin.entity.CoinDelegation;
import com.hm.stock.domain.coin.vo.BuyCoinContractVo;
import com.hm.stock.domain.coin.vo.CalculateContractParamVo;
import com.hm.stock.domain.coin.vo.CalculateContractVo;
import com.hm.stock.domain.coin.vo.UpdateContractVo;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.math.BigDecimal;
import java.util.List;

public interface CoinContractDelegationService extends IService<CoinContractDelegation> {
    List<CoinContractDelegation> selectList(CoinContractDelegation query);

    PageDate<CoinContractDelegation> selectByPage(CoinContractDelegation query, PageParam page);

    CoinContractDelegation detail(Long id);

    Long add(CoinContractDelegation body);

    boolean delete(Long id);

    boolean update(CoinContractDelegation body);

    Boolean buy(BuyCoinContractVo body);

    Boolean sell(Long id);

    Boolean cancel(Long id);

    CalculateContractVo calculate(CalculateContractParamVo body);

    void handle(CoinDelegation coinDelegation);

    Boolean updateProfitLossLimit(UpdateContractVo body);


    void monitorDeliveryByDb();

    void delayedDeliveryTask();
    void closeDelivery(CoinDelegation coinDelegation);

    BigDecimal profitCalculation(CoinContract coinContract, BigDecimal last);
}
