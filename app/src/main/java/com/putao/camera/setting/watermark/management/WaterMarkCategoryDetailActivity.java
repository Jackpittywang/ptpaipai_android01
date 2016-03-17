
package com.putao.camera.setting.watermark.management;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.putao.camera.R;
import com.putao.camera.application.MainApplication;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.bean.WaterMarkCategoryInfo;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.downlad.DownloadFileService;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.http.CacheRequest;
import com.putao.camera.setting.watermark.bean.WaterMarkPackageDetailInfo;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.CommonUtils;
import com.putao.camera.util.Loger;
import com.putao.camera.util.WaterMarkHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yanglun on 15/4/3.
 */
public class WaterMarkCategoryDetailActivity extends BaseActivity implements View.OnClickListener {
    private Button download_btn;
    private ProgressBar download_status_pb;
    private TextView title_tv;
    private ImageView sample_iv;
    private GridView mGridView;
    private WaterMarkPackageDetailInfo mWaterMarkPackageDetailInfo;
    //    private WaterMarkPackageListInfo.PackageInfo mInfo;
    private ImageAdapter mGridViewAdapter;
    private Button right_btn;
    private TextView description_tv;
    private Button back_btn;
    private int position;
    private String wid;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_water_mark_category_detail;
    }

    @Override
    public void doInitSubViews(View view) {
        back_btn = (Button) this.findViewById(R.id.back_btn);
        title_tv = (TextView) this.findViewById(R.id.title_tv);
        right_btn = (Button) this.findViewById(R.id.right_btn);
        right_btn.setVisibility(View.INVISIBLE);
        sample_iv = (ImageView) this.findViewById(R.id.sample_iv);
        description_tv = (TextView) this.findViewById(R.id.description_tv);
        download_btn = (Button) this.findViewById(R.id.download_btn);
        download_btn.setClickable(false);
        download_status_pb = (ProgressBar) this.findViewById(R.id.download_status_pb);
        mGridView = (GridView) this.findViewById(R.id.grid_view);
        addOnClickListener(download_btn, back_btn);
        EventBus.getEventBus().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getEventBus().unregister(this);
    }

    @Override
    public void doInitData() {
        Intent intent = this.getIntent();
        position = intent.getIntExtra("position", 0);
        wid = intent.getStringExtra("wid");
        title_tv.setText("");
        mGridViewAdapter = new ImageAdapter();
        mGridView.setAdapter(mGridViewAdapter);

        updateDownloadBtn();

        queryWaterMarkDetail();
    }

    private boolean isDownloaded() {
        List<WaterMarkCategoryInfo> list = null;
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", wid);
        try {
            list = MainApplication.getDBServer().getWaterMarkCategoryInfoByWhere(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list.size() > 0)
            return true;
        else
            return false;
    }

    private void updateDownloadBtn() {
        if (isDownloaded()) {
            download_status_pb.setVisibility(View.INVISIBLE);

            download_btn.setVisibility(View.VISIBLE);
            download_btn.setText("删除");
            download_btn.setBackgroundResource(R.drawable.gray_btn_bg_larger_corners);
        } else {
            download_btn.setVisibility(View.VISIBLE);
            download_btn.setText("下载");
            download_btn.setBackgroundResource(R.drawable.red_btn_bg_larger_corners);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download_btn:
                if (!CommonUtils.isExternalStorageMounted()) {
                    showToast("存储卡不可用!");
                    return;
                }
                if (isDownloaded()) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("id", wid);
                    try {
                        MainApplication.getDBServer().deleteWaterMarkCategoryInfo(map);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    updateDownloadBtn();
                    Bundle bundle = new Bundle();
                    EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.REFRESH_WATERMARK_MANAGEMENT_ACTIVITY, bundle));
                } else {
                    download_btn.setVisibility(View.INVISIBLE);
                    download_status_pb.setVisibility(View.VISIBLE);
                    String path = WaterMarkHelper.getWaterMarkUnzipFilePath();
                    startDownloadService(mWaterMarkPackageDetailInfo.attachment_url, path, position);
                }
                break;
            case R.id.back_btn:
                finish();
                break;
            default:
                break;
        }
    }

    public void queryWaterMarkDetail() {
        CacheRequest.ICacheRequestCallBack mWaterMarkUpdateCallback = new CacheRequest.ICacheRequestCallBack() {
            @Override
            public void onSuccess(int whatCode, JSONObject json) {
                super.onSuccess(whatCode, json);
                try {
                    Gson gson = new Gson();
                    mWaterMarkPackageDetailInfo = (WaterMarkPackageDetailInfo) gson.fromJson(json.toString(), WaterMarkPackageDetailInfo.class);
                    download_btn.setClickable(true);
                    mGridViewAdapter.notifyDataSetChanged();
                    title_tv.setText(mWaterMarkPackageDetailInfo.category);
                    description_tv.setText(mWaterMarkPackageDetailInfo.description);
                    Drawable nophoto = BitmapHelper.getLoadingDrawable(sample_iv.getWidth(), sample_iv.getHeight());
                    DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(nophoto)
                            .showImageOnFail(nophoto).cacheInMemory(true).cacheOnDisc(true)
                            .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true).displayer(new RoundedBitmapDisplayer(20)).build();
                    sample_iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    ImageLoader.getInstance().displayImage(mWaterMarkPackageDetailInfo.detail_image, sample_iv, options);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(int whatCode, int statusCode, String responseString) {
                super.onFail(whatCode, statusCode, responseString);
                Loger.d("queryWaterMarkDetail(),onFail():" + whatCode + "," + statusCode + "," + responseString);
            }
        };
        HashMap<String, String> map = new HashMap<String, String>();
        CacheRequest mCacheRequest = new CacheRequest("watermark/watermark/detail/?wid=" + wid, map,
                mWaterMarkUpdateCallback);
        mCacheRequest.startGetRequest();
    }

    private void startDownloadService(final String url, final String floderPath, final int position) {
        boolean isExistRunning = CommonUtils.isServiceRunning(this, DownloadFileService.class.getName());
        if (isExistRunning) {
            Loger.i("startDownloadService:exist");
            return;
        } else {
            Loger.i("startDownloadService:run");
        }
        Intent bindIntent = new Intent(this, DownloadFileService.class);
        bindIntent.putExtra("position", position);
        bindIntent.putExtra("url", url);
        bindIntent.putExtra("floderPath", floderPath);
        bindIntent.putExtra("type", DownloadFileService.DOWNLOAD_TYPE_WATER_MARK);
        this.startService(bindIntent);
    }

    class ImageAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        ImageAdapter() {
            inflater = LayoutInflater.from(mActivity);
        }

        @Override
        public int getCount() {
            if (mWaterMarkPackageDetailInfo == null) {
                return 0;
            } else {
                return mWaterMarkPackageDetailInfo.image_list.size();
            }
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ImageView imageView = (ImageView) convertView;
            if (imageView == null) {
                imageView = (ImageView) inflater.inflate(R.layout.layout_download_detail_grid_item, parent, false);
            }
            DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(BitmapHelper.getLoadingDrawable())
                    .showImageForEmptyUri(BitmapHelper.getLoadingDrawable()).showImageOnFail(BitmapHelper.getLoadingDrawable()).cacheInMemory(true)
                    .cacheOnDisc(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(20)).build();
            ImageLoader.getInstance().displayImage(mWaterMarkPackageDetailInfo.image_list.get(position), imageView, options);
            return imageView;
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
                        updateDownloadBtn();
                    }
                });
                break;
        }
    }

    private void updateProgressPartly(int progress, int position) {
        if (progress >= 0 && progress <= 100) {
            download_status_pb.setProgress(progress);
        }
    }
}
