package com.wangyit.test.service;

import com.wangyit.test.base.BaseResult;
import com.wangyit.test.entity.QRCode;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2022-07-30
 */
public interface QRCodeService extends IService<QRCode> {

    BaseResult<?> generateQRCode(String code, String path) throws Exception;

    String checkStatus(String code);

    BaseResult<?> scan(String qrCode);

    BaseResult<?> confirm(String token, String qrCode);

    BaseResult<?> expire(String qrCode);

    BaseResult<?> cancel(String qrCode);

    void setInvalid(String qrCode);

}
