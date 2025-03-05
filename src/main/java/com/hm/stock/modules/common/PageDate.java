package com.hm.stock.modules.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class PageDate<T> {
    @Schema(title = "总条数")
    private long total;

    @Schema(title = "行数据")
    private List<T> list;

    private PageDate(){
    }

    public static <T> PageDate<T> of(IPage<T> page){
        PageDate<T> pageDate = new PageDate<>();
        pageDate.total = page.getTotal();
        pageDate.list = page.getRecords();
        return pageDate;
    }

    public static <T> PageDate<T> of(long total, List<T> list){
        PageDate<T> pageDate = new PageDate<>();
        pageDate.total = total;
        pageDate.list = list;
        return pageDate;
    }
    public static <T> PageDate<T> of(){
        PageDate<T> pageDate = new PageDate<>();
        pageDate.total = 0;
        pageDate.list = Collections.emptyList();
        return pageDate;
    }


}
