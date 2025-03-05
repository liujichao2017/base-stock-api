package ${entity.packagePath};

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

<#list entity.importPaths as importPath>
import ${importPath};
</#list>

/**
* ${remark} 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class ${entity.className} extends BaseEntity {

<#list entity.fields as field>
    @Schema(title = "${field.remark}")
    @Parameter(description = "${field.remark}")
    private ${field.type} ${field.name};

</#list>
}
