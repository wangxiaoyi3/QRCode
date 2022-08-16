package com.wangyit.qrcodeapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.wangyit.qrcodeapp.ui.home.MainActivity;
import com.wangyit.qrcodeapp.ui.popup.ConfirmPopup;

public class ConfirmService extends Service {

    private ConfirmPopup confirmPopup;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        confirm();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void confirm() {
        if (confirmPopup == null) {
            confirmPopup = new ConfirmPopup(getBaseContext());
            confirmPopup
                    .setOutSideDismiss(false)
                    .setBlurBackgroundEnable(true);
        } else {
            confirmPopup.dismiss();
        }
        confirmPopup.showPopupWindow();
        confirmPopup.setPopupListener(new ConfirmPopup.PopupListener() {
            @Override
            public void onShowing() {
                sendMessageToActivity(MainActivity.ACTION_SERVICE_SHOW);
            }

            @Override
            public void onDismiss() {
                sendMessageToActivity(MainActivity.ACTION_SERVICE_DISMISS);
            }
        });

        confirmPopup.setOnClickListener(new ConfirmPopup.OnClickListener() {
            @Override
            public void onClose() {
                confirmPopup.dismiss();
            }

            @Override
            public void onConfirm() {
                confirmPopup.dismiss();
                sendMessageToActivity(MainActivity.ACTION_SERVICE_CONFIRM);
            }

            @Override
            public void onCancel() {
                confirmPopup.dismiss();
            }
        });
    }

    private void sendMessageToActivity(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        sendBroadcast(intent);
    }
}
