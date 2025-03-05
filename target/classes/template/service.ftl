package ${service.packagePath};

import com.baomidou.mybatisplus.extension.service.IService;
import ${entity.classPath};
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface ${service.className} extends IService<${entity.className}> {
    List<${entity.className}> selectList(${entity.className} query);

    PageDate<${entity.className}> selectByPage(${entity.className} query, PageParam page);

    ${entity.className} detail(Long id);

    Long add(${entity.className} body);

    boolean delete(Long id);

    boolean update(${entity.className} body);
}
