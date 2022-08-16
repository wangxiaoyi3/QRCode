package com.wangyit.test.controller;

import com.wangyit.test.websocket.QRCodeServer;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/${api.version}/test")
@Api(tags = "测试")
public class TestController {


    @RequestMapping("/data")
    public Object data() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", "test");
        return result;
    }

    @RequestMapping("/ws")
    public void testSendMessage(String sid, String status) throws IOException {
        Map<String, String> wsMap = new HashMap<>();
        wsMap.put("qrcode", sid);
        wsMap.put("status", status);
        QRCodeServer.sendInfo(sid, wsMap);
    }

}
