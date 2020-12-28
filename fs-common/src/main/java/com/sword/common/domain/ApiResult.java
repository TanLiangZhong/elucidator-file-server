package com.sword.common.domain;

import com.sword.common.constant.ResultMsg;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 业务响应实体
 *
 * @author Tan
 * @version 1.0 2020/11/02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("业务响应")
public class ApiResult<T> implements Serializable {

    @ApiModelProperty("返回编码")
    private int code;

    @ApiModelProperty("是否成功")
    private boolean success;

    @ApiModelProperty("返回消息")
    private String msg;

    @ApiModelProperty("返回结果")
    private T result;

    @ApiModelProperty("当前时间戳")
    private Long timestamp = System.currentTimeMillis();

    public boolean isSuccess() {
        return code == ResultMsg.SUCCESS.getCode();
    }

    public ApiResult(T result) {
        this.code = ResultMsg.SUCCESS.getCode();
        this.msg = ResultMsg.SUCCESS.getMsg();
        this.result = result;
    }

    public ApiResult(ResultMsg resultMsg) {
        this.code = resultMsg.getCode();
        this.msg = resultMsg.getMsg();
    }

    public static <T> ApiResult<T> of(ResultMsg resultMsg) {
        return new ApiResult<>(resultMsg);
    }

    public static <T> ApiResult<T> of(ResultMsg resultMsg, T data) {
        ApiResult<T> r = new ApiResult<>(resultMsg);
        r.setResult(data);
        return r;
    }

    public static <T> ApiResult<T> ok() {
        return new ApiResult<>(ResultMsg.SUCCESS);
    }

    public static <T> ApiResult<T> ok(T result) {
        return new ApiResult<>(result);
    }

    public static <T> ApiResult<T> error() {
        return new ApiResult<>(ResultMsg.ERROR);
    }

    public static <T> ApiResult<T> error(String message) {
        ApiResult<T> r = new ApiResult<>(ResultMsg.ERROR);
        r.setMsg(message);
        return r;
    }

    public static <T> ApiResult<T> fail() {
        return new ApiResult<>(ResultMsg.FAIL);
    }

    public static <T> ApiResult<T> fail(String msg) {
        ApiResult<T> r = new ApiResult<>(ResultMsg.FAIL);
        r.setMsg(msg);
        return r;
    }

    public static <T> ApiResult<T> fail(T result) {
        ApiResult<T> r = new ApiResult<>(ResultMsg.FAIL);
        r.setResult(result);
        return r;
    }
}
