package com.sword.elucidator.controller;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.sword.common.domain.ApiResult;
import com.sword.elucidator.domain.vo.FsFileVo;
import com.sword.elucidator.service.FsFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * 文件上传控制器
 *
 * @author Tan
 * @version 1.0 2020/12/26
 */
@Slf4j
@RestController
@Api(tags = "文件上传")
@RequestMapping
@RequiredArgsConstructor
public class FileController {

    private final FsFileService service;

    private final GridFSBucket gridFSBucket;

    @ApiOperation("文件上传")
    @PostMapping("upload")
    public ApiResult<FsFileVo> upload(@RequestParam("files") MultipartFile file) {
        return ApiResult.ok(service.upload(file));
    }

    @ApiOperation("批量上传文件")
    @PostMapping("batch/upload")
    public ApiResult<List<FsFileVo>> batchUpload(@RequestParam("files") MultipartFile[] file) {
        return ApiResult.ok(service.batchUpload(file));
    }

    @ApiOperation("文件预览")
    @GetMapping("preview/{id}.*")
    public void preview(@PathVariable String id, HttpServletResponse response) {
        try (OutputStream out = response.getOutputStream()) {
            Optional<FsFileVo> file = service.getGridFsFile(id);
            if (file.isPresent()) {
                FsFileVo fileVo = file.get();
                response.setCharacterEncoding("UTF-8");
                response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "inline;fileName=" + URLEncoder.encode(fileVo.getName(), StandardCharsets.UTF_8));
                response.addHeader(HttpHeaders.CONTENT_TYPE, fileVo.getContentType());
                response.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileVo.getSize()));

                GridFSFile gridFsFile = fileVo.getGridFsFile();
                gridFSBucket.downloadToStream(gridFsFile.getObjectId(), out);
            } else {
                out.write(JSON.toJSONString(ApiResult.error("file does not exist!")).getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            log.error("预览文件异常", e);
        }
    }


    @ApiOperation("下载文件")
    @GetMapping("download/{id}.*")
    public void download(@PathVariable String id, HttpServletResponse response) {
        try (OutputStream out = response.getOutputStream()) {
            Optional<FsFileVo> file = service.getGridFsFile(id);
            if (file.isPresent()) {
                FsFileVo fileVo = file.get();
                response.setCharacterEncoding("UTF-8");
                response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;fileName=" + URLEncoder.encode(fileVo.getName(), StandardCharsets.UTF_8));
                response.addHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
                response.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileVo.getSize()));

                GridFSFile gridFsFile = fileVo.getGridFsFile();
                gridFSBucket.downloadToStream(gridFsFile.getObjectId(), out);
            } else {
                out.write(JSON.toJSONString(ApiResult.error("file does not exist!")).getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            log.error("下载文件异常", e);
        }
    }

}
