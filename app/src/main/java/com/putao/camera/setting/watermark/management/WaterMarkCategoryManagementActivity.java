
package com.putao.camera.setting.watermark.management;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.putao.camera.R;
import com.putao.camera.application.MainApplication;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.downlad.DownloadFileService;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.http.CacheRequest;
import com.putao.camera.setting.watermark.download.DownloadFinishActivity;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.CommonUtils;
import com.putao.camera.util.Loger;
import com.putao.camera.util.WaterMarkHelper;
import com.putao.widget.pulltorefresh.PullToRefreshBase;
import com.putao.widget.pulltorefresh.PullToRefreshGridView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public final class WaterMarkCategoryManagementActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        UpdateCallback<StickerListInfo.PackageInfo>, View.OnClickListener {
    private Button right_btn, back_btn;
    private PullToRefreshGridView mPullRefreshGridView;
    private GridView mGridView;
    private WaterMarkManagementAdapter mManagementAdapter;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_water_mark_category_management;
    }

    @Override
    public void doInitSubViews(View view) {
        mPullRefreshGridView = (PullToRefreshGridView) findViewById(R.id.pull_refresh_grid);
        EventBus.getEventBus().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getEventBus().unregister(this);
    }

    @Override
    public void doInitData() {
        mGridView = mPullRefreshGridView.getRefreshableView();
        mPullRefreshGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                Toast.makeText(WaterMarkCategoryManagementActivity.this, "Pull Down!", Toast.LENGTH_SHORT).show();
                //                new GetDataTask().execute();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                Toast.makeText(mContext, "Pull Up!", Toast.LENGTH_SHORT).show();
                //                new GetDataTask().execute();
            }
        });
        //        TextView tv = new TextView(this);
        //        tv.setGravity(Gravity.CENTER);
        //        tv.setText("Empty View, Pull Down/Up to Add Items");
        //        mPullRefreshGridView.setEmptyView(tv);
        mManagementAdapter = new WaterMarkManagementAdapter(this);
        mManagementAdapter.setUpdateCallback(this);
        mGridView.setAdapter(mManagementAdapter);
        mGridView.setOnItemClickListener(this);
        right_btn = (Button) this.findViewById(R.id.right_btn);
        right_btn.setText("已下载");
        back_btn = (Button) this.findViewById(R.id.back_btn);
        addOnClickListener(right_btn, back_btn);
        queryWaterMarkList();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
//        WaterMarkPackageListInfo.PackageInfo info = mManagementAdapter.getItem(position);
        StickerListInfo.PackageInfo info = mManagementAdapter.getItem(position);
        bundle.putString("wid", info.id+"");
        bundle.putInt("position", position);
        ActivityHelper.startActivity(this, WaterMarkCategoryDetailActivity.class, bundle);
    }

    @Override
    public void startProgress(StickerListInfo.PackageInfo info, final int position) {
        String path = WaterMarkHelper.getWaterMarkUnzipFilePath();
        startDownloadService(info.download_url, path, position);
    }

    @Override
    public void delete(StickerListInfo.PackageInfo info, final int position) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", String.valueOf(info.id));
        MainApplication.getDBServer().deleteWaterMarkCategoryInfo(map);
        mManagementAdapter.notifyDataSetChanged();
    }

    private void updateProgressPartly(int progress, int position) {
        int firstVisiblePosition = mGridView.getFirstVisiblePosition();
        int lastVisiblePosition = mGridView.getLastVisiblePosition();
        if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
            View view = mGridView.getChildAt(position - firstVisiblePosition);
            if (view.getTag() instanceof WaterMarkManagementAdapter.ViewHolder) {
                WaterMarkManagementAdapter.ViewHolder vh = (WaterMarkManagementAdapter.ViewHolder) view.getTag();
                vh.download_status_pb.setProgress(progress);
                if (progress > 0 && progress < 100) {
                    vh.water_mark_category_download_btn.setOnClickListener(null);
                    vh.water_mark_category_download_btn.setText("下载中");
                    vh.download_status_pb.setVisibility(View.VISIBLE);
                } else if (progress == 100) {
                    //                    vh.water_mark_category_download_btn.setText("删除");
                    //                    vh.download_status_pb.setVisibility(View.INVISIBLE);
                    //                    vh.water_mark_photo_ok_iv.setVisibility(View.INVISIBLE);
                    //                    mManagementAdapter.notifyDataSetChanged();
                }
            }
        }
    }
//请求水印列表
    public void queryWaterMarkList() {
        CacheRequest.ICacheRequestCallBack mWaterMarkUpdateCallback = new CacheRequest.ICacheRequestCallBack() {
            @Override
            public void onSuccess(int whatCode, JSONObject json) {
                super.onSuccess(whatCode, json);
                final StickerListInfo aWaterMarkRequestInfo;
                try {
                    Gson gson = new Gson();
                    aWaterMarkRequestInfo = (StickerListInfo) gson.fromJson(json.toString(), StickerListInfo.class);
                    mManagementAdapter.setDatas(aWaterMarkRequestInfo.data);
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
        CacheRequest mCacheRequest = new CacheRequest("watermark/watermark/list", map, mWaterMarkUpdateCallback);
        mCacheRequest.startGetRequest();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.right_btn:
                Bundle bundle = new Bundle();
                bundle.putString("source", this.getClass().getName());
                ActivityHelper.startActivity(this, DownloadFinishActivity.class, bundle);
                break;
            case R.id.back_btn:
                finish();
                break;
        }
    }
    //开启开始下载服务
    private void startDownloadService(final String url, final String folderPath, final int position) {
        boolean isExistRunning = CommonUtils.isServiceRunning(this, DownloadFileService.class.getName());
        if (isExistRunning) {
            Loger.i("startDownloadService:exist");
            return;
        } else {
            Loger.i("startDownloadService:run");
        }
        if(null == url || null == folderPath) return;
        Intent bindIntent = new Intent(this, DownloadFileService.class);
        bindIntent.putExtra("position", position);
        bindIntent.putExtra("url", url);
        bindIntent.putExtra("floderPath", folderPath);
        bindIntent.putExtra("type", DownloadFileService.DOWNLOAD_TYPE_WATER_MARK);
        this.startService(bindIntent);
    }
    //下载贴图包
    public void onEvent(BasePostEvent event) {
        switch (event.eventCode) {
            case PuTaoConstants.DOWNLOAD_FILE_FINISH: {
                Loger.d("DOWNLOAD_FILE_FINISH");
                final int percent = event.bundle.getInt("percent");
                final int position = event.bundle.getInt("position");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateProgressPartly(percent, position);
                    }
                });
                break;
            }
            case PuTaoConstants.DOWNLOAD_FILE_DOWNLOADING: {
                final int percent = event.bundle.getInt("percent");
                final int position = event.bundle.getInt("position");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateProgressPartly(percent, position);
                    }
                });
                break;
            }
            case PuTaoConstants.UNZIP_FILE_FINISH:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mManagementAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case PuTaoConstants.REFRESH_WATERMARK_MANAGEMENT_ACTIVITY:
                mManagementAdapter.notifyDataSetChanged();
                break;
        }
    }
}
