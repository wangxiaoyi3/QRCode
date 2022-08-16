package com.wangyit.qrcodeapp.http.request;

import com.hjq.http.config.IRequestApi;

public class UserApi implements IRequestApi {

    private String type;

    @Override
    public String getApi() {
        return "api/v1/user/" + type;
    }

    public UserApi setType(String type) {
        this.type = type;
        return this;
    }
}
