package com.wangyit.test.service.impl;

import com.auth0.jwt.interfaces.Claim;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.wangyit.test.base.BaseResult;
import com.wangyit.test.entity.QRCode;
import com.wangyit.test.entity.User;
import com.wangyit.test.jwt.JwtUtil;
import com.wangyit.test.mapper.QrcodeMapper;
import com.wangyit.test.service.QRCodeService;
import com.wangyit.test.service.UserService;
import com.wangyit.test.util.QRCodeUtil;
import com.wangyit.test.websocket.QRCodeServer;
import lombok.extern.slf4j.Slf4j;
import org.iherus.codegen.qrcode.SimpleQrcodeGenerator;
import org.iherus.codegen.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2022-07-30
 */
@Service
@Slf4j
@Transactional
public class QRCodeServiceImpl extends ServiceImpl<QrcodeMapper, QRCode> implements QRCodeService {

    private final UserService userService;

    @Autowired
    public QRCodeServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public BaseResult<?> generateQRCode(String code, String path) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> map = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryTime = now.plusMinutes(1);
//        String code = UUID.randomUUID().toString().replace("-", "");
        log.info("QRCode：{}", code);
        map.put("qrCode", code);
        map.put("expiryTime", expiryTime.format(formatter));
        OutputStream out = null;
        try {
            String status = checkStatus(code);
            map.put("status", status);
            if (status.equals("2") || status.equals("3")) {
                if (status.equals("3")) {
                    Map<String, String> wsMap = new HashMap<>();
                    wsMap.put("qrcode", code);
                    wsMap.put("status", "2");
                    QRCodeServer.sendInfo(code, wsMap);
                }
                return BaseResult.failed("二维码已失效");
            }
            if (status.equals("1")) {
                QueryWrapper<QRCode> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("qrcode", code);
                QRCode qrCode = this.getOne(queryWrapper);
                expiryTime = qrCode.getExpiryTime();
                map.put("expiryTime", expiryTime.format(formatter));
            }
            long expire = now.until(expiryTime, ChronoUnit.SECONDS);
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            out = new FileOutputStream(path + code + ".png");
            new SimpleQrcodeGenerator().generate(new Gson().toJson(map)).toStream(out);
            if (status.equals("0")) {
                QRCode qrCode = QRCode.builder()
                        .qrcode(code)
                        .generateTime(now)
                        .expiryTime(expiryTime)
                        .status("1")
                        .build();
                this.save(qrCode);
            }
            if (!QRCodeUtil.qrCode.containsKey(code)) {
                QRCodeUtil.qrCode.put(code, expire);
                new Thread(() -> {
                    try {
                        log.info("二维码{} {}秒后失效", code, expire);
                        Thread.sleep(expire * 1000);
                        log.info("二维码{}失效", code);
                        Map<String, String> wsMap = new HashMap<>();
                        wsMap.put("qrcode", code);
                        wsMap.put("status", "2");
                        QRCodeServer.sendInfo(code, wsMap);
                        this.setInvalid(code);
                        QRCodeUtil.qrCode.remove(code);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } finally {
            IOUtils.closeQuietly(out);
        }
        return BaseResult.success();
    }

    @Override
    public String checkStatus(String code) {
        String status = "0";
        QueryWrapper<QRCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("qrcode", code);
        QRCode qrCode = this.getOne(queryWrapper);
        if (qrCode != null) {
            if (qrCode.getStatus().equals("0")) {
                status = "2";
            } else {
                if (qrCode.getExpiryTime().isAfter(LocalDateTime.now())) {
                    status = "1";
                } else {
                    if (qrCode.getStatus().equals("1")) {
                        status = "3";
                        this.setInvalid(code);
                    }
                }
            }
        }
        return status;
    }

    @Override
    public BaseResult<?> scan(String qrCode) {
        try {
            Map<String, String> wsMap = new HashMap<>();
            wsMap.put("qrcode", qrCode);
            wsMap.put("status", "3");
            QRCodeServer.sendInfo(qrCode, wsMap);
            return BaseResult.success("扫码成功");
        } catch (Exception e) {
            e.printStackTrace();
            return BaseResult.failed(e.getMessage());
        }
    }


    @Override
    public BaseResult<?> confirm(String token, String qrCode) {
        try {
            String status = checkStatus(qrCode);
            if (!"1".equals(status)) {
                Map<String, String> wsMap = new HashMap<>();
                wsMap.put("qrcode", qrCode);
                wsMap.put("status", "2");
                QRCodeServer.sendInfo(qrCode, wsMap);
                return BaseResult.failed("二维码已失效");
            }
            Map<String, Claim> claimMap = JwtUtil.getPayloadByToken(token);
            Long userId = claimMap.get("userId").asLong();
            User user = userService.getById(userId);
            if (null != user) {
                String newToken = JwtUtil.getToken(user);
                Map<String, String> wsMap = new HashMap<>();
                wsMap.put("qrcode", qrCode);
                wsMap.put("status", "4");
                wsMap.put("token", newToken);
                QRCodeServer.sendInfo(qrCode, wsMap);
                setInvalid(qrCode);
                return BaseResult.success("确认成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return BaseResult.failed(e.getMessage());
        }
        return BaseResult.failed("确认失败");
    }

    @Override
    public BaseResult<?> expire(String qrCode) {
        try {
            Map<String, String> wsMap = new HashMap<>();
            wsMap.put("qrcode", qrCode);
            wsMap.put("status", "2");
            QRCodeServer.sendInfo(qrCode, wsMap);
            setInvalid(qrCode);
            return BaseResult.failed("二维码已失效");
        } catch (Exception e) {
            e.printStackTrace();
            return BaseResult.failed(e.getMessage());
        }
    }

    @Override
    public BaseResult<?> cancel(String qrCode) {
        try {
            Map<String, String> wsMap = new HashMap<>();
            wsMap.put("qrcode", qrCode);
            wsMap.put("status", "1");
            QRCodeServer.sendInfo(qrCode, wsMap);
            return BaseResult.success("取消登录");
        } catch (Exception e) {
            e.printStackTrace();
            return BaseResult.failed(e.getMessage());
        }
    }

    @Override
    public void setInvalid(String qrCode) {
        UpdateWrapper<QRCode> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("status", "0");
        updateWrapper.eq("qrcode", qrCode);
        this.update(updateWrapper);
    }

}
