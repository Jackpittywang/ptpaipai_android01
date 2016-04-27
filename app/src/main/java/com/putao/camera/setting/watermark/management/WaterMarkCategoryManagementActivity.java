
package com.putao.camera.setting.watermark.management;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.putao.camera.R;
import com.putao.camera.application.MainApplication;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.bean.StickerCategoryInfo;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.db.DatabaseServer;
import com.putao.camera.downlad.DownloadFileService;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.setting.watermark.download.DownloadFinishStickerAdapter;
import com.putao.camera.util.CommonUtils;
import com.putao.camera.util.Loger;
import com.putao.camera.util.WaterMarkHelper;
import com.putao.widget.pulltorefresh.PullToRefreshGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class WaterMarkCategoryManagementActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        UpdateCallback<StickerListInfo.PackageInfo>, View.OnClickListener {
    private Button right_btn, back_btn;
    private PullToRefreshGridView mPullRefreshGridView;
    private GridView mGridView;
    //    private WaterMarkManagementAdapter mManagementAdapter;
    private DownloadFinishStickerAdapter mManagementAdapter;
    private ArrayList<StickerCategoryInfo> list;
    private TextView title_tv, tv_delect_selected,tv_select_all;
    private RelativeLayout rl_empty;
    private LinearLayout choice_ll;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_water_mark_category_management;
    }

    @Override
    public void doInitSubViews(View view) {
        choice_ll=queryViewById(R.id.choice_ll);
        rl_empty=queryViewById(R.id.rl_empty);
        tv_select_all=queryViewById(R.id.tv_select_all);
        tv_delect_selected = queryViewById(R.id.tv_delect_selected);
        title_tv = (TextView) view.findViewById(R.id.title_tv);
        title_tv.setText("贴纸");
        back_btn = queryViewById(R.id.back_btn);
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
//        mGridView = mPullRefreshGridView.getRefreshableView();
//        mPullRefreshGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
//                Toast.makeText(WaterMarkCategoryManagementActivity.this, "Pull Down!", Toast.LENGTH_SHORT).show();
//                //                new GetDataTask().execute();
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
//                Toast.makeText(mContext, "Pull Up!", Toast.LENGTH_SHORT).show();
//                //                new GetDataTask().execute();
//            }
//        });
//        //        TextView tv = new TextView(this);
//        //        tv.setGravity(Gravity.CENTER);
//        //        tv.setText("Empty View, Pull Down/Up to Add Items");
//        //        mPullRefreshGridView.setEmptyView(tv);
//        mManagementAdapter = new WaterMarkManagementAdapter(this);
//        mManagementAdapter.setUpdateCallback(this);
//        mGridView.setAdapter(mManagementAdapter);
//        mGridView.setOnItemClickListener(this);
////        right_btn = (Button) this.findViewById(R.id.right_btn);
////        right_btn.setText("已下载");
//        back_btn = (Button) this.findViewById(R.id.back_btn);
        addOnClickListener(back_btn, tv_delect_selected,tv_select_all);
//        queryWaterMarkList();
    }

    @Override
    public void onResume() {
        super.onResume();
        mGridView = mPullRefreshGridView.getRefreshableView();
        mManagementAdapter = new DownloadFinishStickerAdapter(mActivity);
        mGridView.setAdapter(mManagementAdapter);

        Map<String, String> map = new HashMap<String, String>();
//        map.put("type", WaterMarkCategoryInfo.photo);
        map.put("type", "sticker");
        list = (ArrayList<StickerCategoryInfo>) MainApplication.getDBServer().getStickerCategoryInfoByWhere(map);
        mManagementAdapter.setDatas(list);
        if(list.size()==0){
            rl_empty.setVisibility(View.VISIBLE);
            choice_ll.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Bundle bundle = new Bundle();
//        WaterMarkPackageListInfo.PackageInfo info = mManagementAdapter.getItem(position);
//        StickerListInfo.PackageInfo info = mManagementAdapter.getItem(position);
//        bundle.putString("wid", info.id+"");
//        bundle.putInt("position", position);
//        ActivityHelper.startActivity(this, WaterMarkCategoryDetailActivity.class, bundle);
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
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", String.valueOf(info.id));
        MainApplication.getDBServer().deleteWaterMarkCategoryInfo(map);


        mManagementAdapter.notifyDataSetChanged();
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
                    vh.water_mark_category_download_btn.setOnClickListener(null);
//                    vh.water_mark_category_download_btn.setText("下载中");
//                    vh.download_status_pb.setVisibility(View.VISIBLE);
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
   /* public void queryWaterMarkList() {
        CacheRequest.ICacheRequestCallBack mWaterMarkUpdateCallback = new CacheRequest.ICacheRequestCallBack() {
            @Override
            public void onSuccess(int whatCode, JSONObject json) {
                super.onSuccess(whatCode, json);
                final StickerListInfo aWaterMarkRequestInfo;
                try {
                    Gson gson = new Gson();
                    aWaterMarkRequestInfo = (StickerListInfo) gson.fromJson(json.toString(), StickerListInfo.class);
//                    mManagementAdapter.setDatas(aWaterMarkRequestInfo.data);
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
//        CacheRequest mCacheRequest = new CacheRequest("watermark/watermark/list", map, mWaterMarkUpdateCallback);
        CacheRequest mCacheRequest = new CacheRequest(PuTaoConstants.PAIPAI_MATTER_LIST_PATH + "?type=sticker_pic&page=1", map, mWaterMarkUpdateCallback);
        mCacheRequest.startGetRequest();
    }
*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.tv_select_all:
                for (StickerCategoryInfo stickerCategoryInfo : mManagementAdapter.getDatas()) {
                    stickerCategoryInfo.setChecked(true);
                }
                mManagementAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_delect_selected:
                DatabaseServer dbServer = MainApplication.getDBServer();
                ArrayList<StickerCategoryInfo> datas = new ArrayList<>();
                for (StickerCategoryInfo stickerCategoryInfo : mManagementAdapter.getDatas()) {
                    if (!stickerCategoryInfo.isChecked()) {
                        datas.add(stickerCategoryInfo);
                    } else dbServer.deleteStickerCategoryInfo(stickerCategoryInfo);
                }
                Bundle bundle = new Bundle();
                mManagementAdapter.setDatas(datas);
                Map<String, String> map2 = new HashMap<String, String>();
                map2.put("type", "sticker");
                list = (ArrayList<StickerCategoryInfo>) MainApplication.getDBServer().getStickerCategoryInfoByWhere(map2);
                if(list.size()==0){
                    rl_empty.setVisibility(View.VISIBLE);
                    choice_ll.setVisibility(View.GONE);
                }
                mManagementAdapter.notifyDataSetChanged();
                EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.REFRESH_WATERMARK_MANAGEMENT_ACTIVITY, bundle));
//                MainApplication.getDBServer().deleteStickerCategoryInfo(mDatas.get(position));
//                mDatas.remove(position);
//                notifyDataSetChanged();
//                Bundle bundle = new Bundle();
//                EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.REFRESH_WATERMARK_MANAGEMENT_ACTIVITY, bundle));

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
        if (null == url || null == folderPath) return;
        Intent bindIntent = new Intent(this, DownloadFileService.class);
        bindIntent.putExtra("position", position);
        bindIntent.putExtra("url", url);
        bindIntent.putExtra("floderPath", folderPath);
        bindIntent.putExtra("type", DownloadFileService.DOWNLOAD_TYPE_STICKER);
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
