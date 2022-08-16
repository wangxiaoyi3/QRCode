package com.wangyit.test.base;

import lombok.Data;

/**
 * 返回数据基类
 * @param <T>
 * Data lombok注解，自动生成get，set，toString等方法，需要安装lombok
 */
@Data
public class BaseResult<T> {

    /** 返回码 */
    private int code;
    /** 提示语 */
    private String msg;
    /** token */
    private String token;
    /** 数据 */
    private T data;

    public BaseResult(ResultCode resultCode, T data) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
        this.data = data;
    }

    public BaseResult<T> withToken(String token) {
        this.token = token;
        return this;
    }

    public static <T> BaseResult<T> success() {
        return success(null);
    }

    public static <T> BaseResult<T> success(T data) {
        return new BaseResult<>(ResultCode.SUCCESS, data);
    }

    public static <T> BaseResult<T> failed() {
        return failed(null);
    }

    public static <T> BaseResult<T> failed(T data) {
        return new BaseResult<>(ResultCode.FAILED, data);
    }

    public static <T> BaseResult<T> failed(ResultCode resultCode) {
        return new BaseResult<>(resultCode, null);
    }

}
