package com.hm.stock.domain.coin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.coin.entity.CoinSpontaneousRobot;
import com.hm.stock.domain.coin.vo.GenSpontaneousRobotVo;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface CoinSpontaneousRobotService extends IService<CoinSpontaneousRobot> {
    List<CoinSpontaneousRobot> selectList(CoinSpontaneousRobot query);

    PageDate<CoinSpontaneousRobot> selectByPage(CoinSpontaneousRobot query, PageParam page);

    CoinSpontaneousRobot detail(Long id);

    Long add(CoinSpontaneousRobot body);

    boolean delete(Long id);

    boolean update(CoinSpontaneousRobot body);

    Boolean genSpontaneous(GenSpontaneousRobotVo coinKlineVo);


    void monitorByDb();

    void delayedTask();

    void amplitude(String code, Long ts);

}
