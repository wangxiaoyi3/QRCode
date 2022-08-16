package com.wangyit.test.websocket;

import com.alibaba.druid.support.json.JSONUtils;
import com.google.gson.Gson;
import com.wangyit.test.base.BaseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@RestController
@ServerEndpoint("/ws/qrcode/{sid}")    // 指定websocket 连接的url
public class QRCodeServer {

    // 用来记录当前连接数的变量
    private static volatile int onlineCount = 0;

    // concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象
    private static final CopyOnWriteArraySet<QRCodeServer> webSocketSet = new CopyOnWriteArraySet<>();

    // 与某个客户端的连接会话，需要通过它来与客户端进行数据收发
    private Session session;

    // 接受sid
    private String sid = "";

    private static final Gson gson = new Gson();

    /**
     * 打开连接
     *
     * @param session 会话
     * @throws Exception 异常
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) throws Exception {
        this.session = session;
        webSocketSet.add(this);
        this.sid = sid;
        addOnlineCount();
        log.info("客户端：{} 连接成功, 当前连接总数：{}", sid, getOnlineCount());
    }

    /**
     * 关闭连接
     */
    @OnClose
    public void onClose() {
        subOnlineCount();
        webSocketSet.remove(this);
        log.info("客户端：{} 断开连接, 当前连接总数：{}", this.sid, getOnlineCount());
    }

    /**
     * 接收消息
     *
     * @param message 消息
     * @param session 会话
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("从客户端：{} 收到消息：{}", this.sid, message);
    }

    /**
     * 报错
     *
     * @param session 会话
     * @param error   错误
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("服务错误：{}", error.getMessage());
    }

    /**
     * 发送消息
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 群发自定义消息
     */
    public static void sendInfo(@PathParam("sid") String sid, Object message) throws IOException {
        log.info("推送消息：{} 到客户端：{}", message, sid);
        for (QRCodeServer item : webSocketSet) {
            try {
                //为null则全部推送
                if (sid == null) {
//                    item.sendMessage(message);
                } else if (item.sid.equals(sid)) {
                    item.sendMessage(gson.toJson(BaseResult.success(message)));
                }
            } catch (IOException e) {
                continue;
            }
        }
    }


    /**
     * 获取在线连接数
     *
     * @return onlineCount
     */
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    /**
     * 增加在线数量
     */
    public static synchronized void addOnlineCount() {
        QRCodeServer.onlineCount++;
    }

    /**
     * 减少在线数量
     */
    public static synchronized void subOnlineCount() {
        QRCodeServer.onlineCount--;
    }

    public static CopyOnWriteArraySet<QRCodeServer> getWebSocketSet() {
        return webSocketSet;
    }


}

