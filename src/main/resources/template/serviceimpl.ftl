package ${serviceImpl.packagePath};

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ${entity.classPath};
import ${mapper.classPath};
import ${service.classPath};
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ${serviceImpl.className} extends ServiceImpl<${mapper.className}, ${entity.className}> implements ${service.className} {
    @Override
    public List<${entity.className}> selectList(${entity.className} query) {
        QueryWrapper<${entity.className}> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<${entity.className}> selectByPage(${entity.className} query, PageParam page) {
        QueryWrapper<${entity.className}> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<${entity.className}> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public ${entity.className} detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(${entity.className} body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(${entity.className} body) {
        return updateById(body);
    }
}
