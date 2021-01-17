package com.sword.elucidator.controller;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.sword.common.constant.BaseConstants;
import com.sword.common.domain.ApiResult;
import com.sword.elucidator.domain.bo.UploadPartBo;
import com.sword.elucidator.domain.vo.FileUploadVo;
import com.sword.elucidator.service.FileUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * TODO
 *
 * @author Tan
 * @version 1.0 2021/1/17
 */
@Slf4j
@RestController
@Api(tags = "文件上传")
@RequestMapping("v2")
@RequiredArgsConstructor
public class FileUploadController {

    private final GridFsTemplate gridFsTemplate;
    private final GridFSBucket gridFSBucket;
    private final FileUploadService service;

    @ApiOperation("文件上传")
    @PostMapping("upload")
    public ApiResult<FileUploadVo> upload(@RequestParam("file") MultipartFile file) {
        return ApiResult.ok(service.upload(file));
    }

    @ApiOperation("批量上传文件")
    @PostMapping("batch/upload")
    public ApiResult<List<FileUploadVo>> batchUpload(@RequestParam("files") MultipartFile[] file) {
        return ApiResult.ok(service.batchUpload(file));
    }

    @ApiOperation("分片上传")
    @PostMapping("uploadPart")
    public ApiResult<FileUploadVo> uploadPart(UploadPartBo file) {
        FileUploadVo vo = service.uploadPart(file);
        log.info("  uploadPart {}", vo);
        return ApiResult.ok(vo);
    }

    @ApiOperation("文件预览")
    @GetMapping("preview/{id}.*")
    public void previewV2(@PathVariable String id, HttpServletResponse response) {
        try (OutputStream out = response.getOutputStream()) {
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(id)));
            if (gridFSFile != null) {
                Document document = Optional.ofNullable(gridFSFile.getMetadata()).orElse(new Document());

                response.setCharacterEncoding("UTF-8");
                response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "inline;fileName=" + URLEncoder.encode(gridFSFile.getFilename(), StandardCharsets.UTF_8));
                response.addHeader(HttpHeaders.CONTENT_TYPE, Optional.ofNullable(document.getString(BaseConstants.FILE_METADATA_CONTENT_TYPE)).orElse("image/jpeg"));
                response.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(gridFSFile.getLength()));

                gridFSBucket.downloadToStream(gridFSFile.getObjectId(), out);
            } else {
                out.write(JSON.toJSONString(ApiResult.error("file does not exist!")).getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            log.error("预览文件异常", e);
        }
    }


}
