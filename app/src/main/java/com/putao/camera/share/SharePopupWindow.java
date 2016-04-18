package com.putao.camera.share;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.putao.camera.R;
import com.sunnybear.library.controller.BasicPopupWindow;

import butterknife.OnClick;

/**
 * 分享弹出框
 * Created by guchenkai on 2015/11/27.
 */
public class SharePopupWindow extends BasicPopupWindow implements View.OnClickListener {
    private boolean isCopy = true;
    private OnShareClickListener mOnShareClickListener;
    private TextView tv_collection;
    private ImageView iv_collection;

    public void setOnShareClickListener(OnShareClickListener onShareClickListener) {
        setOnShareClickListener(true, onShareClickListener);
    }

    public void setOnShareClickListener(boolean isCopy, OnShareClickListener onShareClickListener) {
        this.isCopy = isCopy;
        mOnShareClickListener = onShareClickListener;
        LinearLayout ll_second = (LinearLayout) mRootView.findViewById(R.id.ll_second);
        LinearLayout ll_qq_zone = (LinearLayout) mRootView.findViewById(R.id.ll_qq_zone);
        LinearLayout ll_share = (LinearLayout) mRootView.findViewById(R.id.ll_share);
        TextView tv_qq_zone = (TextView) mRootView.findViewById(R.id.tv_qq_zone);
        ImageView iv_qq_zone = (ImageView) mRootView.findViewById(R.id.iv_qq_zone);
        TextView tv_qq = (TextView) mRootView.findViewById(R.id.tv_qq);
        ImageView iv_qq = (ImageView) mRootView.findViewById(R.id.iv_qq);
        tv_collection = (TextView) mRootView.findViewById(R.id.tv_collection);
        iv_collection = (ImageView) mRootView.findViewById(R.id.iv_collection);
        if (!isCopy) {
            ll_qq_zone.setVisibility(View.GONE);
            ll_share.setVisibility(View.INVISIBLE);
            iv_collection.setImageResource(R.drawable.icon_40_03);
            tv_collection.setText("QQ好友");
            iv_qq.setImageResource(R.drawable.icon_40_04);
            tv_qq.setText("QQ空间");
//            //复制
//            ll_second.setVisibility(View.GONE);
//            //QQ空间
//            tv_qq_zone.setText("新浪微博");
//            iv_qq_zone.setImageResource(R.drawable.icon_40_05);
        } else {
            //复制
//            ll_second.setVisibility(View.VISIBLE);
//            //QQ空间
//            tv_qq_zone.setText("QQ空间");
//            iv_qq_zone.setImageResource(R.drawable.icon_40_04);
            ll_qq_zone.setVisibility(View.VISIBLE);
            ll_share.setVisibility(View.GONE);
            tv_collection.setText("收藏");
//            iv_collection.setImageResource(R.drawable.icon_40_13);
            iv_qq.setImageResource(R.drawable.icon_40_03);
            tv_qq.setText("QQ好友");
        }
    }

    public SharePopupWindow(Context context) {
        super(context);
//        setAnimationStyle(R.style.bottom_anim_style);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.popup_share;
    }

    @OnClick({
            R.id.ll_wechat,
            R.id.ll_wechat_friend_circle,
            R.id.ll_collection,
            R.id.ll_qq_friend,
            R.id.ll_qq_zone,
            R.id.ll_sina_weibo,
            R.id.ll_copy_url,
            R.id.tv_cancel
    })
    @Override
    public void onClick(View v) {
        if (mOnShareClickListener != null)
            switch (v.getId()) {
                case R.id.ll_wechat://微信
                    mOnShareClickListener.onWechat();
                    break;
                case R.id.ll_wechat_friend_circle://微信朋友圈
                    mOnShareClickListener.onWechatFriend();
                    break;
                case R.id.ll_collection://收藏
                    if (isCopy) {
                        mOnShareClickListener.onCollection();
                    } else {
                        mOnShareClickListener.onQQFriend();
                    }
                    break;
                case R.id.ll_qq_friend://QQ好友
                    if (isCopy)
                        mOnShareClickListener.onQQFriend();
                    else
                        mOnShareClickListener.onQQZone();
                    break;
                case R.id.ll_qq_zone://QQ空间
                    if (isCopy) {
                        mOnShareClickListener.onQQZone();
                    } else {
                        mOnShareClickListener.onSinaWeibo();
                    }
                    break;
                case R.id.ll_sina_weibo://新浪微博
                    mOnShareClickListener.onSinaWeibo();
                    break;
                case R.id.ll_copy_url://复制链接
                    mOnShareClickListener.onCopyUrl();
                    break;
            }
        dismiss();
    }

    @Override
    public void dismiss() {
        if (mOnShareClickListener != null)
            mOnShareClickListener.onCancel();
        super.dismiss();
    }

    public void setCollectState(boolean isCollect) {
        if (isCollect) {
            tv_collection.setText("取消收藏");
//            iv_collection.setImageResource(R.drawable.icon_40_14);

        } else {
//            iv_collection.setImageResource(R.drawable.icon_40_13);
            tv_collection.setText("收藏");
        }
    }
}