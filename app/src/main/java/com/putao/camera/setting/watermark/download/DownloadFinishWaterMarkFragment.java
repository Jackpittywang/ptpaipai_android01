package com.putao.camera.setting.watermark.download;

import android.view.View;
import android.widget.GridView;

import com.putao.camera.R;
import com.putao.camera.application.MainApplication;
import com.putao.camera.base.BaseFragment;
import com.putao.camera.bean.WaterMarkCategoryInfo;
import com.putao.widget.pulltorefresh.PullToRefreshGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DownloadFinishWaterMarkFragment extends BaseFragment {
    private PullToRefreshGridView mPullRefreshGridView;
    private GridView mGridView;
    private DownloadFinishWaterMarkAdapter mManagementAdapter;
    private ArrayList<WaterMarkCategoryInfo> list;

    public static DownloadFinishWaterMarkFragment newInstance() {
        DownloadFinishWaterMarkFragment fragment = new DownloadFinishWaterMarkFragment();
        return fragment;
    }

    @Override
    public int doGetContentViewId() {
        return R.layout.layout_download_finish_water_mark_fragment;
    }

    @Override
    public void doInitSubViews(View view) {
        super.doInitSubViews(view);
        mPullRefreshGridView = (PullToRefreshGridView) view.findViewById(R.id.pull_refresh_grid);
    }

    @Override
    public void doInitDataes() {

    }

    @Override
    public void onResume() {
        super.onResume();
        mGridView = mPullRefreshGridView.getRefreshableView();
        mManagementAdapter = new DownloadFinishWaterMarkAdapter(mActivity);
        mGridView.setAdapter(mManagementAdapter);

        Map<String, String> map = new HashMap<String, String>();
        map.put("type", WaterMarkCategoryInfo.photo);
        map.put("isInner", "0");
        list = (ArrayList<WaterMarkCategoryInfo>) MainApplication.getDBServer().getWaterMarkCategoryInfoByWhere(map);
        mManagementAdapter.setDatas(list);
    }

}
