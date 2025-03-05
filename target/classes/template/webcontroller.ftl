package ${webController.packagePath};

import com.hm.stock.domain.auth.annotation.AuthAnnotation;
import com.hm.stock.domain.auth.enums.RoleEnum;
import ${entity.classPath};
import ${service.classPath};
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/${url}")
@Tag(name = "${remark}管理接口")
public class ${webController.className} {

    @Autowired
    private ${service.className} service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<${entity.className}>> selectByPage(@Parameter ${entity.className} query,@Parameter PageParam page) {
        return Result.ok(service.selectByPage(query, page));
    }

    @GetMapping("/list")
    @Operation(summary = "列表查询")
    public Result<List<${entity.className}>> selectList(${entity.className} query) {
        return Result.ok(service.selectList(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "通过ID查询详情")
    public Result<${entity.className}> detail(@PathVariable("id") Long id) {
        return Result.ok(service.detail(id));
    }

    @PostMapping
    @AuthAnnotation(RoleEnum.ADMIN)
    @Operation(summary = "添加${remark}")
    public Result<Long> add(@RequestBody ${entity.className} body) {
        return Result.ok(service.add(body));
    }

    @PutMapping
    @AuthAnnotation(RoleEnum.MANAGER)
    @Operation(summary = "修改${remark}")
    public Result<Boolean> update(@RequestBody ${entity.className} body) {
        return Result.ok(service.update(body));
    }

    @DeleteMapping("/{id}")
    @AuthAnnotation(RoleEnum.MANAGER)
    @Operation(summary = "删除${remark}")
    public Result<Boolean> delete(@PathVariable("id") Long id) {
        return Result.ok(service.delete(id));
    }

}
