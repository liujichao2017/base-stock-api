package com.hm.stock.domain.common.controller;

import com.hm.stock.domain.common.service.CommonService;
import com.hm.stock.domain.common.vo.UploadFilesVo;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.Result;
import com.hm.stock.modules.execptions.ErrorResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@Tag(name = "公共接口")
public class CommonController {

    @Autowired
    private CommonService commonService;

    @PostMapping("/upload")
    @Operation(summary = "上传图片")
    public Result<UploadFilesVo> upload(@RequestParam("file")MultipartFile multipartFile) {
        LogicUtils.assertNotNull(multipartFile, ErrorResultCode.E000008);
        return Result.ok(commonService.upload(multipartFile));
    }
}
