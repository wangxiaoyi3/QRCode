package com.wangyit.qrcodeapp.ui.qrcode;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.zxing.Result;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.king.zxing.CaptureActivity;
import com.king.zxing.DecodeConfig;
import com.king.zxing.DecodeFormatManager;
import com.king.zxing.analyze.MultiFormatAnalyzer;
import com.wangyit.qrcodeapp.R;

public class QRCodeActivity extends CaptureActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImmersionBar.with(this).init();

        TitleBar titleBar = findViewById(R.id.tb_qr_code);
        ImmersionBar.setTitleBar(this, titleBar);
        titleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View v) {
                finish();
            }

            @Override
            public void onTitleClick(View v) {

            }

            @Override
            public void onRightClick(View v) {
                Toast.makeText(getApplicationContext(), "暂未开发", Toast.LENGTH_SHORT).show();
            }
        });
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        StatusBarUtils.immersiveStatusBar(this, toolbar,0.2f);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_qr_code;
    }

    @Override
    public void initCameraScan() {
        super.initCameraScan();
        //初始化解码配置
        DecodeConfig decodeConfig = new DecodeConfig();
        decodeConfig.setHints(DecodeFormatManager.QR_CODE_HINTS)//如果只有识别二维码的需求，这样设置效率会更高，不设置默认为DecodeFormatManager.DEFAULT_HINTS
                .setFullAreaScan(false)//设置是否全区域识别，默认false
                .setAreaRectRatio(0.8f)//设置识别区域比例，默认0.8，设置的比例最终会在预览区域裁剪基于此比例的一个矩形进行扫码识别
                .setAreaRectVerticalOffset(0)//设置识别区域垂直方向偏移量，默认为0，为0表示居中，可以为负数
                .setAreaRectHorizontalOffset(0);//设置识别区域水平方向偏移量，默认为0，为0表示居中，可以为负数

        //在启动预览之前，设置分析器，只识别二维码
        getCameraScan()
                .setPlayBeep(true)//设置是否播放音效，默认为false
                .setVibrate(true)//设置是否震动，默认为false
                .setNeedAutoZoom(true)//二维码太小时可自动缩放，默认为false
                .setDarkLightLux(30f)//设置光线足够暗的阈值（单位：lux），需要通过{@link #bindFlashlightView(View)}绑定手电筒才有效
                .setBrightLightLux(70f)//设置光线足够明亮的阈值（单位：lux），需要通过{@link #bindFlashlightView(View)}绑定手电筒才有效
                .bindFlashlightView(ivFlashlight)//绑定手电筒，绑定后可根据光线传感器，动态显示或隐藏手电筒按钮
                .setAnalyzer(new MultiFormatAnalyzer(decodeConfig));//设置分析器,如果内置实现的一些分析器不满足您的需求，你也可以自定义去实现
    }

    @Override
    public boolean onScanResultCallback(Result result) {
        return super.onScanResultCallback(result);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.ivLeft:
                finish();
                break;
            case R.id.ivRight:
                Toast.makeText(getApplicationContext(), "暂未开发", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}