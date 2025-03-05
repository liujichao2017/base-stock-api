package com.hm.stock.domain.news.client.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hm.stock.domain.common.service.CommonService;
import com.hm.stock.domain.common.vo.UploadFilesVo;
import com.hm.stock.domain.news.client.NewsClient;
import com.hm.stock.domain.news.client.vo.MyV1NewsParam;
import com.hm.stock.domain.news.entity.SiteNews;
import com.hm.stock.domain.news.enums.NewsTypeEnum;
import com.hm.stock.domain.news.mapper.SiteNewsMapper;
import com.hm.stock.modules.utils.HttpClient;
import com.hm.stock.modules.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class MyV1NewsClient implements NewsClient {

    private final ThreadLocal<MyV1NewsParam> threadLocal = new ThreadLocal<>();
    @Autowired
    private SiteNewsMapper siteNewsMapper;
    @Autowired
    private CommonService commonService;


    @Override
    public void init(String param) {
        threadLocal.set(JsonUtil.toObj(param, MyV1NewsParam.class));
    }

    @Override
    public void sync() {
        MyV1NewsParam param = threadLocal.get();
        String res = HttpClient.sendGet(param.getUrl());
        res = unicodeDecode(res);
        JSONArray arr = JSONArray.parseArray(res);
        for (int i = 0; i < arr.size(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            try {
                //新闻不存在则添加
                String sourceId = obj.getString("id");
                if (siteNewsMapper.exists(sourceId) != 0) {
                    continue;
                }
                String img = obj.getString("link");

                UploadFilesVo uploadFilesVo = commonService.downloadImage(img);

                long time = System.currentTimeMillis();

                SiteNews siteNews = new SiteNews();
                siteNews.setType(1L);
                siteNews.setTitle(obj.getString("title"));
                siteNews.setSourceId(sourceId);
                siteNews.setSource(NewsTypeEnum.MY_v1.getType());
                siteNews.setViews(new Random().nextInt(100) + 1L);
                siteNews.setShowTime(new Date(time));
                siteNews.setImgurl(uploadFilesVo.getImg());
                siteNews.setContent(obj.getString("content"));
                siteNews.setStatus("1");
                siteNewsMapper.insert(siteNews);
            } catch (Exception e) {
                log.error("新闻保存错误: {}", obj.toJSONString(), e);
            }
        }
    }

    public static String unicodeDecode(String string) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(string);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            string = string.replace(matcher.group(1), ch + "");
        }
        return string;
    }
}
