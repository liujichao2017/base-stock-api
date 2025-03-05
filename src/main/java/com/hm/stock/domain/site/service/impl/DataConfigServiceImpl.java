package com.hm.stock.domain.site.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.site.entity.DataConfig;
import com.hm.stock.domain.site.mapper.DataConfigMapper;
import com.hm.stock.domain.site.service.DataConfigService;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.common.Pair;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataConfigServiceImpl extends ServiceImpl<DataConfigMapper, DataConfig> implements DataConfigService {

    @Override
    public List<DataConfig> selectList(DataConfig query) {
        QueryWrapper<DataConfig> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<DataConfig> selectByPage(DataConfig query, PageParam page) {
        QueryWrapper<DataConfig> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<DataConfig> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public DataConfig detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(DataConfig body) {
        boolean flag = save(body);
        return 0L;
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(DataConfig body) {
        return updateById(body);
    }

    @Override
    public String getByStr(DataConfig defaultValue) {
        QueryWrapper<DataConfig> ew = new QueryWrapper<>();
        ew.eq("`group`", defaultValue.getGroup());
        ew.eq("`key`", defaultValue.getKey());
        ew.last("limit 1");
        DataConfig dataConfig = baseMapper.selectOne(ew);
        if (LogicUtils.isNull(dataConfig)) {
            save(defaultValue);
            return defaultValue.getVal();
        }
        return dataConfig.getVal();
    }

    @Override
    public List<Pair<String, String>> getWithdrawalTime() {
        QueryWrapper<DataConfig> ew = new QueryWrapper<>();
        ew.eq("`group`", "withdrawal");
        List<DataConfig> dataConfigs = baseMapper.selectList(ew);
        if (LogicUtils.isEmpty(dataConfigs)) {
            DataConfig dataConfig = new DataConfig();
            dataConfig.setGroup("withdrawal");
            dataConfig.setType("str");
            dataConfig.setKey("withdrawal_am");
            dataConfig.setVal("09:00-12:30");
            dataConfig.setRemark("提现时间: 填写格式: mm:ss-mm:ss");
            save(dataConfig);
            return Collections.singletonList(Pair.of("09:00", "12:30"));
        }
        return dataConfigs.stream().map(DataConfig::getVal).filter(s->s.contains("-")).map(v -> v.split("-")).map(v -> Pair.of(v[0], v[1]))
                .collect(Collectors.toList());
    }

    @Override
    public List<DataConfig> getGroup(String group) {
        QueryWrapper<DataConfig> ew = new QueryWrapper<>();
        ew.eq("`group`", group);
        return baseMapper.selectList(ew);
    }
}
