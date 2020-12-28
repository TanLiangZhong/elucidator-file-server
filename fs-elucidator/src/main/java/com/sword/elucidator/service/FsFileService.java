package com.sword.elucidator.service;

import com.sword.elucidator.domain.vo.FsFileVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * 文件 - Service
 *
 * @author Tan
 * @version 1.0 2020/12/26
 */
public interface FsFileService {

    /**
     * 上传文件
     *
     * @param file file
     * @return {@link FsFileVo}
     */
    FsFileVo upload(MultipartFile file);

    /**
     * 批量上传
     *
     * @param file file
     * @return {@link FsFileVo}
     */
    List<FsFileVo> batchUpload(MultipartFile[] file);

    /**
     * 获取小文件流 Bytes
     *
     * @param id 文件Id
     * @return {@link FsFileVo}
     */
    Optional<FsFileVo> getFileBytes(String id);

    /**
     * 获取 GridFSFile
     *
     * @param id 文件Id
     * @return {@link FsFileVo}
     */
    Optional<FsFileVo> getGridFsFile(String id);
}
