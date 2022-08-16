package com.wangyit.qrcodeapp.http.request;

import com.hjq.http.config.IRequestApi;

public class QrCodeApi implements IRequestApi {

    private String type;

    private String qrCode;

    @Override
    public String getApi() {
        return "api/v1/qrcode/" + type;
    }

    public QrCodeApi setType(String type) {
        this.type = type;
        return this;
    }

    public QrCodeApi setQrCode(String qrCode) {
        this.qrCode = qrCode;
        return this;
    }
}
