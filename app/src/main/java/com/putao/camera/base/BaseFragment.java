
package com.putao.camera.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.putao.camera.event.EventBus;


public abstract class BaseFragment extends Fragment {
    protected View containerView;
    public BaseActivity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (BaseActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getEventBus().register(this);
        if (containerView == null) {
            if (doGetContentView() != null) {
                containerView = doGetContentView();
            } else {
                containerView = inflater.inflate(doGetContentViewId(), container, false);
            }
            //                    viewQuery = new B5MViewQuery();
            //                    viewQuery.setView(containerView).setClickListener(this);
            doInitSubViews(containerView);
            doInitDataes();
        }
        ViewGroup parent = (ViewGroup) containerView.getParent();
        if (parent != null) {
            parent.removeView(containerView);
        }
        return containerView;
    }
    protected void addOnClickListener(View... views) {
        View.OnClickListener listener = (View.OnClickListener) mActivity;
        if (listener != null)
            for (int i = 0; i < views.length; i++)
                views[i].setOnClickListener(listener);
    }

    public int doGetContentViewId() {
        return 0;
    }

    public View doGetContentView() {
        return null;
    }

    public void doInitSubViews(View view) {
    }

    public void doInitDataes() {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getEventBus().unregister(this);

    }
}
