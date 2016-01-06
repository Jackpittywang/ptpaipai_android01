
package com.putao.camera.collage;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.putao.camera.R;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.bean.CollageConfigInfo;
import com.putao.camera.bean.CollageConfigInfo.CollageCategoryInfo;
import com.putao.camera.bean.CollageConfigInfo.CollageItemInfo;
import com.putao.camera.collage.adapter.CollageSampleAdapter;
import com.putao.camera.collage.adapter.ConnectSampleAdapter;
import com.putao.camera.collage.mode.CollageSampleItem;
import com.putao.camera.collage.util.CollageHelper;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.setting.watermark.management.CollageManagementActivity;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.Loger;

import java.util.ArrayList;

/**
 * Created by jidongdong on 15/1/27.
 */
public class CollageSampleSelectActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Button back_btn, right_btn;
    private GridView grid_col_sample;
    private ArrayList<CollageSampleItem> mCollageList;
    private ArrayList<CollageConfigInfo.ConnectImageInfo> mConnectImageList;
    private CollageConfigInfo mCollageConfigInfo;
    private ArrayList<String> selectImages = new ArrayList<String>();
    private CollageSampleAdapter mCollageSampleAdapter;
    private ConnectSampleAdapter mConnectSampleAdapter;
    private LinearLayout btn_photo_collage, btn_photo_join;
    private TextView connect_tv, collage_tv;
    private boolean mIsconnect = false;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_collage_sample_select;
    }

    @Override
    public void doInitSubViews(View view) {
        back_btn = queryViewById(R.id.back_btn);
        grid_col_sample = queryViewById(R.id.grid_col_sample);
        btn_photo_join = queryViewById(R.id.btn_photo_join);
        btn_photo_collage = queryViewById(R.id.btn_photo_collage);
        connect_tv = queryViewById(R.id.connect_tv);
        collage_tv = queryViewById(R.id.collage_tv);
        right_btn = queryViewById(R.id.right_btn);
        addOnClickListener(back_btn, btn_photo_collage, btn_photo_join, right_btn);
        setCollageButtonSelected();
        EventBus.getEventBus().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getEventBus().unregister(this);
    }

    @Override
    public void doInitData() {
        mCollageConfigInfo = CollageHelper.getCollageConfigInfoFromDB(mContext);
        initCollageGrid();
    }

    @SuppressWarnings("unchecked")
    private void initCollageGrid() {
        if (mCollageConfigInfo == null) {
            return;
        }
        mCollageList = getCollageSampleList();
        mConnectImageList = getConnectImageList();
        mCollageSampleAdapter = new CollageSampleAdapter(mContext, mCollageList);
        mConnectSampleAdapter = new ConnectSampleAdapter(mContext, mConnectImageList);
        grid_col_sample.setAdapter(mCollageSampleAdapter);
        grid_col_sample.setOnItemClickListener(this);
        mCollageSampleAdapter.notifyDataSetChanged();
        //Loger.d("chen+++mCollageList.size()=" + mCollageList.size());
    }

    ArrayList<CollageConfigInfo.ConnectImageInfo> getConnectImageList() {
        if (mCollageConfigInfo == null) {
            return null;
        }
        return mCollageConfigInfo.content.connect_image;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("images", selectImages);
        if (mIsconnect) {
            CollageConfigInfo.ConnectImageInfo i_info = mConnectImageList.get(position);
            if (i_info != null) {
                bundle.putSerializable("sampleinfo", i_info);
            } else {
            }
        } else {
            CollageSampleItem c_info = mCollageList.get(position);
            if (c_info != null) {
                bundle.putSerializable("sampleinfo", c_info);
            } else {
            }
        }
        bundle.putBoolean("isconnect", mIsconnect);
        ActivityHelper.startActivity(mActivity, CollagePhotoSelectActivity.class, bundle);
    }

    private ArrayList<CollageSampleItem> getCollageSampleList() {
        if (mCollageConfigInfo == null) {
            return null;
        }
        ArrayList<CollageSampleItem> list = new ArrayList<CollageSampleItem>();
        for (int i = 0; i < mCollageConfigInfo.content.collage_image.size(); i++) {
            CollageCategoryInfo categoryInfo = mCollageConfigInfo.content.collage_image.get(i);
            if (categoryInfo.elements.size() > 0) {
                for (CollageItemInfo item : categoryInfo.elements) {
                    list.add(new CollageSampleItem(item, categoryInfo.category));
                }
            }
        }
        return list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.btn_photo_collage:
                right_btn.setVisibility(View.VISIBLE);
                mIsconnect = false;
                setCollageButtonSelected();
                grid_col_sample.setAdapter(mCollageSampleAdapter);
                mCollageSampleAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_photo_join:
                right_btn.setVisibility(View.INVISIBLE);
                mIsconnect = true;
                setCollageButtonSelected();
                grid_col_sample.setAdapter(mConnectSampleAdapter);
                mConnectSampleAdapter.notifyDataSetChanged();
                break;
            case R.id.right_btn:
                ActivityHelper.startActivity(mActivity, CollageManagementActivity.class);
                break;
            default:
                break;
        }
    }

    void setCollageButtonSelected() {
        if (mIsconnect) {
            connect_tv.setTextColor(getResources().getColor(R.color.text_color_red));
            collage_tv.setTextColor(getResources().getColor(R.color.text_color_dark_898989));
        } else {
            connect_tv.setTextColor(getResources().getColor(R.color.text_color_dark_898989));
            collage_tv.setTextColor(getResources().getColor(R.color.text_color_red));
        }
    }

    public void onEvent(BasePostEvent event) {
        switch (event.eventCode) {
            case PuTaoConstants.FINISH_TO_MENU_PAGE:
                finish();
                break;
            case PuTaoConstants.UNZIP_FILE_FINISH:

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doInitData();
                    }
                });
                break;
            default:
                break;
        }
    }
}
