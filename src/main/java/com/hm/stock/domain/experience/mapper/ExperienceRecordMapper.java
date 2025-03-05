package com.hm.stock.domain.experience.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hm.stock.domain.experience.entity.ExperienceRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * 体验金使用记录表 Mapper
 */
public interface ExperienceRecordMapper extends BaseMapper<ExperienceRecord> {

    @Select("select sum(amt) from experience_record where source = #{source} and source_id = #{sourceId}")
    BigDecimal queryUseAmt(@Param("source") String source, @Param("sourceId") Long sourceId);
}
