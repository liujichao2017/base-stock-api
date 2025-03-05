package com.hm.stock.domain.experience.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.experience.entity.Experience;
import com.hm.stock.domain.experience.entity.ExperienceRecord;
import com.hm.stock.domain.experience.mapper.ExperienceMapper;
import com.hm.stock.domain.experience.mapper.ExperienceRecordMapper;
import com.hm.stock.domain.experience.service.ExperienceService;
import com.hm.stock.domain.experience.vo.ExperienceVo;
import com.hm.stock.domain.member.enums.FundsSourceEnum;
import com.hm.stock.domain.site.entity.DataConfig;
import com.hm.stock.domain.site.service.DataConfigService;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.enums.YNEnum;
import com.hm.stock.modules.execptions.CommonResultCode;
import com.hm.stock.modules.utils.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExperienceServiceImpl extends ServiceImpl<ExperienceMapper, Experience> implements ExperienceService {
    @Autowired
    private ExperienceRecordMapper experienceRecordMapper;
    @Autowired
    private DataConfigService dataConfigService;


    @Override
    public List<Experience> selectList(Long marketId) {
        QueryWrapper<Experience> ew = new QueryWrapper<>();
        ew.eq("member_id", SessionInfo.getInstance().getId());
        ew.eq("market_id", marketId);
        ew.orderByAsc("create_time");
        List<Experience> list = list(ew);

        for (Experience experience : list) {
            if ("1".equals(experience.getLockStatus())) {
                experience.setStatus(2);
                continue;
            }
            if (experience.getAmt().compareTo(experience.getUseAmt()) == 0) {
                experience.setStatus(3);
                continue;
            }
            boolean flag = experience.getAmt().compareTo(experience.getUseAmt()) > 0;
            flag = flag && experience.getCount() > experience.getUseCount();
            flag = flag && experience.getDeadline().compareTo(new Date()) > 0;
            experience.setStatus(flag ? 1 : 4);
        }
        list.sort(Comparator.comparingInt(Experience::getStatus));
        return list;
    }

    @Override
    public PageDate<Experience> selectByPage(Experience query, PageParam page) {
        QueryWrapper<Experience> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<Experience> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public Experience detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(Experience body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(Experience body) {
        return updateById(body);
    }

    /**
     * @return Map<MarketId, EnableAmt>
     */
    @Override
    public Map<Long, BigDecimal> queryEnableAmt(Long memberId) {
        List<Experience> list = baseMapper.sumEnableAmt(memberId);
        if (LogicUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }


        return list.stream().collect(Collectors.toMap(Experience::getMarketId, Experience::getAmt));
    }

    @Override
    public Map<Long, ExperienceVo> queryExperience(Long memberId) {
        List<Experience> list = baseMapper.queryExperience(memberId);
        if (LogicUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }

        Map<Long, ExperienceVo> experienceMap = new HashMap<>();

        for (Experience experience : list) {
            ExperienceVo experienceVo = experienceMap.computeIfAbsent(experience.getMarketId(),
                                                                      key -> new ExperienceVo());
            BigDecimal amt = experience.getAmt().subtract(experience.getUseAmt());
            experienceVo.addTotalAmt(amt);
            if (YNEnum.no(experience.getLockStatus())) {
                experienceVo.addEnableAmt(amt);
                continue;
            }
            experienceVo.addLockAmt(amt);
            experienceVo.addInfo(amt, experience.getNeedRechargeAmt().subtract(experience.getRechargeAmt()));
        }
        return experienceMap;
    }

    @Override
    public BigDecimal use(Long marketId, Long memberId, FundsSourceEnum fundsSourceEnum, Long sourceId,
            BigDecimal needAmt) {
        BigDecimal useAmt = null;
        try {
            Map<Long, BigDecimal> map = queryEnableAmt(memberId);
            BigDecimal enableAmt = map.getOrDefault(marketId, BigDecimal.ZERO);
            if (enableAmt.compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO;
            }
            List<Experience> enableExperiences = baseMapper.getEnableExperience(memberId, marketId);
            useAmt = needAmt;
            if (enableAmt.compareTo(useAmt) < 0) {
                useAmt = enableAmt;
            }

            ExperienceRecord entity = new ExperienceRecord();
            entity.setSource(fundsSourceEnum.getType());
            entity.setSourceId(sourceId);
            entity.setMemberId(memberId);
            entity.setMarketId(marketId);
            entity.setAmt(useAmt);
            LogicUtils.assertEquals(experienceRecordMapper.insert(entity), 1, CommonResultCode.INTERNAL_ERROR);

            BigDecimal lave = useAmt;
            for (Experience enableExperience : enableExperiences) {
                lave = useExperience(enableExperience, lave);
                if (lave.compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }
            }
        } catch (Exception e) {
            log.error("使用体验金失败: ", e);
            return BigDecimal.ZERO;
        }
        return useAmt;
    }

    @Override
    public BigDecimal queryUseAmt(FundsSourceEnum fundsSourceEnum, Long sourceId) {
        BigDecimal useAmt = experienceRecordMapper.queryUseAmt(fundsSourceEnum.getType(), sourceId);
        return useAmt != null ? useAmt : BigDecimal.ZERO;
    }

    @Override
    public void rollback(FundsSourceEnum fundsSourceEnum, Long sourceId) {
        QueryWrapper<ExperienceRecord> ew = new QueryWrapper<>();
        ew.eq("source", fundsSourceEnum.getType());
        ew.eq("source_id", sourceId);
        List<ExperienceRecord> list = experienceRecordMapper.selectList(ew);
        if (LogicUtils.isEmpty(list)) {
            return;
        }
        ExperienceRecord experienceRecord = list.get(0);

        List<Long> recordId = new ArrayList<>();
        BigDecimal amt = BigDecimal.ZERO;
        for (ExperienceRecord record : list) {
            recordId.add(record.getId());
            amt = amt.add(record.getAmt());
        }

        LogicUtils.assertEquals(experienceRecordMapper.deleteBatchIds(recordId), recordId.size(),
                                CommonResultCode.INTERNAL_ERROR);

        DataConfig dataConfig = new DataConfig();
        dataConfig.setGroup("experience");
        dataConfig.setType("str");
        dataConfig.setKey("experience_rallback_day");
        dataConfig.setVal("3");
        dataConfig.setRemark("退回的体验金保留的天数");
        String day = dataConfigService.getByStr(dataConfig);

        Experience experience = new Experience();
        experience.setMemberId(experienceRecord.getMemberId());
        experience.setMarketId(experienceRecord.getMarketId());
        experience.setAmt(amt);
        experience.setUseAmt(new BigDecimal("0"));
        experience.setCount(1L);
        experience.setUseCount(0L);
        experience.setDeadline(DateTimeUtil.getDate(Integer.parseInt(day)));

        LogicUtils.assertTrue(save(experience), CommonResultCode.INTERNAL_ERROR);
    }

    private BigDecimal useExperience(Experience experience, BigDecimal needAmt) {
        BigDecimal enableAmt = experience.getAmt().subtract(experience.getUseAmt());
        BigDecimal useAmt = needAmt;
        BigDecimal lave = BigDecimal.ZERO;
        if (enableAmt.compareTo(useAmt) < 0) {
            useAmt = enableAmt;
            lave = needAmt.subtract(enableAmt);
        }
        LogicUtils.assertEquals(baseMapper.addUseAmt(experience.getId(), useAmt), 1,
                                CommonResultCode.INTERNAL_ERROR);
        return lave;
    }


}
