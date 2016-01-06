
package com.putao.camera.camera;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.putao.camera.R;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.util.Loger;

/**
 * Created by yanglun on 15/3/4.
 */
public class TestActivity extends BaseActivity {
    private ViewPager water_mark_scene_view_pager;
    private List<View> mSceneWaterMarkViewList;
    private WaterMarkPagerAdapter mWaterMarkPagerAdapter;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_test;
    }

    @Override
    public void doInitSubViews(View view) {
        water_mark_scene_view_pager = (ViewPager) this.findViewById(R.id.water_mark_scene_view_pager1);
    }

    @Override
    public void doInitData() {
        mSceneWaterMarkViewList = new ArrayList<View>();
        for (int i = 0; i < 3; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.layout_water_mark_scene_view_pager, null);
            mSceneWaterMarkViewList.add(view);
        }
        mWaterMarkPagerAdapter = new WaterMarkPagerAdapter();
        water_mark_scene_view_pager.setAdapter(mWaterMarkPagerAdapter);
    }


    class WaterMarkPagerAdapter extends PagerAdapter {
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public int getCount() {
            int size = mSceneWaterMarkViewList.size();
            Loger.i("size:" + size);

            return size;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mSceneWaterMarkViewList.get(position), 0);
            return mSceneWaterMarkViewList.get(position);
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }
    }
}
