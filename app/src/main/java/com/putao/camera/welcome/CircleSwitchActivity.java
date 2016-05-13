
package com.putao.camera.welcome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.putao.camera.R;
import com.putao.camera.application.MainApplication;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.camera.ActivityCamera;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.Loger;
import com.putao.camera.util.SharedPreferencesHelper;
import com.putao.camera.welcome.fragment.SwitchFragment;
import com.putao.camera.welcome.view.AutoScrollViewPagerCirclePageIndicator;

/**
 * Created by yanglun on 15/1/9.
 */
public class CircleSwitchActivity extends BaseActivity {
    private boolean fromAbout = false;
    private ViewPager mPager;
    private AutoScrollViewPagerCirclePageIndicator mIndicator;
   /* public static int[] logos = new int[]{R.drawable.introduction_picture_wall01, R.drawable.introduction_picture_wall02,
            R.drawable.introduction_picture_wall03, R.drawable.introduction_picture_wall04};*/

    public static int[] logos = new int[]{R.drawable.img_wt_01, R.drawable.img_wt_02,
            R.drawable.img_wt_03, R.drawable.img_wt_04};
    private boolean isFristUse;
    @Override
    public void doBefore() {
        super.doBefore();
        isFristUse = SharedPreferencesHelper.readBooleanValue(this, PuTaoConstants.PREFERENC_FIRST_USE_APPLICATION, true);

        int lastVersionCode = SharedPreferencesHelper.readIntValue(this, PuTaoConstants.PREFERENC_VERSION_CODE, 0);
        int curVersionCode = MainApplication.getVersionCode();
        if (getIntent() != null) {
            fromAbout = getIntent().getBooleanExtra("fromAbout", false);
        }
        isFristUse = false;
//        fromAbout = false;
        if (isFristUse || lastVersionCode != curVersionCode) {
            SharedPreferencesHelper.saveIntValue(this, PuTaoConstants.PREFERENC_VERSION_CODE, curVersionCode);
        } else if (!fromAbout) {
            //                if (SharedPreferencesHelper.readBooleanValue(this, PuTaoConstants.PREFERENC_CAMERA_ENTER_SETTING, false))
            //                {
            //                    ActivityHelper.startActivity(this, ActivityCamera.class);
            //                } else
            //            {
            ActivityHelper.startActivity(this, ActivityCamera.class);
            //            }
            finish();
        }
    }

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_circle_switch;
    }

    @Override
    public void doInitSubViews(View view) {
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new SwitchFragmentAdapter(getSupportFragmentManager()));
        mIndicator = (AutoScrollViewPagerCirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setSnap(true);
        mIndicator.setViewPager(mPager);
        EventBus.getEventBus().register(this);
    }

    @Override
    public void doInitData() {
        Loger.i("current time:" + System.currentTimeMillis());
    }

    public void onEvent(BasePostEvent event) {
        switch (event.eventCode) {
            case PuTaoConstants.WELCOME_FINISH_EVENT:
                if (!fromAbout) {
//                    ActivityHelper.startActivity(mActivity, ActivityCamera.class);
                }
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getEventBus().unregister(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.welcome_to_left_out);
        if (isFristUse) {
            SharedPreferencesHelper.saveBooleanValue(this, PuTaoConstants.PREFERENC_FIRST_USE_APPLICATION, false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        try {
//            LogoActivity.instance.finish();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    class SwitchFragmentAdapter extends FragmentPagerAdapter {
        public SwitchFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            Bundle bundle = new Bundle();
            bundle.putInt("position", arg0);
            bundle.putBoolean("fromAbout", fromAbout);
            return SwitchFragment.newInstance(bundle);
        }

        @Override
        public int getCount() {
            return logos.length;
        }
    }
}