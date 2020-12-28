package com.sword.elucidator.exception;

import com.sword.common.constant.ResultMsg;
import com.sword.common.domain.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 全局异常拦截
 *
 * @author Tan
 * @version 1.0 2020/11/02
 */
@Slf4j
@Component
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     *
     * @param ex {@link BusinessException}
     * @return ApiResult
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResult<String> handleException(BusinessException ex) {
        ApiResult<String> restVo = ApiResult.fail();
        if (ex.getResultMsg() != null) {
            restVo.setCode(ex.getResultMsg().getCode());
        }
        restVo.setMsg(ex.getMessage());
        log.error("业务异常 [code => {}, msg => {}]", restVo.getCode(), restVo.getMsg(), ex);
        return restVo;
    }


    /**
     * 参数校验异常处理
     *
     * @param ex {@link MethodArgumentNotValidException}
     * @return ApiResult
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<Map<String, String>> handleException(MethodArgumentNotValidException ex) {
        log.error("Argument Not Valid Exception");
        BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> data = new HashMap<>(8);
        bindingResult.getAllErrors().forEach(it -> {
            FieldError fieldError = (FieldError) it;
            log.error("objectName: {}, field: {}, message: {}", fieldError.getObjectName(), fieldError.getField(), fieldError.getDefaultMessage());
            data.put(fieldError.getField(), fieldError.getDefaultMessage());
        });
        return ApiResult.of(ResultMsg.ARGUMENT_NOT_INVALID, data);
    }

    /**
     * 处理系统异常
     *
     * @param ex      {@link Exception}
     * @param request 请求
     * @return ApiResult
     */
    @ExceptionHandler(Exception.class)
    public ApiResult<String> handleException(Exception ex, HttpServletRequest request) throws UnsupportedEncodingException {
        StringBuilder message = new StringBuilder();
        message.append("\n######################### Error #########################\n");
        message.append("RequestURI: ").append(request.getRequestURI()).append("\n");
        message.append("Method: ").append(request.getMethod()).append("\n");
        message.append("Headers: \n");
        Iterator<String> headerIterator = CollectionUtils.toIterator(request.getHeaderNames());
        while (headerIterator.hasNext()) {
            String name = headerIterator.next();
            message.append("\t").append(name).append(": ").append(URLDecoder.decode(request.getHeader(name), "UTF-8")).append("\n");
        }
        log.info(message.toString(), ex);
        return ApiResult.error();
    }

}
