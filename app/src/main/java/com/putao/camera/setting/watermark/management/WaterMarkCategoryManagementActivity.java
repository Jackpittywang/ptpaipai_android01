
package com.putao.camera.setting.watermark.management;

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
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.setting.watermark.download.DownloadFinishStickerAdapter;
import com.putao.camera.util.Loger;
import com.putao.widget.pulltorefresh.PullToRefreshGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class WaterMarkCategoryManagementActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        UpdateCallback<StickerListInfo.PackageInfo>, View.OnClickListener {
    private Button right_btn, back_btn;
    private PullToRefreshGridView mPullRefreshGridView;
    private GridView mGridView;
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

    }

    @Override
    public void startProgress(StickerListInfo.PackageInfo info, final int position) {
    }

    @Override
    public void startActivity(StickerListInfo.PackageInfo info, int position) {

    }

    @Override
    public void delete(StickerListInfo.PackageInfo info, final int position) {
      /*  Map<String, String> map = new HashMap<String, String>();
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
