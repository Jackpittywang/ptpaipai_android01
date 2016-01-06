
package com.putao.camera.voice;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.putao.camera.R;
import com.putao.camera.album.AlbumProcessDialog;
import com.putao.camera.album.adapter.CategoryPhotoAdapter;
import com.putao.camera.album.view.PhotoListItemClickListener;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.bean.PhotoInfo;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.util.PhotoLoaderHelper;

import java.util.ArrayList;
import java.util.Map;

public class VoiceCategoryPhotoListActivity extends BaseActivity implements PhotoListItemClickListener, View.OnClickListener {
    private ListView body_lv;
    private ImageView empty_iv;
    private Map<String, ArrayList<PhotoInfo>> mPhotoMapCategoryByDate;
    private final static int SET_ALBUM_PHOTOS = 1;
    private final static int GET_ALBUM_PHOTOS = 2;
    private Handler mHandler;
    private CategoryPhotoAdapter mCategoryPhotoAdapter;
    //    private boolean bMultiSelectState = false;
    private Button back_btn;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_voice_category_photo_list;
    }

    @Override
    public void doInitSubViews(View view) {
        body_lv = (ListView) this.findViewById(R.id.body_lv);
        back_btn = (Button) this.findViewById(R.id.back_btn);
        addOnClickListener(back_btn);
        empty_iv = (ImageView) this.findViewById(R.id.body_iv_none);
    }

    @Override
    public void doInitData() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SET_ALBUM_PHOTOS:
                        doRefreshAlbumData();
                        break;
                    case GET_ALBUM_PHOTOS:
                        queryLocalPhoto();
                        break;
                    default:
                        break;
                }
            }

            ;
        };
        mCategoryPhotoAdapter = new CategoryPhotoAdapter(mContext);
        mCategoryPhotoAdapter.setPhotoListItemClickListener(this);
        body_lv.setAdapter(mCategoryPhotoAdapter);
        queryLocalPhoto();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }

    void doRefreshAlbumData() {
        //        bMultiSelectState = false;
        //        mCategoryPhotoAdapter.setMultSelectState(bMultiSelectState);
        mCategoryPhotoAdapter.setData(mPhotoMapCategoryByDate);
        mCategoryPhotoAdapter.notifyDataSetChanged();
        //        mPhotoAdapter.notifyDataSetChanged();
        //        btn_album_del.setVisibility(View.GONE);
        //        /**
        //         * 应用相册空时，显示提示
        //         */
        if (mPhotoMapCategoryByDate.size() > 0) {
            empty_iv.setVisibility(View.INVISIBLE);
        } else {
            empty_iv.setVisibility(View.VISIBLE);
        }
    }

    void queryLocalPhoto() {
        final ProgressDialog dialog = new AlbumProcessDialog(mContext, "正在读取拍拍照片...").Get();
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPhotoMapCategoryByDate = PhotoLoaderHelper.getInstance(mActivity).getPhotoMapCategoryByDate(true);
                sendHandleMessage(SET_ALBUM_PHOTOS);
                dialog.dismiss();
            }
        }).start();
    }

    private void sendHandleMessage(int msgCode) {
        Message msg = new Message();
        msg.what = msgCode;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onPhotoListItemClick(PhotoInfo info) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("PhotoInfo", info);
        EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.VOICE_PHOTO_CONTENT_PROVIDER_BACK, bundle));
        this.finish();
    }

    @Override
    public void onPhotoListItemLongClick(PhotoInfo info) {
    }

    @Override
    public void onCheckedChanged(PhotoInfo PhotoInfo, boolean isChecked) {
        mCategoryPhotoAdapter.ChangePhotoCheckedState(PhotoInfo, isChecked);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
//                UmengAnalysisHelper.onEvent(mContext, UmengAnalysisConstants.UMENG_COUNT_EVENT_PHOTO_LIST_BACK.toString());
                finish();
                break;
            default:
                break;
        }
    }
}
