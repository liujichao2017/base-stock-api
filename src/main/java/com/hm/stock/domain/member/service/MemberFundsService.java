package com.hm.stock.domain.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.member.entity.Member;
import com.hm.stock.domain.member.entity.MemberFunds;
import com.hm.stock.domain.member.vo.FundsOperateVo;

import java.util.List;

public interface MemberFundsService extends IService<MemberFunds> {

    /**
     * 增加可用资金
     * @param fundsOperateVo 入参
     */
    void addEnableAmt(FundsOperateVo fundsOperateVo);

    /**
     * 减少可用资金
     * @param fundsOperateVo 入参
     */
    void subEnableAmt(FundsOperateVo fundsOperateVo);

    /**
     * 增减占用资金, 减少可用资金
     * @param fundsOperateVo 入参
     */
    void addOccupancyAmt(FundsOperateVo fundsOperateVo);

    /**
     * 减少占用资金, 增加可用资金
     * @param fundsOperateVo 入参
     */
    void subOccupancyAmt(FundsOperateVo fundsOperateVo);

    /**
     * 增减冻结资金, 减少可用资金
     * @param fundsOperateVo 入参
     */
    void addFreezeAmt(FundsOperateVo fundsOperateVo);

    /**
     * 减少冻结资金, 增加可用资金
     * @param fundsOperateVo 入参
     */
    void subFreezeAmt(FundsOperateVo fundsOperateVo);


    void freezeToOccupancyAmt(FundsOperateVo fundsOperateVo);
    /**
     * 收益到账, 增加可用资金
     * @param fundsOperateVo 入参
     */
    void addProfitAmt(FundsOperateVo fundsOperateVo);

    /**
     * 收益回退, 减少可用资金
     * @param fundsOperateVo 入参
     */
    void subProfitAmt(FundsOperateVo fundsOperateVo);

    MemberFunds getFundsRecord(Market market, Member member);

    /**
     * 查询用户
     * @param member
     * @return
     */
    List<MemberFunds> getFundsRecords(Member member);
}
