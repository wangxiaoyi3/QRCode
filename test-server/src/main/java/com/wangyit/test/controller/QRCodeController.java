package com.wangyit.test.controller;

import com.wangyit.test.base.BaseResult;
import com.wangyit.test.service.QRCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;

@RestController
@RequestMapping("/api/${api.version}/qrcode")
@Api(tags = "二维码")
public class QRCodeController {

    private final QRCodeService qrCodeService;

    @Autowired
    public QRCodeController(QRCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @RequestMapping(value = "/generate/{code}")
    @ApiOperation(value = "生成")
    public BaseResult<?> generate(@PathVariable("code") String code, HttpServletResponse response) throws Exception {
        if (StringUtils.isBlank(code)) {
            return BaseResult.failed();
        }
        ServletOutputStream out = null;
        FileInputStream in = null;
        File qrCodeFile = null;
        try {
            String path = ResourceUtils.getURL("classpath:").toURI().getPath() + "/static/qrcode/";
            BaseResult<?> result = qrCodeService.generateQRCode(code, path);
            if (result.getCode() == 1) {
                response.setStatus(403);
                return result;
            } else {
                out = response.getOutputStream();
                response.setContentType("image/png");
                qrCodeFile = new File(path + code + ".png");
                in = new FileInputStream(qrCodeFile);
                byte[] bytes = new byte[in.available()];
                in.read(bytes, 0, in.available());
                out.write(bytes);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (qrCodeFile != null && qrCodeFile.exists()) {
                qrCodeFile.delete();
            }
        }
        return null;
    }

    @PostMapping(value = "/scan")
    @ApiOperation(value = "扫码")
    public BaseResult<?> scan(String qrCode) {
        return qrCodeService.scan(qrCode);
    }

    @PostMapping(value = "/confirm")
    @ApiOperation(value = "二维码登录确认")
    public BaseResult<?> confirm(String qrCode, HttpServletRequest request) {
        String token = request.getHeader("token");
        return qrCodeService.confirm(token, qrCode);
    }

    @PostMapping(value = "/expire")
    @ApiOperation(value = "二维码登录确认")
    public BaseResult<?> expire(String qrCode) {
        return qrCodeService.expire(qrCode);
    }

    @PostMapping(value = "/cancel")
    @ApiOperation(value = "二维码取消登录")
    public BaseResult<?> cancel(String qrCode) {
        return qrCodeService.cancel(qrCode);
    }

}
