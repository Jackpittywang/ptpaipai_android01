
package com.putao.camera.setting.watermark.management;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;
import com.putao.camera.R;
import com.putao.camera.base.BaseFragment;
import com.putao.camera.bean.TemplateCategoryInfo;
import com.putao.camera.bean.TemplateIconInfo;
import com.putao.camera.collage.util.CollageHelper;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.downlad.DownloadFileService;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.http.CacheRequest;
import com.putao.camera.util.CommonUtils;
import com.putao.camera.util.Loger;
import com.putao.widget.pulltorefresh.PullToRefreshBase;
import com.putao.widget.pulltorefresh.PullToRefreshGridView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public final class CollageManagementFragment extends BaseFragment implements AdapterView.OnItemClickListener,
        UpdateCallback<TemplateListInfo.PackageInfo>, View.OnClickListener {
    private PullToRefreshGridView mPullRefreshGridView;
    private GridView mGridView;
    private CollageManagementAdapter mManagementAdapter;
    ArrayList<TemplateIconInfo> mTemplateIconInfo;

    @Override
    public int doGetContentViewId() {
        return R.layout.fragment_collage_management;
    }

    @Override
    public void doInitSubViews(View view) {
        mPullRefreshGridView = (PullToRefreshGridView) view.findViewById(R.id.pull_refresh_grid);
    }

    @Override
    public void onResume() {
        super.onResume();
        mManagementAdapter.notifyDataSetChanged();
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
//                Toast.makeText(CollageManagementFragment.this, "Pull Down!", Toast.LENGTH_SHORT).show();
                //                new GetDataTask().execute();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
//                Toast.makeText(mContext, "Pull Up!", Toast.LENGTH_SHORT).show();
                //                new GetDataTask().execute();
            }
        });
        mManagementAdapter = new CollageManagementAdapter(mActivity);
        mManagementAdapter.setUpdateCallback(this);
        mGridView.setAdapter(mManagementAdapter);
        mGridView.setOnItemClickListener(this);

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
    public void startProgress(TemplateListInfo.PackageInfo info, final int position) {
        //保存路径
        String path = CollageHelper.getCollageUnzipFilePath();
        //拼图模板路径
//        String path = CollageHelper.getTemplateUnzipFilePath();
        startDownloadService(info.download_url, path, position);
    }

    @Override
    public void delete(TemplateListInfo.PackageInfo info, final int position) {
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
//                    vh.collage_photo_ok_iv.setVisibility(View.VISIBLE);
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
                final TemplateListInfo aCollageInfo;
                try {
                    Gson gson = new Gson();
                    aCollageInfo = (TemplateListInfo) gson.fromJson(json.toString(), TemplateListInfo.class);
                    mManagementAdapter.setDatas(aCollageInfo.data);

                    Gson gson1 = new Gson();
                    mTemplateIconInfo = gson1.fromJson(json.toString(), TemplateCategoryInfo.class).data;

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
        CacheRequest mCacheRequest = new CacheRequest(PuTaoConstants.PAIPAI_MATTER_LIST_PATH + "?type=template_pic&page=1", map, mWaterMarkUpdateCallback);
        mCacheRequest.startGetRequest();
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.right_btn:
//                Bundle bundle = new Bundle();
//                bundle.putString("source", this.getClass().getName());
//                ActivityHelper.startActivity(mActivity, DownloadFinishActivity.class, bundle);
//                break;
//            case R.id.back_btn:
////                finish();
//                break;
//        }
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
        mTemplateIconInfo.get(position).type="template";
        bindIntent.putExtra("item",mTemplateIconInfo.get(position));
        bindIntent.putExtra("position", position);
        bindIntent.putExtra("url", url);
        bindIntent.putExtra("floderPath", folderPath);
        bindIntent.putExtra("type", DownloadFileService.DOWNLOAD_TYPE_TEMPLATE);
//        bindIntent.putExtra("type", DownloadFileService.DOWNLOAD_TYPE_TEMPLATE);
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
