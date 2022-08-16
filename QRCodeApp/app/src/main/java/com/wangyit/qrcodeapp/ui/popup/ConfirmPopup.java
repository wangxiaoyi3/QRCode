package com.wangyit.qrcodeapp.ui.popup;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.wangyit.qrcodeapp.R;

import razerdp.basepopup.BasePopupWindow;
import razerdp.util.animation.AnimationHelper;
import razerdp.util.animation.TranslationConfig;

public class ConfirmPopup extends BasePopupWindow implements View.OnClickListener {

    private static final String TAG = "ConfirmPopup";
    private AppCompatTextView closeTextView;

    private AppCompatButton confirmButton;

    private AppCompatTextView cancelTextView;

    private PopupListener popupListener;

    private OnClickListener onClickListener;

    public interface PopupListener {

        void onShowing();

        void onDismiss();
    }

    public interface OnClickListener {

        void onClose();

        void onConfirm();

        void onCancel();

    }

    public void setPopupListener(PopupListener popupListener) {
        this.popupListener = popupListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    public ConfirmPopup(Context context) {
        super(context);
        setContentView(R.layout.popup_confirm);
        closeTextView = findViewById(R.id.tv_close);
        confirmButton = findViewById(R.id.btn_confirm);
        cancelTextView = findViewById(R.id.tv_cancel);
        setOnClickListener(closeTextView, confirmButton, cancelTextView);
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return AnimationHelper.asAnimation()
                .withTranslation(TranslationConfig.FROM_BOTTOM)
                .toShow();
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return AnimationHelper.asAnimation()
                .withTranslation(TranslationConfig.TO_BOTTOM)
                .toDismiss();
    }

    @Override
    public void onShowing() {
        super.onShowing();
        popupListener.onShowing();
    }

    @Override
    public void onDismiss() {
        super.onDismiss();
        popupListener.onDismiss();
    }

    @Override
    public void onClick(View v) {
        if (onClickListener != null) {
            if (v == closeTextView) {
                onClickListener.onClose();
            } else if (v == confirmButton) {
                onClickListener.onConfirm();
            } else if (v == cancelTextView) {
                onClickListener.onCancel();
            }
        }
    }

    private void setOnClickListener(View ...views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }
}
