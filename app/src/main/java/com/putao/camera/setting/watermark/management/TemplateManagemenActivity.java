
package com.putao.camera.setting.watermark.management;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.putao.camera.R;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.bean.TemplateCategoryInfo;
import com.putao.camera.bean.TemplateIconInfo;
import com.putao.camera.collage.CollageMakeActivity;
import com.putao.camera.collage.CollagePhotoSelectActivity;
import com.putao.camera.collage.mode.CollageSampleItem;
import com.putao.camera.collage.util.CollageHelper;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.downlad.DownloadFileService;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.http.CacheRequest;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.CommonUtils;
import com.putao.camera.util.Loger;
import com.putao.camera.util.ToasterHelper;
import com.putao.widget.pulltorefresh.PullToRefreshBase;
import com.putao.widget.pulltorefresh.PullToRefreshGridView;
import com.sunnybear.library.view.LoadingHUD;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public final class TemplateManagemenActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        UpdateCallback<TemplateListInfo.PackageInfo>, View.OnClickListener {
    private PullToRefreshGridView mPullRefreshGridView;
    private GridView mGridView;
    private TextView title_tv;
    private Button right_btn, back_btn;
    private CollageManagementAdapter mManagementAdapter;
    private ArrayList<String> selectImages = new ArrayList<String>();
    ArrayList<TemplateIconInfo> mTemplateIconInfo;
    TemplateIconInfo templateIconInfo;
    private int imageTotal = 0;
    private LoadingHUD mLoading;
    private RelativeLayout rl_empty;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_template_management;
    }

    @Override
    public void doInitSubViews(View view) {
        mLoading = LoadingHUD.getInstance(this);
        EventBus.getEventBus().register(this);
        rl_empty= (RelativeLayout) view.findViewById(R.id.rl_empty);
        mPullRefreshGridView = (PullToRefreshGridView) view.findViewById(R.id.pull_refresh_grid);
        right_btn=queryViewById(R.id.right_btn);
        back_btn = queryViewById(R.id.back_btn);
        title_tv = queryViewById(R.id.title_tv);
        title_tv.setText("选择模板");
        right_btn.setText("返回");
    }


    @Override
    public void onResume() {
        super.onResume();
        mManagementAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getEventBus().unregister(this);
    }

    @Override
    public void doInitData() {

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

        addOnClickListener(back_btn,right_btn);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            selectImages=(ArrayList<String>) bundle.getSerializable("images");
            imageTotal = bundle.getInt("imgsum");
            templateIconInfo = (TemplateIconInfo) bundle.getSerializable("sampleinfo");

            back_btn.setVisibility(View.GONE);
            right_btn.setVisibility(View.VISIBLE);
        }else {
            back_btn.setVisibility(View.VISIBLE);
            right_btn.setVisibility(View.GONE);
        }

        queryCollageList(imageTotal);
    }

    private ArrayList<CollageSampleItem> mCollageList;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*Bundle bundle = new Bundle();
                WaterMarkPackageListInfo.PackageInfo info = mManagementAdapter.getItem(position);
                bundle.putSerializable("info", info);
                ActivityHelper.startActivity(this, WaterMarkCategoryDetailActivity.class, bundle);*/
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
    public void startActivity(TemplateListInfo.PackageInfo info, int position) {
        Bundle bundle = new Bundle();
        if (imageTotal!=0) {
            bundle.putSerializable("images", selectImages);
            bundle.putSerializable("sampleinfo", mTemplateIconInfo.get(position));
            ActivityHelper.startActivity(mActivity, CollageMakeActivity.class, bundle);
            finish();
        }else {
            bundle.putSerializable("images", selectImages);
            bundle.putSerializable("sampleinfo", mTemplateIconInfo.get(position));
            ActivityHelper.startActivity(mActivity, CollagePhotoSelectActivity.class, bundle);

        }


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
                if (progress > 0 && progress < 100) {
                    vh.pb_download.setVisibility(View.VISIBLE);
                    vh.collage_photo_download_iv.setVisibility(View.GONE);
                    vh.collage_photo_download_iv.setOnClickListener(null);
                } else if (progress == 100) {
                    vh.pb_download.setVisibility(View.GONE);
                    vh.collage_photo_download_iv.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    public void queryCollageList(final int imgSum) {
        mLoading.show();
        CacheRequest.ICacheRequestCallBack mWaterMarkUpdateCallback = new CacheRequest.ICacheRequestCallBack() {
            @Override
            public void onSuccess(int whatCode, JSONObject json) {
                super.onSuccess(whatCode, json);
                final TemplateListInfo aCollageInfo;
                try {
                    Gson gson = new Gson();
                    aCollageInfo = (TemplateListInfo) gson.fromJson(json.toString(), TemplateListInfo.class);
                    Gson gson1 = new Gson();
                    mTemplateIconInfo = gson1.fromJson(json.toString(), TemplateCategoryInfo.class).data;
                    if (imgSum == 0) {
                        mManagementAdapter.setDatas(aCollageInfo.data);

                    } else {
                        ArrayList<TemplateListInfo.PackageInfo> packageInfos=new ArrayList<>();
                        ArrayList<TemplateIconInfo> newTemplateIconInfo=new ArrayList<>();
                        for (TemplateListInfo.PackageInfo iconInfo : aCollageInfo.data) {
                            if (iconInfo.max_num >=imgSum) {
                                packageInfos.add(iconInfo);
                            }
                        }
                        mManagementAdapter.setDatas(packageInfos);

                        for(TemplateIconInfo templateIconInfo:mTemplateIconInfo){
                            if (Integer.parseInt(templateIconInfo.num) >= imgSum) {
                                newTemplateIconInfo.add(templateIconInfo);
                            }

                        }
                        mTemplateIconInfo=newTemplateIconInfo;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mManagementAdapter.notifyDataSetChanged();
                mLoading.dismiss();
            }

            @Override
            public void onFail(int whatCode, int statusCode, String responseString) {
                super.onFail(whatCode, statusCode, responseString);
                ToasterHelper.showShort(TemplateManagemenActivity.this, "网络不太给力", R.drawable.img_blur_bg);
                rl_empty.setVisibility(View.VISIBLE);
                mPullRefreshGridView.setVisibility(View.GONE);

            }
        };
        HashMap<String, String> map = new HashMap<String, String>();
        CacheRequest mCacheRequest = new CacheRequest(PuTaoConstants.PAIPAI_MATTER_LIST_PATH + "?type=template_pic&page=1", map, mWaterMarkUpdateCallback);
        mCacheRequest.startGetRequest();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.right_btn:
                Bundle bundle = new Bundle();
                bundle.putSerializable("images",selectImages);
                 bundle.putSerializable("sampleinfo",templateIconInfo);
                ActivityHelper.startActivity(mActivity, CollageMakeActivity.class, bundle);
                finish();
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
        if (null == url || null == folderPath) return;
        Intent bindIntent = new Intent(mActivity, DownloadFileService.class);
        mTemplateIconInfo.get(position).type = "template";
        bindIntent.putExtra("item", mTemplateIconInfo.get(position));
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
                mActivity.runOnUiThread(new Runnable() {
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
            case PuTaoConstants.REFRESH_COLLAGE_MANAGEMENT_ACTIVITY:
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mManagementAdapter.notifyDataSetChanged();
                    }
                });
                break;
        }
    }
}
