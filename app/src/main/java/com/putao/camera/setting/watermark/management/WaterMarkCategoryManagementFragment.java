
package com.putao.camera.setting.watermark.management;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.google.gson.Gson;
import com.putao.camera.R;
import com.putao.camera.base.BaseFragment;
import com.putao.camera.bean.StickerCategoryInfo;
import com.putao.camera.bean.StickerIconInfo;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.downlad.DownloadFileService;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.http.CacheRequest;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.CommonUtils;
import com.putao.camera.util.Loger;
import com.putao.camera.util.WaterMarkHelper;
import com.putao.widget.pulltorefresh.PullToRefreshBase;
import com.putao.widget.pulltorefresh.PullToRefreshGridView;
import com.sunnybear.library.view.LoadingHUD;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public final class WaterMarkCategoryManagementFragment extends BaseFragment implements AdapterView.OnItemClickListener,
        UpdateCallback<StickerListInfo.PackageInfo>, View.OnClickListener {
    private Button right_btn, back_btn;
    private PullToRefreshGridView mPullRefreshGridView;
    private GridView mGridView;
    private WaterMarkManagementAdapter mManagementAdapter;
    private StickerListInfo aWaterMarkRequestInfo;
    ArrayList<StickerCategoryInfo> mStickerCategoryInfos;
    private LoadingHUD mLoading;

    @Override
    public int doGetContentViewId() {
        return R.layout.fragment_water_mark_category_management;
    }

    @Override
    public void doInitSubViews(View view) {
        mLoading = LoadingHUD.getInstance(getActivity());
        mPullRefreshGridView = (PullToRefreshGridView) view.findViewById(R.id.pull_refresh_grid);
        /*right_btn = (Button) view.findViewById(R.id.right_btn);
        right_btn.setText("已下载");
        back_btn = (Button) view.findViewById(R.id.back_btn);*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void doInitDataes() {
        mGridView = mPullRefreshGridView.getRefreshableView();
        mPullRefreshGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
//                Toast.makeText(WaterMarkCategoryManagementFragment.this, "Pull Down!", Toast.LENGTH_SHORT).show();
                //                new GetDataTask().execute();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
//                Toast.makeText(mContext, "Pull Up!", Toast.LENGTH_SHORT).show();
                //                new GetDataTask().execute();
            }
        });
        mManagementAdapter = new WaterMarkManagementAdapter(mActivity);
        mManagementAdapter.setUpdateCallback(this);
        mGridView.setAdapter(mManagementAdapter);
        mGridView.setOnItemClickListener(this);

        queryWaterMarkList();
    }

    @Override
    public void onResume() {
        super.onResume();
        mManagementAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        StickerListInfo.PackageInfo info = mManagementAdapter.getItem(position);
//        bundle.putInt("id", info.id);
        bundle.putString("id", String.valueOf(info.id));
        bundle.putInt("position", position);
        ActivityHelper.startActivity(mActivity, WaterMarkCategoryDetailActivity.class, bundle);
    }


    @Override
    public void startProgress(StickerListInfo.PackageInfo info, final int position) {
        String path = WaterMarkHelper.getWaterMarkUnzipFilePath();
        startDownloadService(info.download_url, path, position);
    }

    @Override
    public void startActivity(StickerListInfo.PackageInfo info, int position) {

    }

    @Override
    public void delete(StickerListInfo.PackageInfo info, final int position) {
        /*Map<String, String> map = new HashMap<String, String>();
        map.put("id", String.valueOf(info.id));
        MainApplication.getDBServer().deleteWaterMarkCategoryInfo(map);
        mManagementAdapter.notifyDataSetChanged();*/
    }

    @Override
    public void queryDetail(StickerListInfo.PackageInfo info, int position) {


    }

    private void updateProgressPartly(int progress, int position) {
        int firstVisiblePosition = mGridView.getFirstVisiblePosition();
        int lastVisiblePosition = mGridView.getLastVisiblePosition();
        if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
            View view = mGridView.getChildAt(position - firstVisiblePosition);
            if (view.getTag() instanceof WaterMarkManagementAdapter.ViewHolder) {
                WaterMarkManagementAdapter.ViewHolder vh = (WaterMarkManagementAdapter.ViewHolder) view.getTag();
//                vh.download_status_pb.setProgress(progress);
                if (progress > 0 && progress < 100) {
                    vh.water_mark_category_download_btn.setImageResource(R.drawable.btn_22_02);
                    vh.pb_download.setVisibility(View.VISIBLE);
                    vh.water_mark_category_download_btn.setOnClickListener(null);

                } else if (progress == 100) {
                    //mManagementAdapter.notifyDataSetChanged();
                    vh.pb_download.setVisibility(View.GONE);
                    vh.water_mark_category_download_btn.setImageResource(R.drawable.btn_22_03);
                }
            }
        }
    }

    //请求水印列表
    public void queryWaterMarkList() {
        mLoading.show();
        CacheRequest.ICacheRequestCallBack mWaterMarkUpdateCallback = new CacheRequest.ICacheRequestCallBack() {
            @Override
            public void onSuccess(int whatCode, JSONObject json) {
                super.onSuccess(whatCode, json);
//                final StickerListInfo aWaterMarkRequestInfo;

                try {
                    Gson gson = new Gson();
                    aWaterMarkRequestInfo = (StickerListInfo) gson.fromJson(json.toString(), StickerListInfo.class);
                    mManagementAdapter.setDatas(aWaterMarkRequestInfo.data);
                    Gson gson1 = new Gson();
                    mStickerCategoryInfos = gson1.fromJson(json.toString(), StickerIconInfo.class).data;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                mManagementAdapter.notifyDataSetChanged();
                mLoading.dismiss();
            }

            @Override
            public void onFail(int whatCode, int statusCode, String responseString) {
                super.onFail(whatCode, statusCode, responseString);
                mLoading.dismiss();
            }
        };
        HashMap<String, String> map = new HashMap<String, String>();
//        CacheRequest mCacheRequest = new CacheRequest("watermark/watermark/list", map, mWaterMarkUpdateCallback);
        CacheRequest mCacheRequest = new CacheRequest(PuTaoConstants.PAIPAI_MATTER_LIST_PATH + "?type=sticker_pic&page=1", map, mWaterMarkUpdateCallback);
        mCacheRequest.startGetRequest();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.right_btn:
//                Bundle bundle = new Bundle();
//                bundle.putString("source", this.getClass().getName());
//                ActivityHelper.startActivity(mActivity, DownloadFinishActivity.class, bundle);
//                break;
//            case R.id.back_btn:
////                finish();
//                break;
        }
    }

    //开启开始下载服务
    private void startDownloadService(final String url, final String folderPath, final int position) {
        boolean isExistRunning = CommonUtils.isServiceRunning(mActivity, DownloadFileService.class.getName());
        if (isExistRunning) {
            Loger.i("startDownloadService:exist");
            return;
        } else {
            Loger.i("startDownloadService:run");
        }
        if (null == url || null == folderPath) return;
        mStickerCategoryInfos.get(position).type = "sticker";
        Intent bindIntent = new Intent(mActivity, DownloadFileService.class);
        bindIntent.putExtra("item", mStickerCategoryInfos.get(position));
        bindIntent.putExtra("position", position);
        bindIntent.putExtra("url", url);
        bindIntent.putExtra("floderPath", folderPath);
        bindIntent.putExtra("type", DownloadFileService.DOWNLOAD_TYPE_STICKER);
        mActivity.startService(bindIntent);
    }

    //下载贴图包
    public void onEvent(BasePostEvent event) {
        switch (event.eventCode) {
            case PuTaoConstants.DOWNLOAD_FILE_FINISH: {
                Loger.d("DOWNLOAD_FILE_FINISH");
                final int percent = event.bundle.getInt("percent");
                final int position = event.bundle.getInt("position");
                mActivity.runOnUiThread(new Runnable() {
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
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateProgressPartly(percent, position);
                    }
                });
                break;
            }
            case PuTaoConstants.UNZIP_FILE_FINISH:
                mActivity.runOnUiThread(new Runnable() {
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
