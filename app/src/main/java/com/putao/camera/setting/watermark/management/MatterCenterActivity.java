
package com.putao.camera.setting.watermark.management;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.putao.camera.R;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.setting.watermark.download.DownloadFinishActivity;
import com.putao.camera.util.ActivityHelper;

/**
 * Created by yanglun on 15/4/10.
 */
public class MatterCenterActivity extends BaseActivity  implements View.OnClickListener{
    private Button back_btn, right_btn;
    private TextView title_tv,tv_paster,tv_dynamic,tv_template;
    private ViewPager vp_content;
    private SparseArray<Fragment> mFragments;

    private boolean mIspaster = false;


    @Override
    public int doGetContentViewId() {
        return R.layout.activity_matter_center;
    }

    @Override
    public void doInitSubViews(View view) {
        tv_template=queryViewById(R.id.tv_template);
        tv_paster=queryViewById(R.id.tv_paster);
        tv_dynamic=queryViewById(R.id.tv_dynamic);

        back_btn = queryViewById(R.id.back_btn);
        title_tv =  queryViewById(R.id.title_tv);
        right_btn =  queryViewById(R.id.right_btn);
        right_btn.setText("素材管理");
        title_tv.setText("素材中心");
        vp_content=queryViewById(R.id.vp_content);

        addFragments();

//        vp_content.setOverScrollMode();
        vp_content.setCurrentItem(0, false);
        vp_content.setOffscreenPageLimit(3);
    }

    /**
     * 添加Fragment
     */
    private void addFragments() {
        vp_content.setOffscreenPageLimit(1);
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
                Bundle bundle = new Bundle();
                bundle.putString("source", this.getClass().getName());
                ActivityHelper.startActivity(mActivity, DownloadFinishActivity.class, bundle);
//                Bundle bundle = new Bundle();
//                ActivityHelper.startActivity(mActivity, DownloadFinishMaterialCenterActivity.class,bundle);
                break;
            case R.id.tv_paster:
                vp_content.setCurrentItem(0, false);
                tv_paster.setBackgroundResource(R.drawable.shippment_tab_title);
                break;
            case R.id.tv_dynamic:
                vp_content.setCurrentItem(1, false);
                tv_dynamic.setBackgroundResource(R.drawable.shippment_tab_title);
                break;
            case R.id.tv_template:
                vp_content.setCurrentItem(2, false);
                tv_template.setBackgroundResource(R.drawable.shippment_tab_title);
                break;

            case R.id.back_btn:
                finish();
                break;
        }
    }



    @Override
    public void doInitData() {
        addOnClickListener(right_btn, back_btn,tv_paster,tv_dynamic,tv_template);

    }






}
