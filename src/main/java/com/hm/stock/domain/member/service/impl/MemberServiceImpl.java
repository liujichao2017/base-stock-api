package com.hm.stock.domain.member.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.auth.service.AuthService;
import com.hm.stock.domain.coin.entity.CoinContract;
import com.hm.stock.domain.coin.service.CoinContractService;
import com.hm.stock.domain.experience.service.ExperienceService;
import com.hm.stock.domain.experience.vo.ExperienceVo;
import com.hm.stock.domain.fund.service.MemberFundRecordService;
import com.hm.stock.domain.fund.vo.CountMemberFundVo;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.market.mapper.MarketMapper;
import com.hm.stock.domain.market.service.MarketService;
import com.hm.stock.domain.member.entity.Member;
import com.hm.stock.domain.member.entity.MemberFunds;
import com.hm.stock.domain.member.entity.MemberFundsConvert;
import com.hm.stock.domain.member.entity.MemberFundsLogs;
import com.hm.stock.domain.member.enums.FundsOperateTypeEnum;
import com.hm.stock.domain.member.enums.FundsSourceEnum;
import com.hm.stock.domain.member.mapper.MemberFundsConvertMapper;
import com.hm.stock.domain.member.mapper.MemberFundsLogsMapper;
import com.hm.stock.domain.member.mapper.MemberFundsMapper;
import com.hm.stock.domain.member.mapper.MemberMapper;
import com.hm.stock.domain.member.service.MemberFundsService;
import com.hm.stock.domain.member.service.MemberService;
import com.hm.stock.domain.member.vo.*;
import com.hm.stock.domain.message.service.MemberMessageService;
import com.hm.stock.domain.site.entity.SysUser;
import com.hm.stock.domain.site.mapper.SysUserMapper;
import com.hm.stock.domain.stock.entity.MemberPosition;
import com.hm.stock.domain.stock.mapper.MemberPositionMapper;
import com.hm.stock.domain.stock.service.MemberPositionService;
import com.hm.stock.domain.stock.vo.MemberPositionStatVo;
import com.hm.stock.domain.stock.vo.MemberPositionVo;
import com.hm.stock.domain.ws.entity.WsMember;
import com.hm.stock.domain.ws.handler.SocketSessionManage;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.enums.YNEnum;
import com.hm.stock.modules.execptions.CommonResultCode;
import com.hm.stock.modules.execptions.ErrorResultCode;
import com.hm.stock.modules.redis.RedisClient;
import com.hm.stock.modules.redis.RedisKey;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

    @Autowired
    private MemberFundsService memberFundsService;
    @Autowired
    private MarketService marketService;
    @Autowired
    private MemberFundsConvertMapper memberFundsConvertMapper;
    @Autowired
    private MemberFundsMapper memberFundsMapper;
    @Autowired
    private MarketMapper marketMapper;

    @Autowired
    private MemberPositionMapper memberPositionMapper;

    @Autowired
    private MemberFundsLogsMapper memberFundsLogsMapper;
    @Autowired
    private AuthService authService;
    @Autowired
    private MemberMessageService memberMessageService;
    @Autowired
    private SocketSessionManage socketSessionManage;
    @Autowired
    private RedisClient redisClient;

    @Autowired
    private MemberPositionService memberPositionService;
    @Autowired
    private MemberFundRecordService memberFundRecordService;
    @Autowired
    private CoinContractService coinContractService;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private ExperienceService experienceService;


    @Override
    public MemberVo detail() {
        SessionInfo instance = SessionInfo.getInstance();
        Member member = getById(instance.getId());
        MemberVo memberVo = new MemberVo();
        BeanUtils.copyProperties(member, memberVo);
        List<MemberFundsVo> funds = getFunds(member);
        // 计算浮动盈利
        sumFloatProfitAmt(funds);
        memberVo.setMemberFunds(funds);

        SysUser serviceUrl = getServiceUrl(memberVo.getUserId());
        if (LogicUtils.isNotNull(serviceUrl)) {
            memberVo.setOnlineService(serviceUrl.getOnlineService());
            memberVo.setFundService(serviceUrl.getFundService());
        }

        return memberVo;
    }


    @Override
    public PageDate<MemberFundsLogs> fundsList(MemberFundsLogs query, PageParam page) {
        SessionInfo instance = SessionInfo.getInstance();
        query.setMemberId(instance.getId());
        query.setVisible("1");
        QueryWrapper<MemberFundsLogs> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("id");
        PageDTO<MemberFundsLogs> result = memberFundsLogsMapper.selectPage(
                new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public boolean update(Member body) {
        SessionInfo instance = SessionInfo.getInstance();
        Member member = getById(instance.getId());
        LogicUtils.assertNotNull(member, ErrorResultCode.E000006);
        if (LogicUtils.isNotBlank(body.getSmsCode())) {
            String code = redisClient.get(RedisKey.smsCodeKey(member.getPhone()));
            LogicUtils.assertEquals(code, body.getSmsCode(), ErrorResultCode.E000037);
        }
        boolean flag = false;
        if (LogicUtils.isNotBlank(body.getAvatarImg())) {
            flag = true;
            member.setAvatarImg(body.getAvatarImg());
        }
        if (LogicUtils.isNotBlank(body.getPassword())) {
            flag = true;
            member.setPassword(body.getPassword());
        }
        if (LogicUtils.isNotBlank(body.getWithPwd())) {
            flag = true;
            member.setWithPwd(body.getWithPwd());
        }
        LogicUtils.assertTrue(flag, ErrorResultCode.E000001);
        return updateById(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean convertAmt(FundConvertVo fundConvertVo) {
        Member member = SessionInfo.getMember();
        LogicUtils.assertNotNull(member, ErrorResultCode.E000006);

        Market fromMarket = marketService.getById(fundConvertVo.getFromId());
        LogicUtils.assertNotNull(fromMarket, ErrorResultCode.E000003);

        Market toMarket = marketService.getById(fundConvertVo.getToId());
        LogicUtils.assertNotNull(toMarket, ErrorResultCode.E000003);

        boolean isFromMaster = YNEnum.yes(fromMarket.getIsMaster());
        boolean isToMaster = YNEnum.yes(toMarket.getIsMaster());
        LogicUtils.assertTrue(isFromMaster || isToMaster, ErrorResultCode.E000004);

        BigDecimal fromAmt = fundConvertVo.getAmt();
        BigDecimal exchangeRate = BigDecimal.ZERO;
        if (isFromMaster) {
            exchangeRate = toMarket.getMainExchangeRate();
        }
        if (isToMaster) {
            exchangeRate = fromMarket.getSlaveExchangeRate();
        }
        BigDecimal toAmt = fromAmt.multiply(exchangeRate);
        toAmt = toAmt.setScale(3, 4);
        FundsOperateVo fromVo = new FundsOperateVo();
        fromVo.setSource(FundsSourceEnum.CONVERT);
        fromVo.setOperateType(FundsOperateTypeEnum.OTHER);
        fromVo.setMember(member);
        fromVo.setAmt(fromAmt);
        fromVo.setMarket(fromMarket);

        FundsOperateVo toVo = new FundsOperateVo();
        toVo.setSource(FundsSourceEnum.CONVERT);
        toVo.setOperateType(FundsOperateTypeEnum.OTHER);
        toVo.setMember(member);
        toVo.setAmt(toAmt);
        toVo.setMarket(toMarket);

        memberFundsService.subEnableAmt(fromVo);
        memberFundsService.addEnableAmt(toVo);

        MemberFundsConvert memberFundsConvert = new MemberFundsConvert();
        memberFundsConvert.setMemberId(member.getId());
        memberFundsConvert.setFromId(fromMarket.getId());
        memberFundsConvert.setToId(toMarket.getId());
        memberFundsConvert.setFromAmt(fromAmt);
        memberFundsConvert.setToAmt(toAmt);
        memberFundsConvert.setExchangeRate(exchangeRate);
        LogicUtils.assertTrue(memberFundsConvertMapper.insert(memberFundsConvert) == 1,
                              CommonResultCode.INTERNAL_ERROR);
        return true;
    }

    @Override
    public Boolean realName(MemberRealVo memberRealVo) {
        MemberVo detail = detail();
        LogicUtils.assertTrue("0".equals(detail.getIsActive()) || "2".equals(detail.getIsActive()),
                              ErrorResultCode.E000027);

        Member member = new Member();
        member.setId(detail.getId());
        member.setRealName(memberRealVo.getRealName());
        member.setIdCard(memberRealVo.getIdCard());
        member.setImg1Key(memberRealVo.getImg1Key());
        member.setImg2Key(memberRealVo.getImg2Key());
        member.setImg3Key(memberRealVo.getImg3Key());
        member.setIsActive("3");
        LogicUtils.assertTrue(baseMapper.updateById(member) == 1,
                              CommonResultCode.INTERNAL_ERROR);
        return true;
    }

    @Override
    public Member getAndCheck(FundsSourceEnum fundsSourceEnum) {
        SessionInfo instance = SessionInfo.getInstance();
        Member member = getById(instance.getId());
        LogicUtils.assertNotNull(member, ErrorResultCode.E000006);

        // 实名
        LogicUtils.assertTrue("1".equals(member.getIsActive()), ErrorResultCode.E000023);

        // 交易权限 fundsSourceEnum
        return member;
    }

    @Override
    public void memberChange(Long memberId) {
        Member member = getById(memberId);
        LogicUtils.assertNotNull(member, ErrorResultCode.E000006);
        if (YNEnum.no(member.getIsLogin())) {
            authService.logout(member.getId());
        }
        WsMember wsMember = new WsMember();
        wsMember.setMember(member);
        wsMember.setUnreadMsg(memberMessageService.countUnread(member.getId()));
        socketSessionManage.send(memberId, wsMember);
    }

    @Override
    public MemberPositionStatVo getMemberPositionStat(Long memberId) {
        MemberPositionStatVo memberPositionStatVo = new MemberPositionStatVo();
        
        // Initialize all amounts to zero
        memberPositionStatVo.setQuantificationSum(BigDecimal.ZERO);
        memberPositionStatVo.setEnableSum(BigDecimal.ZERO);
        memberPositionStatVo.setOccupancySum(BigDecimal.ZERO);
        memberPositionStatVo.setProfitSum(BigDecimal.ZERO);
        memberPositionStatVo.setFloatingProfitSum(BigDecimal.ZERO);

        // Query member funds
        QueryWrapper<MemberFunds> fundsEw = new QueryWrapper<>();
        // 根据memberId查询，此处忽略了货币类型的查询,假定都是美元
        String currencyType = "us";
        fundsEw.eq("member_id", memberId);
        fundsEw.eq("currency_type",currencyType);
        List<MemberFunds> memberFunds = memberFundsMapper.selectList(fundsEw);
        
        // Calculate total funds and available funds
        for (MemberFunds funds : memberFunds) {
            if("QUANTIFICATION".equals(funds.getAccountType())){
                memberPositionStatVo.setQuantificationSum(memberPositionStatVo.getQuantificationSum()
                .add(funds.getEnableAmt())
                .add(funds.getOccupancyAmt())
                .add(funds.getFreezeAmt())
                );

                memberPositionStatVo.setEnableSum(memberPositionStatVo.getEnableSum().add(funds.getEnableAmt()));
                memberPositionStatVo.setOccupancySum(memberPositionStatVo.getOccupancySum().add(funds.getOccupancyAmt()));
                memberPositionStatVo.setProfitSum(memberPositionStatVo.getProfitSum().add(funds.getProfitAmt()));
            }
            
        }

        // 计算浮动利润 
        BigDecimal floatingProfit = memberPositionMapper.selectFloatingProfit(memberId);
        memberPositionStatVo.setFloatingProfitSum(floatingProfit);



        return memberPositionStatVo;
    }

    /**
     * 计算浮动利润
     *
     * @param memberFundsVos 客户
     * @return 持有的资产的 浮动利润
     */
    private BigDecimal sumFloatProfitAmt(List<MemberFundsVo> memberFundsVos) {
        Map<Long, MemberFundsVo> memberFundsMap = memberFundsVos.stream()
                .collect(Collectors.toMap(MemberFundsVo::getMarketId, m -> m));
        // 统计股票
        List<MemberPositionVo> list = memberPositionService.selectList("1");
        for (MemberPositionVo memberPositionVo : list) {
            MemberFundsVo memberFundsVo = memberFundsMap.get(memberPositionVo.getMarketId());
            memberFundsVo.setFloatProfitAmt(memberFundsVo.getFloatProfitAmt().add(memberPositionVo.getProfitAndLose()));
        }
        // 基金
        List<CountMemberFundVo> countMemberFundVos = memberFundRecordService.countFund();
        for (CountMemberFundVo countMemberFundVo : countMemberFundVos) {
            BigDecimal income = countMemberFundVo.getIncome();
            BigDecimal arrivalIncome = countMemberFundVo.getArrivalIncome();

            MemberFundsVo memberFundsVo = memberFundsMap.get(countMemberFundVo.getMarketId());
            if (memberFundsVo == null) {
                continue;
            }
            memberFundsVo.setFloatProfitAmt(memberFundsVo.getFloatProfitAmt().add(income.subtract(arrivalIncome)));
        }
        // 虚拟币
        CoinContract query = new CoinContract();
        query.setStatus("1");
        List<CoinContract> coinContracts = coinContractService.selectList(query);
        Long coinMarketId = marketMapper.selectByUsdtCoinMarketId();
        for (CoinContract coinContract : coinContracts) {
            MemberFundsVo memberFundsVo = memberFundsMap.get(coinMarketId);
            if (LogicUtils.isNotNull(memberFundsVo)) {
                memberFundsVo.setFloatProfitAmt(memberFundsVo.getFloatProfitAmt().add(coinContract.getProfitAmt()));
            }
        }

        return BigDecimal.ZERO;
    }

    private List<MemberFundsVo> getFunds(Member member) {
        QueryWrapper<Market> marketEw = new QueryWrapper<>();
        marketEw.eq("status", "1");
        List<Market> markets = marketMapper.selectList(marketEw);
        QueryWrapper<MemberFunds> ew = new QueryWrapper<>();
        ew.eq("member_id", member.getId());
        List<MemberFunds> memberFunds = memberFundsMapper.selectList(ew);
        Map<Long, List<MemberFunds>> memberFundsMap = memberFunds.stream()
                .collect(Collectors.groupingBy(MemberFunds::getMemberId));

        // 体验金
        Map<Long, ExperienceVo> experienceMap = experienceService.queryExperience(member.getId());

        MemberVo memberVo = new MemberVo();
        BeanUtils.copyProperties(member, memberVo);
        List<MemberFunds> memberFundsList = memberFundsMap.get(member.getId());
        List<MemberFundsVo> memberFundsVos = new ArrayList<>();
        for (Market market : markets) {
            boolean flag = true;
            if (LogicUtils.isNotEmpty(memberFundsList)) {
                for (MemberFunds funds : memberFundsList) {
                    if (LogicUtils.isEquals(funds.getMarketId(), market.getId())) {
                        flag = false;
                        MemberFundsVo memberFundsVo = buildMemberFundsVo(member, market, funds);
                        memberFundsVo.setExperience(experienceMap.get(market.getId()));
                        memberFundsVos.add(memberFundsVo);
                        break;
                    }
                }
            }
            if (flag) {
                memberFundsVos.add(buildMemberFundsVo(member, market));
            }
        }
        memberFundsVos.sort((m1, m2) -> Math.toIntExact(m1.getMemberId() - m2.getMemberId()));
        return memberFundsVos;
    }

    private static MemberFundsVo buildMemberFundsVo(Member record, Market market) {
        MemberFundsVo memberFundsVo = new MemberFundsVo();
        memberFundsVo.setMemberId(record.getId());
        memberFundsVo.setMarketId(market.getId());
        memberFundsVo.setCurrency(market.getCurrency());
        memberFundsVo.setEnableAmt(BigDecimal.ZERO);
        memberFundsVo.setOccupancyAmt(BigDecimal.ZERO);
        memberFundsVo.setFreezeAmt(BigDecimal.ZERO);
        memberFundsVo.setProfitAmt(BigDecimal.ZERO);
        memberFundsVo.setFloatProfitAmt(BigDecimal.ZERO);
        return memberFundsVo;
    }

    private MemberFundsVo buildMemberFundsVo(Member record, Market market, MemberFunds funds) {
        MemberFundsVo memberFundsVo = new MemberFundsVo();
        memberFundsVo.setMemberId(record.getId());
        memberFundsVo.setMarketId(market.getId());
        memberFundsVo.setCurrency(market.getName());
        memberFundsVo.setEnableAmt(funds.getEnableAmt());
        memberFundsVo.setOccupancyAmt(funds.getOccupancyAmt());
        memberFundsVo.setFreezeAmt(funds.getFreezeAmt());
        memberFundsVo.setProfitAmt(funds.getProfitAmt());
        memberFundsVo.setFloatProfitAmt(BigDecimal.ZERO);
        return memberFundsVo;
    }

    private SysUser getServiceUrl(Long userId) {
        try {
            SysUser sysUser = sysUserMapper.selectById(userId);
            if (LogicUtils.isNull(sysUser)) {
                return null;
            }
            if (LogicUtils.isBlank(sysUser.getOnlineService()) || JSONObject.parseArray(sysUser.getOnlineService())
                    .isEmpty()) {
                return getServiceUrl(sysUser.getParentId());
            }
            return sysUser;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
