package com.wangyit.qrcodeapp.http.model;

import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.BodyType;
import com.wangyit.qrcodeapp.other.AppConfig;

public class RequestServer implements IRequestServer {

    @Override
    public String getHost() {
        return AppConfig.getHostUrl();
    }

    @Override
    public String getPath() {
        return "/";
    }

    @Override
    public BodyType getType() {
        // 以表单的形式提交参数
        return BodyType.FORM;
    }
}