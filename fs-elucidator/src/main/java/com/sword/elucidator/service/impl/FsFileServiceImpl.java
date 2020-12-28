package com.sword.elucidator.service.impl;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.sword.elucidator.conf.FsProperties;
import com.sword.elucidator.domain.document.FsFileDocument;
import com.sword.elucidator.domain.vo.FsFileVo;
import com.sword.elucidator.exception.BusinessException;
import com.sword.elucidator.repository.FsFileRepository;
import com.sword.elucidator.service.FsFileService;
import com.sword.elucidator.utils.ServletUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 文件 - service 小型
 *
 * @author Tan
 * @version 1.0 2020/12/27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FsFileServiceImpl implements FsFileService {

    private final FsFileRepository fsFileRepository;
    private final FsProperties fsProperties;
    private final GridFsTemplate gridFsTemplate;
    private final GridFSBucket gridFSBucket;

    @Override
    public FsFileVo upload(MultipartFile file) {
        try {
            return conversion(saveFsFile(file, uploadBigFile(file)));
        } catch (Exception e) {
            log.info("upload file error ===>>> ", e);
            throw new BusinessException("文件上传失败！Error: " + e.getMessage());
        }
    }

    @Override
    public List<FsFileVo> batchUpload(MultipartFile[] files) {
        return Stream.of(files).map(this::upload).collect(Collectors.toList());
    }

    @Override
    public Optional<FsFileVo> getFileBytes(String id) {
        Optional<FsFileDocument> optionalFsFile = fsFileRepository.findById(id);
        if (optionalFsFile.isEmpty()) {
            return Optional.empty();
        }
        FsFileDocument fsFile = optionalFsFile.get();
        FsFileVo vo = conversion(fsFile);

        GridFSFile gridFsFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fsFile.getContentId())));
        vo.setContent(Optional.ofNullable(gridFsFile).map(this::downloadGridFsFile).orElse(null));
        return Optional.of(vo);
    }

    @Override
    public Optional<FsFileVo> getGridFsFile(String id) {
        Optional<FsFileDocument> optionalFsFile = fsFileRepository.findById(id);
        if (optionalFsFile.isEmpty()) {
            return Optional.empty();
        }
        FsFileVo vo = conversion(optionalFsFile.get());
        vo.setGridFsFile(gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(optionalFsFile.get().getContentId()))));
        return Optional.of(vo);
    }

    /**
     * 下载 GridFs文件
     *
     * @param gridFsFile GridFs文件
     * @return content
     */
    private byte[] downloadGridFsFile(GridFSFile gridFsFile) {
        try (GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(gridFsFile.getObjectId())) {
            if (downloadStream.getGridFSFile().getLength() > 0) {
                GridFsResource resource = new GridFsResource(gridFsFile, downloadStream);
                return resource.getContent().readAllBytes();
            }
        } catch (Exception e) {
            log.info("GridFSDownloadStream Error ===>>> ", e);
        }
        return null;
    }

    private FsFileVo conversion(FsFileDocument source) {
        FsFileVo target = new FsFileVo();
        BeanUtils.copyProperties(source, target);
        target.setPreviewUrl(fsProperties.getBasePreviewUrl() + "/preview/" + target.getId() + target.getSuffix());
        return target;
    }

    /**
     * 上传大文件
     *
     * @param file file
     * @return file id
     * @throws IOException Io 异常
     */
    private String uploadBigFile(MultipartFile file) throws IOException {
        return gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType()).toHexString();
    }

    /**
     * 保存 FsFileDocument
     *
     * @param file      文件
     * @param contentId 文件内容Id
     * @return FsFileDocument
     * @throws IOException Io异常
     */
    private FsFileDocument saveFsFile(MultipartFile file, String contentId) throws IOException {
        String md5 = DigestUtils.md5DigestAsHex(file.getBytes());
        String fileName = Optional.ofNullable(file.getOriginalFilename()).orElse("");
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        return fsFileRepository.save(new FsFileDocument(
                fileName,
                file.getContentType(),
                file.getSize(),
                suffix,
                md5,
                LocalDateTime.now(),
                contentId
        ));
    }
}
