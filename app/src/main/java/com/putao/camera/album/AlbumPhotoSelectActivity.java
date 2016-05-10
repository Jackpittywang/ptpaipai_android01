
package com.putao.camera.album;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.putao.camera.R;
import com.putao.camera.album.adapter.AlbumGridAdapter;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.camera.filter.CustomerFilter;
import com.putao.camera.collage.adapter.GalleryListAdapter;
import com.putao.camera.collage.mode.GalleryEntity;
import com.putao.camera.collage.mode.PhotoGridItem;
import com.putao.camera.collage.util.CollagePhotoUtil;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.editor.PhotoEditorActivity;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.movie.MovieCameraActivity;
import com.putao.camera.movie.MoviePhotoCutActivity;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.Loger;
import com.putao.camera.util.PhotoLoaderHelper;
import com.putao.widget.stickygridheaders.StickyGridHeadersGridView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AlbumPhotoSelectActivity extends BaseActivity implements View.OnClickListener {
    private Button back_btn, right_btn;
    private ListView layout_gallery_list;
    private final static int SetAlbumPhotos = 1;
    private final static int SetGalleryList = 2;
    private StickyGridHeadersGridView mGridView;
    private List<PhotoGridItem> mGirdList = new ArrayList<PhotoGridItem>();
    private LinearLayout sl_gallery_list;
    private boolean mGalleryShow = false;
    ArrayList<GalleryEntity> mGalleryList;
    private RelativeLayout gallery_list_panel;
    private TextView title_tv, back_tv;
    private boolean multiSelectState = false;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SetAlbumPhotos:
                    LoadAlbumData();
                    break;
                case SetGalleryList:
                    loadGalleryList();
                    break;
                default:
                    break;
            }
        }
    };
    private AlbumGridAdapter mAlbumGridAdapter;
    private boolean isFromMovie;
    boolean isFromConnectPhoto = false;
    boolean from_collage_photo = false;
    private LinearLayout nonePhotoView;
    private RelativeLayout grid_rl;
    //    private ImageView body_iv_none_camera;
    private ImageView iv_icon;

    private int bucket_id = -1;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_album_photo_select;
    }

    @Override
    public void doInitSubViews(View view) {
        back_tv = queryViewById(R.id.back_tv);
        iv_icon = queryViewById(R.id.iv_icon);
        iv_icon.setImageResource(R.drawable.btn_nav_spread_down);
        nonePhotoView = (LinearLayout) this.findViewById(R.id.body_iv_none);
       /* body_iv_none_camera = (ImageView) this.findViewById(R.id.body_iv_none_camera);
        body_iv_none_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityHelper.startActivity(mActivity, ActivityCamera.class);
                finish();
              *//*  Bundle bundle = new Bundle();
                bundle.putString("reason", "edit");
                ActivityHelper.startActivity(mActivity, MovieCameraActivity.class, bundle);*//*
            }
        });*/
        mGridView = (StickyGridHeadersGridView) findViewById(R.id.asset_grid);
        back_btn = (Button) this.findViewById(R.id.back_btn);
        title_tv = (TextView) this.findViewById(R.id.title_tv);
        title_tv.setText("相机胶卷");
        right_btn = (Button) findViewById(R.id.right_btn);
        grid_rl = (RelativeLayout) findViewById(R.id.grid_rl);
        layout_gallery_list = (ListView) findViewById(R.id.layout_gallery_list);
        sl_gallery_list = (LinearLayout) findViewById(R.id.sl_gallery_list);
        gallery_list_panel = (RelativeLayout) findViewById(R.id.gallery_list_panel);
        gallery_list_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGalleryShow) {
                    hideGalleryLsit();
                }
            }
        });
        gallery_list_panel.setVisibility(View.GONE);
        addOnClickListener(back_btn, right_btn, title_tv, back_tv);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = mGirdList.get(position).getPath();
                if (isMultiSelectState()) {
                    if (CollagePhotoUtil.IS_CAMERA_ICON.equals(path)) {
                        return;
                    }
                    PhotoGridItem item = mGirdList.get(position);
                    if (item.isSelected()) {
                        item.setSelected(false);
                    } else {
                        item.setSelected(true);
                    }
                    mAlbumGridAdapter.notifyDataSetChanged();
                } else {

                    OnItemClickFinish(path);
                }
            }
        });
/*       mGridView.setLongClickable(true);
        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isMultiSelectState()) {
                    setMultiSelectState(true);
                } else {
                    setMultiSelectState(false);
                }
                return true;
            }
        });*/
        mGridView.setHeadersIgnorePadding(true);
        EventBus.getEventBus().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getEventBus().unregister(this);
    }

    protected void OnItemClickFinish(String path) {
        if (path.startsWith(CollagePhotoUtil.IS_CAMERA_ICON)) {
            Bundle bundle = new Bundle();
            if (isFromConnectPhoto) {
                bundle.putString("reason", "connect_photo");
                ActivityHelper.startActivity(mActivity, MovieCameraActivity.class, bundle);
                finish();
            } else if (from_collage_photo) {
                bundle.putString("reason", "collage_photo");
                ActivityHelper.startActivity(mActivity, MovieCameraActivity.class, bundle);
                finish();
            } else {
                bundle.putString("reason", "edit");
                ActivityHelper.startActivity(mActivity, MovieCameraActivity.class, bundle);
            }
        } else if (isFromMovie) {
            // 跳转到movie剪切
            Intent intent = new Intent(this, MoviePhotoCutActivity.class);
            intent.putExtra("photo_data", path);
            this.startActivity(intent);
        } else if (isFromConnectPhoto) {
            Bundle bundle = new Bundle();
            bundle.putString("photo_path", path);
            EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.EVENT_CONNECT_PHOTO_SELECT, bundle));
            finish();
        } else if (from_collage_photo) {
            Bundle bundle = new Bundle();
            bundle.putString("photo_path", path);
            EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.EVENT_COLLAGE_PHOTO_SELECT, bundle));
            finish();
        } else {// 图片编辑
            Intent intent = new Intent(this, PhotoEditorActivity.class);
            intent.putExtra("filterName", CustomerFilter.FilterType.NONE);
            intent.putExtra("photo_data", path);
            intent.putExtra("from", "album");
            this.startActivity(intent);
            finish();
        }
    }

    @Override
    public void doInitData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isFromMovie = bundle.getBoolean("from_movie", false);
            isFromConnectPhoto = bundle.getBoolean("from_connect_photo", false);
            from_collage_photo = bundle.getBoolean("from_collage_photo", false);
        }
//        getData();
        getGallery();
    }

    @Override
    protected void onRestart() {
        reSetAlbumData();
        super.onRestart();
    }

    void getGallery() {
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("正在读取照片...");
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mGalleryList = CollagePhotoUtil.QueryALLGalleryList(mContext);
                sendHandleMessage(SetGalleryList);
                for (int i = 0; i < mGalleryList.size(); i++) {
                    GalleryEntity item = mGalleryList.get(i);
                    if (item.getImage_path().contains(PuTaoConstants.PAIAPI_PHOTOS_FOLDER)) {
                        bucket_id = item.getBucket_id();
                    }
                }
                dialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        reSetAlbumData();
                    }
                });
            }
        }).start();
    }

    void reSetAlbumData() {
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("正在更新相册照片...");
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Loger.d("chen+++bucket_id=" + bucket_id);
                if (bucket_id != -1) {
                    mGirdList = CollagePhotoUtil.QueryPhotoByBUCKET_ID(mContext, bucket_id);
                } else {
                    mGirdList = CollagePhotoUtil.QueryALLPhoto(mContext);
                }
                sendHandleMessage(SetAlbumPhotos);
                dialog.dismiss();
            }
        }).start();
    }

    void loadGalleryList() {
        GalleryListAdapter galleryAdapter = new GalleryListAdapter(mContext, mGalleryList);
        layout_gallery_list.setAdapter(galleryAdapter);
        layout_gallery_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GalleryEntity entity = mGalleryList.get(position);
                bucket_id = entity.getBucket_id();
                title_tv.setText(entity.getBucket_name());
                reSetAlbumData();
                hideGalleryLsit();
            }
        });
    }

    void LoadAlbumData() {
        mAlbumGridAdapter = new AlbumGridAdapter(mActivity, mGirdList, mGridView);
        mGridView.setAdapter(mAlbumGridAdapter);
        //至少有一个相机的Icon
        if (mGirdList.size() > 0) {
            nonePhotoView.setVisibility(View.INVISIBLE);
            mGridView.setVisibility(View.VISIBLE);
        } else {
            nonePhotoView.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.INVISIBLE);
        }
    }

    void getData() {
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("正在读取照片...");
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mGirdList = CollagePhotoUtil.QueryALLPhoto(mContext);
                sendHandleMessage(SetAlbumPhotos);
                dialog.dismiss();
            }
        }).start();
    }

    private void sendHandleMessage(int messagecode) {
        Message msg = new Message();
        msg.what = messagecode;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.back_tv:
                finish();
                break;
            /*case R.id.right_btn:
                if (!isMultiSelectState()) {
                    if (mGalleryShow) {
                        hideGalleryLsit();
                    } else {
                        showGalleryList();
                    }
                } else {
                    deleteSelectedAlbumPhotos();
                }
                break;*/

            case R.id.title_tv:
                if (mGalleryShow) {
                    iv_icon.setImageResource(R.drawable.btn_nav_spread_down);
                    hideGalleryLsit();
                } else {
                    iv_icon.setImageResource(R.drawable.btn_nav_spread_up);
                    showGalleryList();
                }
            default:
                break;
        }
    }

    private void blur(Bitmap bkg, View view) {
        long startMs = System.currentTimeMillis();
        float scaleFactor = 32;
        float radius = 4;
        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor), (int) (view.getMeasuredHeight() / scaleFactor),
                Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);
        canvas.drawColor(0x99ffffff);
        overlay = com.putao.camera.util.FastBlur.doBlur(overlay, (int) radius, true);
        // view.setBackground(new BitmapDrawable(getResources(), overlay));
        view.setBackgroundDrawable(new BitmapDrawable(getResources(), overlay));
        //        Loger.d("blur()++++++time=" + (System.currentTimeMillis() - startMs) + "ms");
    }

    private void applyBlur() {
        grid_rl.setDrawingCacheEnabled(true);
        grid_rl.buildDrawingCache();
        final Bitmap bmp = grid_rl.getDrawingCache();
        sl_gallery_list.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                sl_gallery_list.getViewTreeObserver().removeOnPreDrawListener(this);
                blur(bmp, sl_gallery_list);
                grid_rl.setDrawingCacheEnabled(false);
                return true;
            }
        });
    }

    void showGalleryList() {
        applyBlur();
        gallery_list_panel.setBackgroundColor(0x66000000);
//        ObjectAnimator.ofFloat(sl_gallery_list, "translationX", 0, sl_gallery_list.getWidth()).setDuration(10).start();
        sl_gallery_list.setVisibility(View.VISIBLE);
        gallery_list_panel.setVisibility(View.VISIBLE);
//        ObjectAnimator.ofFloat(sl_gallery_list, "translationX", sl_gallery_list.getWidth(), 0).setDuration(300).start();
        ObjectAnimator.ofFloat(sl_gallery_list, "translationY", -sl_gallery_list.getHeight(), 0).setDuration(300).start();
        mGalleryShow = true;
    }

    void hideGalleryLsit() {
        gallery_list_panel.setBackgroundColor(0x00000000);
//        ObjectAnimator.ofFloat(sl_gallery_list, "translationX", 0, sl_gallery_list.getWidth()).setDuration(300).start();
        ObjectAnimator.ofFloat(sl_gallery_list, "translationY", 0, -sl_gallery_list.getHeight()).setDuration(300).start();
        mGalleryShow = false;
        gallery_list_panel.postDelayed(new Runnable() {
            @Override
            public void run() {
                gallery_list_panel.setVisibility(View.GONE);
            }
        }, 550);
    }

    public boolean isMultiSelectState() {
        return multiSelectState;
    }

    private void reSetRightBtn(boolean multiSelectState) {
        if (multiSelectState) {
            right_btn.setText("删除");
            right_btn.setBackgroundResource(R.drawable.red_btn_bg);
        } else {
            right_btn.setText("");
            right_btn.setBackgroundResource(R.drawable.icon_album_group);
        }
    }

    public void setMultiSelectState(boolean multiSelectState) {
        this.multiSelectState = multiSelectState;
        reSetRightBtn(multiSelectState);
        mAlbumGridAdapter.setMultiSelect(multiSelectState);
    }

    void deleteSelectedAlbumPhotos() {
        if (mGirdList.size() <= 0) {
            return;
        }
        final ProgressDialog showdialog = new AlbumProcessDialog(mContext, "正在删除照片...").Get();
        showdialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = mGirdList.size() - 1; i >= 0; i--) {
                    PhotoGridItem info = mGirdList.get(i);
                    if (info.isSelected()) {
                        String filePath = info.getPath();
                        String id = info.getId();
                        boolean isFileDelete = new File(filePath).delete();
                        int isFileDbDeltet = PhotoLoaderHelper.DeleteFileFromDB(id);
                        if (!isFileDelete || isFileDbDeltet == -1) {
                            Loger.d("删除" + filePath + "失败");
                        }
                        mGirdList.remove(i);
                    }
                }
                showdialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        setMultiSelectState(false);
                    }
                });
                sendHandleMessage(SetAlbumPhotos);
            }
        }).start();
    }

    public void onEvent(BasePostEvent event) {
        switch (event.eventCode) {
            case PuTaoConstants.FINISH_TO_MENU_PAGE:
                finish();
                break;
        }
    }
}
