package com.hm.stock.domain.common.service.impl;
import com.hm.stock.domain.common.service.CommonService;
import com.hm.stock.domain.common.vo.UploadFilesVo;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.execptions.CommonResultCode;
import com.hm.stock.modules.execptions.ErrorResultCode;
import com.hm.stock.modules.execptions.InternalException;
import com.hm.stock.modules.utils.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

@Service
public class CommonServiceImpl implements CommonService {

    private static final Logger log = LoggerFactory.getLogger(CommonServiceImpl.class);

    @Value("${file.path}")
    private String filePath;

    @Override
    public UploadFilesVo upload(MultipartFile multipartFile) {
        String fileName = this.buildSaveName(multipartFile);
        // 文件信息
        String savePath = filePath;
        // 创建目标目录
        File directory = new File(savePath);
        LogicUtils.assertTrue(directory.exists() || directory.mkdirs(), CommonResultCode.INTERNAL_ERROR);

        // 保存文件
        try {
            File dest = new File(directory, fileName);
            multipartFile.transferTo(dest);
            UploadFilesVo vo = new UploadFilesVo();
            vo.setImg(fileName);
            return vo;
        } catch (IOException e) {
            log.error("保存文件异常: {}", savePath, e);
            throw new InternalException(CommonResultCode.INTERNAL_ERROR);
        }
    }

    @Override
    public UploadFilesVo upload(String base64) {
        // 文件信息
        String savePath = filePath;
        // 创建目标目录
        File directory = new File(savePath);
        LogicUtils.assertTrue(directory.exists() || directory.mkdirs(), CommonResultCode.INTERNAL_ERROR);

        // 保存文件
        try {
            String fileName = UUID.randomUUID().toString().replace("-", "").concat(".png");
            File dest = new File(directory, fileName);
            Files.write(dest.toPath(), Base64.getDecoder().decode(base64), StandardOpenOption.CREATE);
            UploadFilesVo vo = new UploadFilesVo();
            vo.setImg(fileName);
            return vo;
        } catch (IOException e) {
            log.error("保存文件异常: {}", savePath, e);
            throw new InternalException(CommonResultCode.INTERNAL_ERROR);
        }
    }

    @Override
    public UploadFilesVo downloadImage(String img) {
        // 文件信息
        String savePath = filePath;
        // 创建目标目录
        File directory = new File(savePath);
        LogicUtils.assertTrue(directory.exists() || directory.mkdirs(), CommonResultCode.INTERNAL_ERROR);

        // 保存文件
        try {
            String fileName = UUID.randomUUID().toString().replace("-", "").concat(".png");
            File dest = new File(directory, fileName);
            Files.write(dest.toPath(), HttpClient.sendGetByByte(img), StandardOpenOption.CREATE);
            UploadFilesVo vo = new UploadFilesVo();
            vo.setImg(fileName);
            return vo;
        } catch (IOException e) {
            log.error("保存文件异常: {}", savePath, e);
            throw new InternalException(CommonResultCode.INTERNAL_ERROR);
        }
    }

    /**
     * 生成文件名称
     *
     * @param multipartFile 文件对象
     * @return String
     */
    private String buildSaveName(MultipartFile multipartFile) {
        String name = multipartFile.getOriginalFilename();
        String ext = Objects.requireNonNull(name).substring(name.lastIndexOf("."));
        return UUID.randomUUID() + ext.toLowerCase();
    }

}
