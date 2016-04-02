package com.sunnybear.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.sunnybear.library.R;

/**
 * 加载布局
 * Created by guchenkai on 8/19/15.
 */
public class LoadingLayout extends FrameLayout {

    private int emptyView, errorView, loadingView;
    private OnClickListener onRetryClickListener;

    public LoadingLayout(Context context) {
        this(context, null);
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public LoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LoadingLayout, 0, 0);
        try {
            emptyView = a.getResourceId(R.styleable.LoadingLayout_emptyView, R.layout.widget_empty_view);
            errorView = a.getResourceId(R.styleable.LoadingLayout_errorView, R.layout.widget_error_view);
            loadingView = a.getResourceId(R.styleable.LoadingLayout_loadingView, R.layout.widget_loading_view);

            LayoutInflater inflater = LayoutInflater.from(getContext());
            inflater.inflate(emptyView, this, true);
            inflater.inflate(errorView, this, true);
            inflater.inflate(loadingView, this, true);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount() - 1; i++) {
            getChildAt(i).setVisibility(GONE);
        }
        findViewById(R.id.btn_empty_retry).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRetryClickListener != null)
                    onRetryClickListener.onClick(v);
            }
        });
        findViewById(R.id.btn_error_retry).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRetryClickListener != null)
                    onRetryClickListener.onClick(v);
            }
        });
    }

    /**
     * 加载重试监听器
     *
     * @param onRetryClickListener 加载重试监听器
     */
    public void setOnRetryClickListener(OnClickListener onRetryClickListener) {
        this.onRetryClickListener = onRetryClickListener;
    }

    /**
     * 显示空页面
     */
    public void showEmpty() {
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            if (i == 0)
                child.setVisibility(VISIBLE);
            else
                child.setVisibility(GONE);
        }
    }

    /**
     * 显示错误页面
     */
    public void showError() {
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            if (i == 1)
                child.setVisibility(VISIBLE);
            else
                child.setVisibility(GONE);
        }
    }

    /**
     * 显示加载页面
     */
    public void showLoading() {
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            if (i == 2)
                child.setVisibility(VISIBLE);
            else
                child.setVisibility(GONE);
        }
    }

    /**
     * 显示内容页面
     */
    public void showContent() {
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            if (i == 3)
                child.setVisibility(VISIBLE);
            else
                child.setVisibility(GONE);
        }
    }
}
