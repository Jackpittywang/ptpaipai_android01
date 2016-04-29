
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
import com.putao.camera.bean.TemplateIconInfo;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.db.DatabaseServer;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.setting.watermark.download.DownloadFinishedTemplateAdapter;
import com.putao.camera.util.Loger;
import com.putao.widget.pulltorefresh.PullToRefreshGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class CollageManagementActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        UpdateCallback<TemplateListInfo.PackageInfo>, View.OnClickListener {
    private Button right_btn, back_btn;
    private PullToRefreshGridView mPullRefreshGridView;
    private GridView mGridView;
    private DownloadFinishedTemplateAdapter mManagementAdapter;
    private TextView title_tv,tv_delect_selected,tv_select_all;
    private ArrayList<TemplateIconInfo> list;
    private RelativeLayout rl_empty;
    private LinearLayout choice_ll;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_collage_management;
    }

    @Override
    public void doInitSubViews(View view) {
        choice_ll=queryViewById(R.id.choice_ll);
        rl_empty=queryViewById(R.id.rl_empty);
        tv_select_all=queryViewById(R.id.tv_select_all);
        tv_delect_selected=queryViewById(R.id.tv_delect_selected);
        title_tv = (TextView) view.findViewById(R.id.title_tv);
        title_tv.setText("拼图模板");
        mPullRefreshGridView = (PullToRefreshGridView) view.findViewById(R.id.pull_refresh_grid);
        back_btn = (Button) view.findViewById(R.id.back_btn);
        EventBus.getEventBus().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mGridView = mPullRefreshGridView.getRefreshableView();
        mManagementAdapter = new DownloadFinishedTemplateAdapter(mActivity);
        mGridView.setAdapter(mManagementAdapter);

        Map<String, String> map = new HashMap<String, String>();
          map.put("type", "template");
        list = (ArrayList<TemplateIconInfo>) MainApplication.getDBServer().getTemplateIconInfoByWhere(map);
        mManagementAdapter.setDatas(list);
        if(list.size()==0){
            rl_empty.setVisibility(View.VISIBLE);
            choice_ll.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getEventBus().unregister(this);
    }

    @Override
    public void doInitData() {

        addOnClickListener( back_btn,tv_delect_selected,tv_select_all);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }


    @Override
    public void startProgress(TemplateListInfo.PackageInfo info, final int position) {

    }

    @Override
    public void startActivity(TemplateListInfo.PackageInfo info, int position) {

    }

    @Override
    public void delete(TemplateListInfo.PackageInfo info, final int position) {
    }

    @Override
    public void queryDetail(TemplateListInfo.PackageInfo info, int position) {

    }

    private void updateProgressPartly(int progress, int position) {
        int firstVisiblePosition = mGridView.getFirstVisiblePosition();
        int lastVisiblePosition = mGridView.getLastVisiblePosition();
        if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
            View view = mGridView.getChildAt(position - firstVisiblePosition);
            if (view.getTag() instanceof CollageManagementAdapter.ViewHolder) {
                CollageManagementAdapter.ViewHolder vh = (CollageManagementAdapter.ViewHolder) view.getTag();
//                vh.download_status_pb.setProgress(progress);
                if (progress > 0 && progress < 100) {
//                    vh.download_status_pb.setVisibility(View.VISIBLE);
                } else if (progress == 100) {
//                    vh.download_status_pb.setVisibility(View.INVISIBLE);
                    vh.collage_photo_ok_iv.setVisibility(View.VISIBLE);
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
                for (TemplateIconInfo templateIconInfo : mManagementAdapter.getDatas()) {
                    templateIconInfo.setChecked(true);
                }
                mManagementAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_delect_selected:
                DatabaseServer dbServer = MainApplication.getDBServer();
                ArrayList<TemplateIconInfo> datas = new ArrayList<>();
                for (TemplateIconInfo templateIconInfo : mManagementAdapter.getDatas()) {
                    if (!templateIconInfo.isChecked()) {
                        datas.add(templateIconInfo);
                    } else dbServer.deleteTemplateIconInfo(templateIconInfo);
                }
                Bundle bundle = new Bundle();
                mManagementAdapter.setDatas(datas);
                Map<String, String> map = new HashMap<String, String>();
                map.put("type", "template");
                list = (ArrayList<TemplateIconInfo>) MainApplication.getDBServer().getTemplateIconInfoByWhere(map);
                if(list.size()==0){
                    rl_empty.setVisibility(View.VISIBLE);
                    choice_ll.setVisibility(View.GONE);
                }
                mManagementAdapter.notifyDataSetChanged();
                EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.REFRESH_COLLAGE_MANAGEMENT_ACTIVITY, bundle));

                break;
        }
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

                        mManagementAdapter.notifyDataSetChanged();

                break;
        }
    }
}
