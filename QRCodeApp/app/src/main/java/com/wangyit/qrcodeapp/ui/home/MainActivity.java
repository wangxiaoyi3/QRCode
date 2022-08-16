package com.wangyit.qrcodeapp.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.TitleBar;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.king.zxing.CameraScan;
import com.wangyit.qrcodeapp.R;
import com.wangyit.qrcodeapp.base.AppActivity;
import com.wangyit.qrcodeapp.bean.Menu;
import com.wangyit.qrcodeapp.data.model.QRCode;
import com.wangyit.qrcodeapp.http.model.HttpData;
import com.wangyit.qrcodeapp.http.request.QrCodeApi;
import com.wangyit.qrcodeapp.http.request.UserApi;
import com.wangyit.qrcodeapp.http.response.User;
import com.wangyit.qrcodeapp.service.ConfirmService;
import com.wangyit.qrcodeapp.ui.popup.ConfirmPopup;
import com.wangyit.qrcodeapp.ui.popup.MenuPopup;
import com.wangyit.qrcodeapp.ui.qrcode.QRCodeActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppActivity {

    public static final int REQUEST_CODE_SCAN = 0X01;
    public static final int REQUEST_CODE_PHOTO = 0X02;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final String ACTION_SERVICE_SHOW = "action.ServiceShow";
    public static final String ACTION_SERVICE_DISMISS = "action.ServiceDismiss";
    public static final String ACTION_SERVICE_CONFIRM = "action.ServiceConfirm";
    public ServiceNeedBroadcastReceiver broadcastReceiver;

    private ConfirmPopup confirmPopup;

    private MenuPopup menuPopup;

    private AppCompatTextView userNameTextView;

    private TitleBar titleBar;

    private List<Menu> menuList;

    private String code;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        userNameTextView = findViewById(R.id.tv_user_name);
        titleBar = findViewById(R.id.tb_home);
        ImmersionBar.setTitleBar(this, titleBar);
        initReceiver();
    }

    @Override
    protected void initData() {
        menuList = new ArrayList<>();
        menuList.add(new Menu(R.drawable.ic_qr_code, "扫一扫"));
        menuList.add(new Menu(R.drawable.ic_menu, "其他"));
        getUserInfo();
    }

    private void getUserInfo() {
        EasyHttp.post(this)
                .api(new UserApi().setType("profile"))
                .request(new HttpCallback<HttpData<User>>(this) {
                    @Override
                    public void onSucceed(HttpData<User> result) {
                        if (result.getCode() == 0) {
                            userNameTextView.setText(result.getData().getUserName());
                        } else {
                            Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        super.onFail(e);
                    }
                });
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SERVICE_SHOW);
        filter.addAction(ACTION_SERVICE_DISMISS);
        filter.addAction(ACTION_SERVICE_CONFIRM);
        broadcastReceiver = new ServiceNeedBroadcastReceiver();
        registerReceiver(broadcastReceiver, filter);
    }

    private class ServiceNeedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_SERVICE_SHOW.equals(action)) {
                setStatusBar(false);
            } else if (ACTION_SERVICE_DISMISS.equals(action)) {
                setStatusBar(true);
                cancel();
            } else if (ACTION_SERVICE_CONFIRM.equals(action)) {
                Toast.makeText(getApplicationContext(), "onConfirm", Toast.LENGTH_SHORT).show();
                confirm();
            } else {

            }
        }
    }

    private void startQRCode() {
        Intent intent = new Intent(MainActivity.this, QRCodeActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    @Override
    public void onRightClick(View view) {
        if (menuPopup == null) {
            menuPopup = new MenuPopup(getBaseContext());
            menuPopup
                    .linkTo(view);
            menuPopup.setOnClickListener(position -> {
                if (position == 0) {
                    startQRCode();
                } else {
                    Toast.makeText(getApplicationContext(), menuList.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        menuPopup.setMenuList(menuList);
        menuPopup.showPopupWindow(view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data!=null){
            switch (requestCode){
                case REQUEST_CODE_SCAN:
                    String result = CameraScan.parseScanResult(data);
                    try {
                        Gson gson = new Gson();
                        QRCode qrCode = gson.fromJson(result, QRCode.class);
                        Date expiryTime = sdf.parse(qrCode.getExpiryTime());
                        Date now = new Date();
                        if ((!"1".equals(qrCode.getStatus()) && !"0".equals(qrCode.getStatus())) || now.after(expiryTime)) {
                            Toast.makeText(getApplicationContext(), "二维码已过期", Toast.LENGTH_SHORT).show();
                            code = qrCode.getQrCode();
                            expire();
                        } else {
                            code = qrCode.getQrCode();
                            scan();
                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, ConfirmService.class);
                            startService(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case REQUEST_CODE_PHOTO:
                    break;
            }

        }
    }

    public void setStatusBar(boolean isDark) {
        getStatusBarConfig().statusBarDarkFont(isDark).init();
    }

    private void scan() {
        EasyHttp.post(this)
                .api(new QrCodeApi()
                        .setType("scan")
                        .setQrCode(code))
                .request(new HttpCallback<HttpData<?>>(this) {

                    @Override
                    public void onSucceed(HttpData<?> result) {
                        super.onSucceed(result);
                    }

                    @Override
                    public void onFail(Exception e) {
                        super.onFail(e);
                    }
                });
    }

    private void confirm() {
        EasyHttp.post(this)
                .api(new QrCodeApi()
                        .setType("confirm")
                        .setQrCode(code))
                .request(new HttpCallback<HttpData<?>>(this) {

                    @Override
                    public void onSucceed(HttpData<?> result) {
                        super.onSucceed(result);
                    }

                    @Override
                    public void onFail(Exception e) {
                        super.onFail(e);
                    }
                });
    }

    private void expire() {
        EasyHttp.post(this)
                .api(new QrCodeApi()
                        .setType("expire")
                        .setQrCode(code))
                .request(new HttpCallback<HttpData<?>>(this) {

                    @Override
                    public void onSucceed(HttpData<?> result) {
                        super.onSucceed(result);
                    }

                    @Override
                    public void onFail(Exception e) {
                        super.onFail(e);
                    }
                });
    }

    private void cancel() {
        EasyHttp.post(this)
                .api(new QrCodeApi()
                        .setType("cancel")
                        .setQrCode(code))
                .request(new HttpCallback<HttpData<?>>(this) {

                    @Override
                    public void onSucceed(HttpData<?> result) {
                        super.onSucceed(result);
                    }

                    @Override
                    public void onFail(Exception e) {
                        super.onFail(e);
                    }
                });
    }
}