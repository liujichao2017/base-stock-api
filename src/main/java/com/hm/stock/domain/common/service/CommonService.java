package com.hm.stock.domain.common.service;

import com.hm.stock.domain.common.vo.UploadFilesVo;
import org.springframework.web.multipart.MultipartFile;

public interface CommonService {

    UploadFilesVo upload(MultipartFile multipartFile);

    UploadFilesVo upload(String base64);

    UploadFilesVo downloadImage(String img);
}
