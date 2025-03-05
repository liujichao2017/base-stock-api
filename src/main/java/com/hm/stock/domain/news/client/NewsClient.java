package com.hm.stock.domain.news.client;

import com.hm.stock.domain.news.client.impl.JsNewsClient;
import com.hm.stock.domain.news.client.impl.MyV1NewsClient;
import com.hm.stock.domain.news.entity.SiteNewsConfig;
import com.hm.stock.domain.news.enums.NewsTypeEnum;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.execptions.ErrorResultCode;
import com.hm.stock.modules.utils.BeanUtil;

public interface NewsClient {


    void init(String param);

    void sync();

    static NewsClient getInstance(SiteNewsConfig config) {
        NewsClient client = null;
        switch (NewsTypeEnum.getEnum(config.getSource())) {
            case JS:
                client = BeanUtil.getBean(JsNewsClient.class);
                break;
            case MY_v1:
                client = BeanUtil.getBean(MyV1NewsClient.class);
                break;
        }
        LogicUtils.assertNotNull(client, ErrorResultCode.E000001);
        client.init(config.getJson());
        return client;
    }
}
