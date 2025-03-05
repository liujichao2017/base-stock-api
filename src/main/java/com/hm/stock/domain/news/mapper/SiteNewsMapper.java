package com.hm.stock.domain.news.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hm.stock.domain.news.entity.SiteNews;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
* 新闻资讯 Mapper
*/
public interface SiteNewsMapper extends BaseMapper<SiteNews> {

    @Select("select count(1) from site_news where source_id = #{sourceId}")
    int exists(@Param("sourceId") String sourceId);
}
