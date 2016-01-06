
package com.putao.camera.setting.watermark.download;

import android.view.View;
import android.widget.GridView;

import com.putao.camera.R;
import com.putao.camera.base.BaseFragment;
import com.putao.camera.bean.CollageConfigInfo.CollageItemInfo;
import com.putao.camera.db.CollageDBHelper;
import com.putao.widget.pulltorefresh.PullToRefreshGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class DownloadFinishCollageFragment extends BaseFragment {
    private PullToRefreshGridView mPullRefreshGridView;
    private GridView mGridView;
    private DownloadFinishCollageAdapter mManagementAdapter;
    private ArrayList<CollageItemInfo> list;

    public static DownloadFinishCollageFragment newInstance() {
        DownloadFinishCollageFragment fragment = new DownloadFinishCollageFragment();
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
        mGridView = mPullRefreshGridView.getRefreshableView();
        mManagementAdapter = new DownloadFinishCollageAdapter(mActivity);
        mGridView.setAdapter(mManagementAdapter);
        Map<String, String> map = new HashMap<String, String>();
        map.put("isInner", "0");
        list = (ArrayList<CollageItemInfo>) CollageDBHelper.getInstance().queryList(map, "_id");
        mManagementAdapter.setDatas(list);
    }
}
