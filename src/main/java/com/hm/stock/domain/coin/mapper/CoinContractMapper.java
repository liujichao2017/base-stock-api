package com.hm.stock.domain.coin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hm.stock.domain.coin.entity.CoinContract;

/**
* 虚拟币合约 Mapper
*/
public interface CoinContractMapper extends BaseMapper<CoinContract> {

    int updateStopAmt(CoinContract coinContract);
}
