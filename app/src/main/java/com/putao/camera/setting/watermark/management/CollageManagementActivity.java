
package com.putao.camera.setting.watermark.management;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.putao.camera.R;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.collage.util.CollageHelper;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.downlad.DownloadFileService;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.http.CacheRequest;
import com.putao.camera.setting.watermark.download.DownloadFinishActivity;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.CommonUtils;
import com.putao.camera.util.Loger;
import com.putao.widget.pulltorefresh.PullToRefreshBase;
import com.putao.widget.pulltorefresh.PullToRefreshGridView;

import org.json.JSONObject;

import java.util.HashMap;

public final class CollageManagementActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        UpdateCallback<CollageListInfo.PackageInfo>, View.OnClickListener {
    private Button right_btn, back_btn;
    private PullToRefreshGridView mPullRefreshGridView;
    private GridView mGridView;
    private CollageManagementAdapter mManagementAdapter;
    private TextView title_tv;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_collage_management;
    }

    @Override
    public void doInitSubViews(View view) {
        title_tv = (TextView) this.findViewById(R.id.title_tv);
        title_tv.setText("拼图列表");
        mPullRefreshGridView = (PullToRefreshGridView) findViewById(R.id.pull_refresh_grid);
        EventBus.getEventBus().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mManagementAdapter.notifyDataSetChanged();
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
                Toast.makeText(CollageManagementActivity.this, "Pull Down!", Toast.LENGTH_SHORT).show();
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
        mManagementAdapter = new CollageManagementAdapter(this);
        mManagementAdapter.setUpdateCallback(this);
        mGridView.setAdapter(mManagementAdapter);
        mGridView.setOnItemClickListener(this);
        right_btn = (Button) this.findViewById(R.id.right_btn);
        right_btn.setText("已下载");
        back_btn = (Button) this.findViewById(R.id.back_btn);
        addOnClickListener(right_btn, back_btn);
        queryCollageList();
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
    public void startProgress(CollageListInfo.PackageInfo info, final int position) {
        String path = CollageHelper.getCollageUnzipFilePath();
        startDownloadService(info.attachment_url, path, position);
    }

    @Override
    public void delete(CollageListInfo.PackageInfo info, final int position) {
    }

    private void updateProgressPartly(int progress, int position) {
        int firstVisiblePosition = mGridView.getFirstVisiblePosition();
        int lastVisiblePosition = mGridView.getLastVisiblePosition();
        if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
            View view = mGridView.getChildAt(position - firstVisiblePosition);
            if (view.getTag() instanceof CollageManagementAdapter.ViewHolder) {
                CollageManagementAdapter.ViewHolder vh = (CollageManagementAdapter.ViewHolder) view.getTag();
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
                final CollageListInfo aCollageInfo;
                try {
                    Gson gson = new Gson();
                    aCollageInfo = (CollageListInfo) gson.fromJson(json.toString(), CollageListInfo.class);
                    mManagementAdapter.setDatas(aCollageInfo.list);
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
        CacheRequest mCacheRequest = new CacheRequest("collage/collage/list", map, mWaterMarkUpdateCallback);
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
        bindIntent.putExtra("type", DownloadFileService.DOWNLOAD_TYPE_COLLAGE);
        this.startService(bindIntent);
    }

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
                //                String filePath = event.bundle.getString("percent");
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
            case PuTaoConstants.REFRESH_COLLAGE_MANAGEMENT_ACTIVITY:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mManagementAdapter.notifyDataSetChanged();
                    }
                });
                break;
        }
    }
}
