package com.wangyit.test.base;

/**
 * 返回状态码枚举类
 */
public enum ResultCode {
    SUCCESS(0, "成功"),
    FAILED(1, "失败"),
    TOKEN_NULL(2, "token为空"),
    TOKEN_INVALID(2, "token无效");

    /** 返回码 */
    private int code;
    /** 提示语 */
    private String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
