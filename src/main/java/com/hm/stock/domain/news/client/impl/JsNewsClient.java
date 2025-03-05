package com.hm.stock.domain.news.client.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hm.stock.domain.common.service.CommonService;
import com.hm.stock.domain.common.vo.UploadFilesVo;
import com.hm.stock.domain.news.client.NewsClient;
import com.hm.stock.domain.news.client.vo.JsNewsParam;
import com.hm.stock.domain.news.entity.SiteNews;
import com.hm.stock.domain.news.enums.NewsTypeEnum;
import com.hm.stock.domain.news.mapper.SiteNewsMapper;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.utils.HttpClient;
import com.hm.stock.modules.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

@Service
@Slf4j
public class JsNewsClient implements NewsClient {


    private final ThreadLocal<JsNewsParam> threadLocal = new ThreadLocal<>();
    @Autowired
    private SiteNewsMapper siteNewsMapper;
    @Autowired
    private CommonService commonService;


    @Override
    public void init(String param) {
        threadLocal.set(JsonUtil.toObj(param, JsNewsParam.class));
    }

    @Override
    public void sync() {
        JsNewsParam jsNewsParam = threadLocal.get();
        //新闻类型：1、财经要闻，2、经济数据，3、全球股市，4、7*24全球，5、商品资讯，6、上市公司，7、全球央行
        sync(jsNewsParam, 1L);
        sync(jsNewsParam, 2L);
        sync(jsNewsParam, 3L);
        sync(jsNewsParam, 4L);
        sync(jsNewsParam, 5L);
        sync(jsNewsParam, 6L);
        sync(jsNewsParam, 7L);
    }


    private void sync(JsNewsParam param, Long type) {
        String url = param.getDomain() + String.format("/stock-markets?type=%d&key=%s&countryId=%s", type, param.getKey(),param.getCountryId());
        String body = HttpClient.sendGet(url);
        JSONArray array = JSON.parseArray(body);
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            try {

                String sourceId = obj.getString("id");
                //新闻不存在则添加
                if (siteNewsMapper.exists(sourceId) != 0) {
                    continue;
                }

                Long time = obj.getLong("time");
                if (LogicUtils.isNull(time)) {
                    time = System.currentTimeMillis();
                } else {
                    time = time * 1000;
                }

                String imgBase64 = obj.getString("img");
                UploadFilesVo upload = commonService.upload(imgBase64);


                SiteNews siteNews = new SiteNews();
                siteNews.setType(type);
                siteNews.setTitle(obj.getString("title"));
                siteNews.setSourceId(sourceId);
                siteNews.setSource(NewsTypeEnum.JS.getType());
                siteNews.setViews(new Random().nextInt(100) + 1L);
                siteNews.setShowTime(new Date(time));
                siteNews.setImgurl(upload.getImg());
                siteNews.setContent(obj.getString("content"));
                siteNews.setStatus("1");
                siteNewsMapper.insert(siteNews);
            } catch (Exception e) {
                log.error("新闻保存错误: {}", obj.toJSONString(), e);
            }
        }
    }
}
