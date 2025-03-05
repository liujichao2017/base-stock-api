package com.hm.stock.domain.aitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.aitrade.entity.AiTrade;
import com.hm.stock.domain.aitrade.entity.AiTradeRecord;
import com.hm.stock.domain.aitrade.mapper.AiTradeMapper;
import com.hm.stock.domain.aitrade.mapper.AiTradeRecordMapper;
import com.hm.stock.domain.aitrade.service.AiTradeService;
import com.hm.stock.domain.aitrade.vo.AiTradeBuyVo;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.market.mapper.MarketMapper;
import com.hm.stock.domain.member.entity.Member;
import com.hm.stock.domain.member.enums.FundsOperateTypeEnum;
import com.hm.stock.domain.member.enums.FundsSourceEnum;
import com.hm.stock.domain.member.service.MemberFundsService;
import com.hm.stock.domain.member.vo.FundsOperateVo;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.execptions.ErrorResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AiTradeServiceImpl extends ServiceImpl<AiTradeMapper, AiTrade> implements AiTradeService {

    @Autowired
    private MemberFundsService memberFundsService;
    @Autowired
    private MarketMapper marketMapper;
    @Autowired
    private AiTradeRecordMapper aiTradeRecordMapper;


    @Override
    public List<AiTrade> selectList(AiTrade query) {
        QueryWrapper<AiTrade> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<AiTrade> selectByPage(AiTrade query, PageParam page) {
        QueryWrapper<AiTrade> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<AiTrade> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public AiTrade detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(AiTrade body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(AiTrade body) {
        return updateById(body);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean buy(AiTradeBuyVo body) {
        AiTrade aiTrade = detail(body.getId());
        LogicUtils.assertNotNull(aiTrade, ErrorResultCode.E000028);

        Market market = marketMapper.selectById(aiTrade.getMarketId());
        LogicUtils.assertNotNull(aiTrade, ErrorResultCode.E000001);

        Member member = SessionInfo.getMember();

        AiTradeRecord aiTradeRecord = new AiTradeRecord();
        aiTradeRecord.setMarketId(market.getId());
        aiTradeRecord.setAiTradeId(aiTrade.getId());
        aiTradeRecord.setMemberId(member.getId());
        aiTradeRecord.setName(aiTrade.getName());
        aiTradeRecord.setTradeSucRate(aiTrade.getTradeSucRate());
        aiTradeRecord.setFutureEarningsRate(aiTrade.getFutureEarningsRate());
        aiTradeRecord.setTradeCycle(aiTrade.getTradeCycle());
        aiTradeRecord.setBuyAmt(body.getAmt());
        aiTradeRecord.setIncomeAmt(new BigDecimal("0"));
        aiTradeRecord.setStatus("1");
        LogicUtils.assertTrue(aiTradeRecordMapper.insert(aiTradeRecord) == 1, ErrorResultCode.E000001);

        FundsOperateVo fundsOperateVo = new FundsOperateVo();
        fundsOperateVo.setSource(FundsSourceEnum.AI);
        fundsOperateVo.setOperateType(FundsOperateTypeEnum.FREEZE);
        fundsOperateVo.setMarket(market);
        fundsOperateVo.setMember(member);

        fundsOperateVo.setAmt(body.getAmt());
        fundsOperateVo.setNegative(false);
        fundsOperateVo.setSourceId(aiTradeRecord.getId());
        fundsOperateVo.setName(aiTradeRecord.getName());
        fundsOperateVo.set(FundsOperateVo.FundsInfoKey.PRODUCT_NAME, aiTradeRecord.getName())
                .set(FundsOperateVo.FundsInfoKey.FREEZE_AMT, aiTradeRecord.getBuyAmt())
                .build();
        memberFundsService.addFreezeAmt(fundsOperateVo);
        return null;
    }
}
