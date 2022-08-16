package com.wangyit.qrcodeapp.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import com.hjq.http.EasyConfig;
import com.wangyit.qrcodeapp.R;

import okhttp3.OkHttpClient;

public final class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initSdk(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // 清理所有图片内存缓存
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        // 根据手机内存剩余情况清理图片内存缓存
    }

    /**
     * 初始化一些第三方框架
     */
    public static void initSdk(Application application) {

        // Activity 栈管理初始化
        com.wangyit.qrcodeapp.manager.ActivityManager.getInstance().init(application);

        // 网络请求框架初始化
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        // 获取登录用户的token
        SharedPreferences sp = application.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String token = sp.getString("token", "");

        EasyConfig.with(okHttpClient)
                // 是否打印日志
                .setLogEnabled(com.wangyit.qrcodeapp.other.AppConfig.isLogEnable())
                // 设置服务器配置
                .setServer(new com.wangyit.qrcodeapp.http.model.RequestServer())
                // 设置请求处理策略
                .setHandler(new com.wangyit.qrcodeapp.http.model.RequestHandler(application))
                // 设置请求重试次数
                .setRetryCount(1)
                // 添加全局请求参数
//                .addParam("token", token)
                // 添加全局请求头
                .addHeader("token", token)
                // 启用配置
                .into();

        // 注册网络状态变化监听
        ConnectivityManager connectivityManager = ContextCompat.getSystemService(application, ConnectivityManager.class);
        if (connectivityManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
                @Override
                public void onLost(@NonNull Network network) {
                    Activity topActivity = com.wangyit.qrcodeapp.manager.ActivityManager.getInstance().getTopActivity();
                    if (topActivity instanceof LifecycleOwner) {
                        LifecycleOwner lifecycleOwner = ((LifecycleOwner) topActivity);
                        if (lifecycleOwner.getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                            Toast.makeText(topActivity.getBaseContext(), R.string.common_network_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }
}