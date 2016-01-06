
package com.putao.camera.setting.watermark.download;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.putao.camera.R;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.setting.MyFragmentPagerAdapter;
import com.putao.camera.setting.watermark.management.WaterMarkCategoryManagementActivity;

import java.util.ArrayList;

public class DownloadFinishActivity extends BaseActivity implements View.OnClickListener {
    private ViewPager mPager;
    private ArrayList<Fragment> fragmentList;
    private Button title1_btn, title2_btn;
    private Button back_btn;
    private TextView title_tv;
    private Button right_btn;

    public void initTextView() {
        back_btn = (Button) findViewById(R.id.back_btn);
        title_tv = (TextView) findViewById(R.id.title_tv);
        title_tv.setText("下载管理");
        right_btn = (Button) findViewById(R.id.right_btn);
        right_btn.setVisibility(View.INVISIBLE);
        right_btn.setText("完成");
        title1_btn = (Button) findViewById(R.id.title1_btn);
        title2_btn = (Button) findViewById(R.id.title2_btn);
        addOnClickListener(back_btn, title1_btn, title2_btn);
    }

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_download_finish;
    }

    @Override
    public void doInitSubViews(View view) {
        initTextView();
        initViewPager();
        String source = this.getIntent().getStringExtra("source");
        if (source.equals(WaterMarkCategoryManagementActivity.class.getName())) {
            mPager.setCurrentItem(0);
        } else {
            mPager.setCurrentItem(1);
        }
    }

    @Override
    public void doInitData() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.title1_btn:
                mPager.setCurrentItem(0);
                break;
            case R.id.title2_btn:
                mPager.setCurrentItem(1);
                break;
        }
    }

    /*
     * ViewPager
     */
    public void initViewPager() {
        mPager = (ViewPager) findViewById(R.id.viewpager);
        fragmentList = new ArrayList<Fragment>();
        Fragment secondFragment = DownloadFinishWaterMarkFragment.newInstance();
        Fragment thirdFragment = DownloadFinishCollageFragment.newInstance();
        fragmentList.add(secondFragment);
        fragmentList.add(thirdFragment);
        mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    public class MyOnPageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPageSelected(int arg0) {
            if (arg0 == 0) {
                title1_btn.setBackgroundResource(R.drawable.red_btn_bg);
                title1_btn.setTextAppearance(mActivity, R.style.button_red);
                title2_btn.setBackgroundResource(android.R.color.transparent);
                title2_btn.setTextAppearance(mActivity, R.style.button_transparent);
            } else if (arg0 == 1) {
                title2_btn.setBackgroundResource(R.drawable.red_btn_bg);
                title2_btn.setTextAppearance(mActivity, R.style.button_red);
                title1_btn.setBackgroundResource(android.R.color.transparent);
                title1_btn.setTextAppearance(mActivity, R.style.button_transparent);
            }
        }
    }
}