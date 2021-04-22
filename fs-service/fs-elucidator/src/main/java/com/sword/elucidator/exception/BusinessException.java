package com.sword.elucidator.exception;

import com.sword.common.constant.ResultMsg;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 业务异常
 *
 * @author Tan
 * @version 1.0 2020/11/02
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    private ResultMsg resultMsg;

    public BusinessException(String msg) {
        super(msg);
    }

    public BusinessException(ResultMsg result) {
        super(result.getMsg());
        this.resultMsg = result;
    }

    public BusinessException(String message, Exception e) {
        super(message, e);
    }

    public BusinessException(ResultMsg result, String message) {
        super(message);
        this.resultMsg = result;
    }

    public static BusinessException of(ResultMsg msg) {
        return new BusinessException(msg);
    }

    public static BusinessException of(String message) {
        return new BusinessException(ResultMsg.FAIL, message);
    }

    public static BusinessException of(String message, Exception e) {
        return new BusinessException(message, e);
    }
}
