
package com.putao.camera.setting.watermark.management;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.putao.camera.R;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.setting.watermark.download.DownloadFinishMaterialCenterActivity;
import com.putao.camera.util.ActivityHelper;
import com.putao.widget.view.UnScrollableViewPager;

/**
 * Created by yanglun on 15/4/10.
 */
public class MatterCenterActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private Button back_btn, right_btn;
    private TextView title_tv;
    private RadioButton matter_paster_btn, matter_dynamic_pasting_btn, matter_jigsaw_btn;
    private UnScrollableViewPager vp_content;
    private RadioGroup rg_matter;
    private SparseArray<Fragment> mFragments;

    private boolean mIspaster = false;


    @Override
    public int doGetContentViewId() {
        return R.layout.activity_matter_center;
    }

    @Override
    public void doInitSubViews(View view) {
        back_btn = queryViewById(R.id.back_btn);
        title_tv = queryViewById(R.id.title_tv);
        right_btn = queryViewById(R.id.right_btn);
        right_btn.setText("素材管理");
        title_tv.setText("素材中心");
        matter_paster_btn = queryViewById(R.id.matter_paster_btn);
        matter_dynamic_pasting_btn = queryViewById(R.id.matter_dynamic_pasting_btn);
        matter_jigsaw_btn = queryViewById(R.id.matter_jigsaw_btn);
        vp_content = queryViewById(R.id.vp_content);
        rg_matter = queryViewById(R.id.rg_matter);

        addFragments();
        rg_matter.check(R.id.matter_paster_btn);
        vp_content.setCurrentItem(0, false);
        rg_matter.setOnCheckedChangeListener(this);
        vp_content.setOffscreenPageLimit(3);
    }

    /**
     * 添加Fragment
     */
    private void addFragments() {
        mFragments = new SparseArray<>();
        mFragments.put(0, Fragment.instantiate(mActivity, WaterMarkCategoryManagementFragment.class.getName()));
        mFragments.put(1, Fragment.instantiate(mActivity, DynamicManagementFragment.class.getName()));
        mFragments.put(2, Fragment.instantiate(mActivity, CollageManagementFragment.class.getName()));
        vp_content.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.right_btn:
//                Bundle bundle = new Bundle();
//                bundle.putString("source", this.getClass().getName());
//                ActivityHelper.startActivity(mActivity, DownloadFinishActivity.class, bundle);
                Bundle bundle = new Bundle();
                ActivityHelper.startActivity(mActivity, DownloadFinishMaterialCenterActivity.class, bundle);
                break;
            case R.id.back_btn:
                finish();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.matter_paster_btn:
                vp_content.setCurrentItem(0, false);
                break;
            case R.id.matter_dynamic_pasting_btn:
                vp_content.setCurrentItem(1, false);
                break;
            case R.id.matter_jigsaw_btn:
                vp_content.setCurrentItem(2, false);
                break;
        }
    }


    @Override
    public void doInitData() {
        addOnClickListener(right_btn, back_btn);
    }
}
