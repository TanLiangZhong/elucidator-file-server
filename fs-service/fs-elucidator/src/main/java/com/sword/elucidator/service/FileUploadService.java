package com.sword.elucidator.service;

import com.sword.common.domain.PageVo;
import com.sword.elucidator.domain.bo.FileSearchBo;
import com.sword.elucidator.domain.bo.UploadPartBo;
import com.sword.elucidator.domain.document.FileDocument;
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
     * @return {@link FileDocument}
     */
    FileDocument upload(MultipartFile file);

    /**
     * 批量上传
     *
     * @param file file
     * @return {@link FileDocument}
     */
    List<FileDocument> batchUpload(MultipartFile[] file);

    /**
     * 分片上传
     *
     * @param file 文件对象
     * @return {@link FileDocument}
     */
    FileDocument uploadPart(UploadPartBo file);

    /**
     * 分页查询
     *
     * @param bo 查询条件
     * @return {@link FileDocument}
     */
    PageVo<FileDocument> findPage(FileSearchBo bo);
}
