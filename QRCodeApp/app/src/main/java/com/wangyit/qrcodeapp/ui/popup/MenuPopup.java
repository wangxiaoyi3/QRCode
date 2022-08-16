package com.wangyit.qrcodeapp.ui.popup;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.wangyit.qrcodeapp.R;
import com.wangyit.qrcodeapp.bean.Menu;

import java.util.List;

import razerdp.basepopup.BasePopupWindow;

public class MenuPopup extends BasePopupWindow implements View.OnClickListener {

    private List<Menu> menuList;

    private AppCompatImageView mIvArrow;

    private LinearLayout menuLayout;

    private OnClickListener onClickListener;

    public interface OnClickListener {

        void onSelect(int position);

    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public MenuPopup(Context context) {
        this(context, null);

    }

    public MenuPopup(Context context, List<Menu> menuList) {
        super(context);
        setContentView(R.layout.popup_menu);
        setBackground(R.color.black30);
        mIvArrow = findViewById(R.id.iv_arrow);
        menuLayout = findViewById(R.id.ll_menu);
        this.menuList = menuList;
    }

    public MenuPopup setMenuList(List<Menu> menuList) {
        this.menuList = menuList;
        return this;
    }

    @Override
    public boolean onBeforeShow() {
        if (menuLayout.getChildCount() == 0 && menuList != null && !menuList.isEmpty()) {
            AppCompatTextView menuView;
            Drawable icon;
            for (Menu menu : menuList) {
                menuView = (AppCompatTextView)LayoutInflater.from(getContext()).inflate(R.layout.popup_menu_item, null);
                if (menu.getIcon() != 0) {
                    icon = ContextCompat.getDrawable(getContext(), menu.getIcon());
                    if (icon != null) {
                        icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
                    }
                    menuView.setCompoundDrawables(icon, null, null, null);
                    menuView.setCompoundDrawablePadding(10);
                }
                menuView.setText(menu.getTitle());
                menuView.setOnClickListener(this);
                menuLayout.addView(menuView);
            }

        }
        return super.onBeforeShow();
    }

    @Override
    public void onPopupLayout(@NonNull Rect popupRect, @NonNull Rect anchorRect) {
        super.onPopupLayout(popupRect, anchorRect);
//        //计算basepopup中心与anchorview中心方位
//        //e.g：算出gravity == Gravity.Left，意味着Popup显示在anchorView的左侧
//        int gravity = computeGravity(popupRect, anchorRect);
//        boolean verticalCenter = false;
//        //计算垂直位置
//        switch (gravity & Gravity.VERTICAL_GRAVITY_MASK) {
//            case Gravity.TOP:
//                mIvArrow.setVisibility(View.VISIBLE);
//                //设置箭头水平位置为相对于basepopup居中
//                mIvArrow.setTranslationX((popupRect.width() - mIvArrow.getWidth()) >> 1);
//                //设置箭头垂直位置为相对于basepopup底部
//                mIvArrow.setTranslationY(popupRect.height() - mIvArrow.getHeight());
//                //设置旋转角度0度（即箭头朝下，具体根据您的初始切图而定）
//                mIvArrow.setRotation(0f);
//                break;
//            case Gravity.BOTTOM:
//                mIvArrow.setVisibility(View.VISIBLE);
//                mIvArrow.setTranslationX((popupRect.width() - mIvArrow.getWidth()) >> 1);
//                mIvArrow.setTranslationY(0);
//                mIvArrow.setRotation(180f);
//                break;
//            case Gravity.CENTER_VERTICAL:
//                verticalCenter = true;
//                break;
//        }
//        switch (gravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
//            case Gravity.LEFT:
//                mIvArrow.setVisibility(View.VISIBLE);
//                mIvArrow.setTranslationX(popupRect.width() - mIvArrow.getWidth());
//                mIvArrow.setTranslationY((popupRect.height() - mIvArrow.getHeight()) >> 1);
//                mIvArrow.setRotation(270f);
//                break;
//            case Gravity.RIGHT:
//                mIvArrow.setVisibility(View.VISIBLE);
//                mIvArrow.setTranslationX(0);
//                mIvArrow.setTranslationY((popupRect.height() - mIvArrow.getHeight()) >> 1);
//                mIvArrow.setRotation(90f);
//                break;
//            case Gravity.CENTER_HORIZONTAL:
//                //如果basepopup与anchorview中心对齐，则隐藏箭头
//                mIvArrow.setVisibility(verticalCenter ? View.INVISIBLE : View.VISIBLE);
//                break;
//        }
    }

    @Override
    public void onClick(View v) {
        this.dismiss();
        onClickListener.onSelect(menuLayout.indexOfChild(v));
    }
}
