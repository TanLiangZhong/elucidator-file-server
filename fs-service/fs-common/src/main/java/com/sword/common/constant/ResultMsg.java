package com.sword.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 状态码
 * <p>
 * 状态码规则：
 * 1xxx: 公共状态码
 *
 * @author Tan
 * @version 1.0 2020/11/02
 */
@Getter
@AllArgsConstructor
public enum ResultMsg {

    /**
     * 公共状态码
     */
    SUCCESS(1, "操作成功"),
    FAIL(0, "操作失败"),
    ERROR(-1, "系统开小差了"),
    ARGUMENT_NOT_INVALID(1001, "参数无效"),
    DATA_NOT_FOUND(1002, "没有找到记录"),
    PARAM_IS_ILLEGAL(1003, "包含非法字符"),
    VALIDATOR_ERROR(1004, "数据校验异常"),
    REPEAT_OPERATION(1005, "亲，您已操作过，请勿重复操作"),

    ;

    private final Integer code;
    private final String msg;
}
