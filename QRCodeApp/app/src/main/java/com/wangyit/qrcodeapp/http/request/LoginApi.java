package com.wangyit.qrcodeapp.http.request;

import com.hjq.http.config.IRequestApi;

public class LoginApi implements IRequestApi {

    @Override
    public String getApi() {
        return "api/v1/user/login";
    }
}
