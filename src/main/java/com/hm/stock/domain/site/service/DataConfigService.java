package com.hm.stock.domain.site.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.site.entity.DataConfig;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.common.Pair;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DataConfigService extends IService<DataConfig> {
    List<DataConfig> selectList(DataConfig query);

    PageDate<DataConfig> selectByPage(DataConfig query, PageParam page);

    DataConfig detail(Long id);

    Long add(DataConfig body);

    boolean delete(Long id);

    boolean update(DataConfig body);

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    String getByStr(DataConfig defaultValue);

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    List<Pair<String,String>> getWithdrawalTime();

    List<DataConfig> getGroup(String group);
}
