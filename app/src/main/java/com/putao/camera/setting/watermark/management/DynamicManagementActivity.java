
package com.putao.camera.setting.watermark.management;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.putao.camera.R;
import com.putao.camera.application.MainApplication;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.bean.DynamicIconInfo;
import com.putao.camera.collage.util.CollageHelper;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.db.DatabaseServer;
import com.putao.camera.downlad.DownloadFileService;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.http.CacheRequest;
import com.putao.camera.setting.watermark.download.DownloadFinishedDynamicAdapter;
import com.putao.camera.util.CommonUtils;
import com.putao.camera.util.Loger;
import com.putao.widget.pulltorefresh.PullToRefreshGridView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class DynamicManagementActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        UpdateCallback<DynamicListInfo.PackageInfo>, View.OnClickListener {
    private Button right_btn, back_btn;
    private PullToRefreshGridView mPullRefreshGridView;
    private GridView mGridView;
//    private DynamicManagementAdapter mManagementAdapter;
private DownloadFinishedDynamicAdapter mManagementAdapter;
    private TextView title_tv,tv_delect_selected,tv_select_all;
    private ArrayList<DynamicIconInfo> list;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_dynamic_management;
    }

    @Override
    public void doInitSubViews(View view) {
        tv_select_all=queryViewById(R.id.tv_select_all);
        tv_delect_selected=queryViewById(R.id.tv_delect_selected);
        title_tv = (TextView) view.findViewById(R.id.title_tv);
        title_tv.setText("动态贴图");
        mPullRefreshGridView = (PullToRefreshGridView) view.findViewById(R.id.pull_refresh_grid);
        back_btn = (Button) view.findViewById(R.id.back_btn);
        EventBus.getEventBus().register(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getEventBus().unregister(this);
    }
    @Override
    public void onResume() {
        super.onResume();
        mGridView = mPullRefreshGridView.getRefreshableView();
        mManagementAdapter = new DownloadFinishedDynamicAdapter(mActivity);
        mGridView.setAdapter(mManagementAdapter);

        Map<String, String> map = new HashMap<String, String>();
//        map.put("type", WaterMarkCategoryInfo.photo);
        map.put("type", "dynamic");
//        map.put("type", "1");
        list = (ArrayList<DynamicIconInfo>) MainApplication.getDBServer().getDynamicIconInfoByWhere(map);
        mManagementAdapter.setDatas(list);
    }

    @Override
    public void doInitData() {
//        mGridView = mPullRefreshGridView.getRefreshableView();
//        mPullRefreshGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
////                Toast.makeText(CollageManagementFragment.this, "Pull Down!", Toast.LENGTH_SHORT).show();
//                //                new GetDataTask().execute();
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
////                Toast.makeText(mContext, "Pull Up!", Toast.LENGTH_SHORT).show();
//                //                new GetDataTask().execute();
//            }
//        });
//
////        mManagementAdapter = new DynamicManagementAdapter(mActivity);
//        mManagementAdapter = new DownloadFinishDynamicAdapter(mActivity);
//        mManagementAdapter.setUpdateCallback(this);
//        mGridView.setAdapter(mManagementAdapter);
//        mGridView.setOnItemClickListener(this);
        addOnClickListener(back_btn,tv_delect_selected,tv_select_all);
//        queryCollageList();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Bundle bundle = new Bundle();
//                WaterMarkPackageListInfo.PackageInfo info = mManagementAdapter.getItem(position);
//                bundle.putSerializable("info", info);
//                ActivityHelper.startActivity(this, WaterMarkCategoryDetailActivity.class, bundle);
    }

    int progress = 0;

    @Override
    public void startProgress(DynamicListInfo.PackageInfo info, final int position) {
        String path = CollageHelper.getCollageUnzipFilePath();
        startDownloadService(info.download_url, path, position);
    }

    @Override
    public void delete(DynamicListInfo.PackageInfo info, final int position) {
    }

    private void updateProgressPartly(int progress, int position) {
        int firstVisiblePosition = mGridView.getFirstVisiblePosition();
        int lastVisiblePosition = mGridView.getLastVisiblePosition();
        if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
            View view = mGridView.getChildAt(position - firstVisiblePosition);
            if (view.getTag() instanceof DynamicManagementAdapter.ViewHolder) {
                DynamicManagementAdapter.ViewHolder vh = (DynamicManagementAdapter.ViewHolder) view.getTag();
                vh.download_status_pb.setProgress(progress);
                if (progress > 0 && progress < 100) {
                    vh.download_status_pb.setVisibility(View.VISIBLE);
                } else if (progress == 100) {
                    vh.download_status_pb.setVisibility(View.INVISIBLE);
                    vh.collage_photo_ok_iv.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void updateFinish() {
        //        vh.download_status_pb.setVisibility(View.INVISIBLE);
        //        vh.collage_photo_ok_iv.setVisibility(View.VISIBLE);
    }

    public void queryCollageList() {
        CacheRequest.ICacheRequestCallBack mWaterMarkUpdateCallback = new CacheRequest.ICacheRequestCallBack() {
            @Override
            public void onSuccess(int whatCode, JSONObject json) {
                super.onSuccess(whatCode, json);
                final DynamicListInfo aDynamicListInfo;
                try {
                    Gson gson = new Gson();
                    aDynamicListInfo = (DynamicListInfo) gson.fromJson(json.toString(), DynamicListInfo.class);
//                    mManagementAdapter.setDatas(aDynamicListInfo.data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mManagementAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(int whatCode, int statusCode, String responseString) {
                super.onFail(whatCode, statusCode, responseString);
            }
        };
        HashMap<String, String> map = new HashMap<String, String>();
        CacheRequest mCacheRequest = new CacheRequest(PuTaoConstants.PAIPAI_MATTER_LIST_PATH + "?type=dynamic_pic&page=1", map, mWaterMarkUpdateCallback);
        mCacheRequest.startGetRequest();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.tv_select_all:
                for (DynamicIconInfo dynamicIconInfo : mManagementAdapter.getDatas()) {
                    dynamicIconInfo.setChecked(true);
                }
                mManagementAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_delect_selected:
                DatabaseServer dbServer = MainApplication.getDBServer();
                ArrayList<DynamicIconInfo> datas = new ArrayList<>();
                for (DynamicIconInfo dynamicIconInfo : mManagementAdapter.getDatas()) {
                    if (!dynamicIconInfo.isChecked()) {
                        datas.add(dynamicIconInfo);
                    } else dbServer.deleteDynamicIconInfo(dynamicIconInfo);
                }
                Bundle bundle = new Bundle();
                mManagementAdapter.setDatas(datas);
                mManagementAdapter.notifyDataSetChanged();
                EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.REFRESH_DYNAMIC_MANAGEMENT_ACTIVITY, bundle));

                break;
        }
    }

    private void startDownloadService(final String url, final String folderPath, final int position) {
        boolean isExistRunning = CommonUtils.isServiceRunning(mActivity, DownloadFileService.class.getName());
        if (isExistRunning) {
            Loger.i("startDownloadService:exist");
            return;
        } else {
            Loger.i("startDownloadService:run");
        }
        if(null == url || null == folderPath) return;
        Intent bindIntent = new Intent(mActivity, DownloadFileService.class);
        bindIntent.putExtra("position", position);
        bindIntent.putExtra("url", url);
        bindIntent.putExtra("floderPath", folderPath);
        bindIntent.putExtra("type", DownloadFileService.DOWNLOAD_TYPE_DYNAMIC);
        mActivity.startService(bindIntent);
    }

    public void onEvent(BasePostEvent event) {
        switch (event.eventCode) {
            case PuTaoConstants.DOWNLOAD_FILE_FINISH: {
                Loger.d("DOWNLOAD_FILE_FINISH");
                final int percent = event.bundle.getInt("percent");
                final int position = event.bundle.getInt("position");
                mActivity. runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateProgressPartly(percent, position);
                    }
                });
                break;
            }
            case PuTaoConstants.DOWNLOAD_FILE_DOWNLOADING: {
                //                String filePath = event.bundle.getString("percent");
                final int percent = event.bundle.getInt("percent");
                final int position = event.bundle.getInt("position");
                mActivity. runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateProgressPartly(percent, position);
                    }
                });
                break;
            }
            case PuTaoConstants.UNZIP_FILE_FINISH:
                mActivity. runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mManagementAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case PuTaoConstants.REFRESH_COLLAGE_MANAGEMENT_ACTIVITY:
                mActivity. runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mManagementAdapter.notifyDataSetChanged();
                    }
                });
                break;
        }
    }
}
