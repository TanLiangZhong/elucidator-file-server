package com.sword.elucidator.service;

import com.sword.elucidator.domain.bo.UploadPartBo;
import com.sword.elucidator.domain.vo.FileUploadVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传
 *
 * @author Tan
 * @version 1.0 2021/1/17
 */
public interface FileUploadService {


    /**
     * 上传文件
     *
     * @param file file
     * @return {@link FileUploadVo}
     */
    FileUploadVo upload(MultipartFile file);

    /**
     * 批量上传
     *
     * @param file file
     * @return {@link FileUploadVo}
     */
    List<FileUploadVo> batchUpload(MultipartFile[] file);

    /**
     * 分片上传
     *
     * @param file 文件对象
     * @return {@link FileUploadVo}
     */
    FileUploadVo uploadPart(UploadPartBo file);


}
