package com.wangyit.qrcodeapp.http.model;

public class HttpData<T> {

    /** 返回码 */
    private int code;
    /** 提示语 */
    private String msg;
    /** token */
    private String token;
    /** 数据 */
    private T data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return msg;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public T getData() {
        return data;
    }
}