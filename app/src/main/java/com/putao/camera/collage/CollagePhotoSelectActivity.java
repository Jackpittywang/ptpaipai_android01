package com.putao.camera.collage;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.putao.camera.R;
import com.putao.camera.album.AlbumProcessDialog;
import com.putao.camera.album.adapter.AlbumGridAdapter;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.bean.CollageConfigInfo;
import com.putao.camera.bean.TemplateIconInfo;
import com.putao.camera.camera.ActivityCamera;
import com.putao.camera.collage.adapter.GalleryListAdapter;
import com.putao.camera.collage.mode.CollageSampleItem;
import com.putao.camera.collage.mode.GalleryEntity;
import com.putao.camera.collage.mode.PhotoGridItem;
import com.putao.camera.collage.util.CollagePhotoUtil;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.constants.UmengAnalysisConstants;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.movie.MovieCameraActivity;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.Loger;
import com.putao.camera.util.ToasterHelper;
import com.putao.widget.stickygridheaders.StickyGridHeadersGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jidongdong on 15/1/27.
 */
public class CollagePhotoSelectActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout nonePhotoView;
    private Button back_btn, btn_ok, btn_gallery;
    private LinearLayout select_image_contanier, jigsaw_photo_selected,ll_ok;
    private ListView layout_gallery_list;
    private ArrayList<String> selectImages = new ArrayList<String>();
    private final static int SetAlbumPhotos = 1;
    private final static int SetGalleryList = 2;
    private final static int ImageSum = 3;
    private StickyGridHeadersGridView mGridView;
    private List<PhotoGridItem> mGirdList = new ArrayList<PhotoGridItem>();
    private TextView tv_photo_num,sum;
    private  ImageView iv_icon;
    private int maxnum = 1;
    private CollageSampleItem mSampleInfo;
    private CollageConfigInfo.ConnectImageInfo mConnectSample;
    private TemplateIconInfo mTemplateIconInfo;
    private LinearLayout sl_gallery_list;
    private boolean mGalleryShow = false;
    ArrayList<GalleryEntity> mGalleryList;
    private RelativeLayout gallery_list_panel;
    private boolean mIsconnect = false;
    private int bucket_id = -1;

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
                case ImageSum:
                    sum.setText(selectImages.size()+"");
//                    loadGalleryList();
                    break;
                default:
                    break;
            }
        }
    };
    private TextView title_tv;
    private RelativeLayout grid_rl;
    private ImageView body_iv_none_camera;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_photo_select;
    }

    @Override
    public void doInitSubViews(View view) {
        sum= queryViewById(R.id.sum);
        iv_icon=queryViewById(R.id.iv_icon);
        iv_icon.setImageResource(R.drawable.btn_nav_spread_down);
        mGridView = queryViewById(R.id.asset_grid);
        body_iv_none_camera = queryViewById(R.id.body_iv_none_camera);
        body_iv_none_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityHelper.startActivity(mActivity, ActivityCamera.class);
            }
        });
        nonePhotoView = queryViewById(R.id.body_iv_none);
        back_btn = queryViewById(R.id.back_btn);
        title_tv = queryViewById(R.id.title_tv);
        title_tv.setText("相机胶卷");
        /*btn_gallery = queryViewById(R.id.right_btn);
        btn_gallery.setBackgroundResource(R.drawable.icon_album_group);*/
        grid_rl = (RelativeLayout) findViewById(R.id.grid_rl);
        ll_ok=queryViewById(R.id.ll_ok);
//        btn_ok = queryViewById(R.id.btn_ok);
        tv_photo_num = queryViewById(R.id.tv_photo_num);
        select_image_contanier = queryViewById(R.id.select_image_contanier);
        jigsaw_photo_selected = queryViewById(R.id.jigsaw_photo_selected);
        layout_gallery_list = queryViewById(R.id.layout_gallery_list);
        sl_gallery_list = queryViewById(R.id.sl_gallery_list);
        gallery_list_panel = queryViewById(R.id.gallery_list_panel);
        gallery_list_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGalleryShow) {
                    hideGalleryLsit();
                }
            }
        });
        gallery_list_panel.setVisibility(View.GONE);
        addOnClickListener(back_btn, title_tv,ll_ok);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ((position < 0) || (mGirdList.size() == 0)) {
                    // MI 2SC java.lang.ArrayIndexOutOfBoundsException: length=12; index=-2
                    return;
                }
                Loger.d("select image path:" + mGirdList.get(position).getPath());

                if (mGirdList.get(position).getPath().startsWith(CollagePhotoUtil.IS_CAMERA_ICON)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("reason", "collage");
                    ActivityHelper.startActivity(mActivity, MovieCameraActivity.class, bundle);
                } else {
                    addSelectImage(mGirdList.get(position).getPath());
                }
            }
        });
        mGridView.setHeadersIgnorePadding(true);
//        ll_ok.setEnabled(false);
//        btn_ok.setEnabled(false);
    }


    public void onEvent(BasePostEvent event) {
        switch (event.eventCode) {
            case PuTaoConstants.COLLAGE_CAMERA_FINISH:
                Bundle bundle1 = event.bundle;
                String photo_data = bundle1.getString("photo_data");
                addSelectImage(photo_data);
                break;

            case PuTaoConstants.FINISH_TO_MENU_PAGE:
                finish();
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getEventBus().unregister(this);
    }

    @Override
    public void doInitData() {
        selectImages.clear();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
                 String photo_num_str = "";

                mTemplateIconInfo = (TemplateIconInfo) bundle.getSerializable("sampleinfo");
                if (mTemplateIconInfo != null) {
                    maxnum = Integer.parseInt(mTemplateIconInfo.num);
                    photo_num_str = "1~"+maxnum;
                    doAnalysis();
                }
        tv_photo_num.setText(String.format(getResources().getString(R.string.collage_select_image), photo_num_str));
        /*if (bundle != null) {
            mIsconnect = bundle.getBoolean("isconnect", false);
            String photo_num_str = "";
            if (mIsconnect) {
                maxnum = 9;
                photo_num_str = "2~9";
                mConnectSample = (CollageConfigInfo.ConnectImageInfo) bundle.getSerializable("sampleinfo");
            } else {
                mSampleInfo = (CollageSampleItem) bundle.getSerializable("sampleinfo");
                if (mSampleInfo != null) {
                    maxnum = Integer.parseInt(mSampleInfo.category);
                    photo_num_str = maxnum + "";
                    doAnalysis();
                }
            }*/
        }
        getData();
        getGallery();

        EventBus.getEventBus().register(this);
    }

    @Override
    protected void onRestart() {
        getData();
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

   /* void getGallery() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mGalleryList = CollagePhotoUtil.QueryALLGalleryList(mContext);
                sendHandleMessage(SetGalleryList);
               *//* for (int i = 0; i < mGalleryList.size(); i++) {
                    GalleryEntity item = mGalleryList.get(i);
                    if (item.getImage_path().contains(PuTaoConstants.PAIAPI_PHOTOS_FOLDER)) {
                        bucket_id = item.getBucket_id();
                    }
                }*//*
            }
        }).start();
    }*/

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


   /* void reSetAlbumData(final int bucket_id) {
        final ProgressDialog dialog = new AlbumProcessDialog(mContext, "正在更新相册照片...").Get();
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mGirdList = CollagePhotoUtil.QueryPhotoByBUCKET_ID(mContext, bucket_id);
                sendHandleMessage(SetAlbumPhotos);
                dialog.dismiss();
            }
        }).start();

    }
*/
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


   /* void loadGalleryList() {
        GalleryListAdapter galleryAdapter = new GalleryListAdapter(mContext, mGalleryList);
        layout_gallery_list.setAdapter(galleryAdapter);
        layout_gallery_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GalleryEntity entity = mGalleryList.get(position);
                reSetAlbumData(entity.getBucket_id());
                hideGalleryLsit();
            }
        });
    }*/

    /**
     * 执行友盟统计
     */
    private void doAnalysis() {
        String eventcode = UmengAnalysisConstants.UMENG_COUNT_EVENT_JIGSAW_IMAGEPICKER_ONE;
        switch (maxnum) {
            case 1:
                eventcode = UmengAnalysisConstants.UMENG_COUNT_EVENT_JIGSAW_IMAGEPICKER_ONE;
                break;
            case 2:
                eventcode = UmengAnalysisConstants.UMENG_COUNT_EVENT_JIGSAW_IMAGEPICKER_TWO;
                break;
            case 3:
                eventcode = UmengAnalysisConstants.UMENG_COUNT_EVENT_JIGSAW_IMAGEPICKER_THREE;
                break;
            case 4:
                eventcode = UmengAnalysisConstants.UMENG_COUNT_EVENT_JIGSAW_IMAGEPICKER_FOUR;
                break;
        }
        doUmengEventAnalysis(eventcode);
    }
    private AlbumGridAdapter mAlbumGridAdapter;

    void LoadAlbumData() {
       /* mGridView.setAdapter(new StickyGridAdapter(mActivity, mGirdList, mGridView));
        //至少有一个相机的Icon
        if (mGirdList.size() > 1) {
            nonePhotoView.setVisibility(View.INVISIBLE);
            jigsaw_photo_selected.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.VISIBLE);
        } else {
            nonePhotoView.setVisibility(View.VISIBLE);
            jigsaw_photo_selected.setVisibility(View.INVISIBLE);
            mGridView.setVisibility(View.INVISIBLE);
        }*/

        mAlbumGridAdapter = new AlbumGridAdapter(mActivity, mGirdList, mGridView);
        mGridView.setAdapter(mAlbumGridAdapter);
        //至少有一个相机的Icon
        if (mGirdList.size() > 0) {
            nonePhotoView.setVisibility(View.INVISIBLE);
            jigsaw_photo_selected.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.VISIBLE);
        } else {
            nonePhotoView.setVisibility(View.VISIBLE);
            jigsaw_photo_selected.setVisibility(View.INVISIBLE);
            mGridView.setVisibility(View.INVISIBLE);
        }

    }

    void getData() {
        final ProgressDialog dialog = new AlbumProcessDialog(mContext, "正在读取葡萄相机照片...").Get();
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
          /*  case R.id.btn_ok:
                goMageCollage();
                break;*/
            case R.id.ll_ok:
                if(selectImages.size()==0){
                    ToasterHelper.showShort(this,"至少选一张照片",R.drawable.img_blur_bg);
                }else {
                    goMageCollage();
                }


                break;

            case R.id.title_tv:
                if (mGalleryShow) {
                    iv_icon.setImageResource(R.drawable.btn_nav_spread_down);
                    hideGalleryLsit();
                } else {
                    iv_icon.setImageResource(R.drawable.btn_nav_spread_up);
                    showGalleryList();
                }
                break;
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
//        ObjectAnimator.ofFloat(sl_gallery_list, "translationY", -sl_gallery_list.getHeight(),0).setDuration(10).start();
        sl_gallery_list.setVisibility(View.VISIBLE);
        gallery_list_panel.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(sl_gallery_list, "translationY", -sl_gallery_list.getHeight(),0).setDuration(300).start();
        mGalleryShow = true;
    }

    void hideGalleryLsit() {
        gallery_list_panel.setBackgroundColor(0x00000000);
        ObjectAnimator.ofFloat(sl_gallery_list, "translationY",0, -sl_gallery_list.getHeight()).setDuration(300).start();
        mGalleryShow = false;
        gallery_list_panel.postDelayed(new Runnable() {
            @Override
            public void run() {
                gallery_list_panel.setVisibility(View.GONE);
            }
        }, 550);
    }


    private void goMageCollage() {
        /*if (!mIsconnect && (selectImages.size() != maxnum)) {
            showToast("请先选择" + maxnum + "图片");
            return;
        }*/
        doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_JIGSAW_IMAGEPICKER_NEXT);
        Bundle bundle = new Bundle();
        bundle.putSerializable("images", selectImages);
         if (!mIsconnect) {
            bundle.putSerializable("sampleinfo", mTemplateIconInfo);
             ActivityHelper.startActivity(mActivity, CollageMakeActivity.class, bundle);
//            ActivityHelper.startActivity(mActivity, ConnectPhotoActivity.class, bundle);
        }

       /* if (mIsconnect) {
            bundle.putSerializable("sampleinfo", mConnectSample);
            ActivityHelper.startActivity(mActivity, ConnectPhotoActivity.class, bundle);
        } else {
            bundle.putSerializable("sampleinfo", mSampleInfo);
            ActivityHelper.startActivity(mActivity, CollageMakeActivity.class, bundle);
        }*/
    }

    void enableCollageButton(boolean enable) {
        if (enable) {
            ll_ok.setEnabled(true);
           /* btn_ok.setEnabled(true);
            btn_ok.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_view_eb5350));*/
        } else {
//            ll_ok.setEnabled(false);
//            btn_ok.setEnabled(false);
//            btn_ok.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_view_grey));
        }
    }

    void updateCollageButtonEnable() {
        if (!mIsconnect) {
            if (selectImages.size() >= 1) {
                enableCollageButton(true);
            } else {
                enableCollageButton(false);
            }
        } else {
            if (selectImages.size() == maxnum) {
                enableCollageButton(true);
            } else {
                enableCollageButton(false);
            }
        }
    }

    /**
     * 添加新的选择照片
     *
     * @param
     */
    private void addSelectImage(String path) {

        if (selectImages.size() >= maxnum) {
            ToasterHelper.showShort(this,"最多只能选择" + maxnum + "张照片",R.drawable.img_blur_bg);
            return;
        }
        final RelativeLayout viewItem = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.activity_photo_selected_item, null);
        ImageView photo = (ImageView) viewItem.findViewById(R.id.select_image);
//        Bitmap bitmap_sample = ((BitmapDrawable) getResources().getDrawable(R.drawable.filter_none)).getBitmap();
//        Bitmap bitmap_remove = ((BitmapDrawable) getResources().getDrawable(R.drawable.jigsaw_button_delete)).getBitmap();
//        Bitmap bitmap = BitmapHelper.getInstance().getCenterCropBitmap(path, 200, 200);
//        if (bitmap == null) {
//            Loger.d("can't read select image");
//            return;
//        }
        ImageView remove_image = (ImageView) viewItem.findViewById(R.id.remove_image);
        remove_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info1 = (String) viewItem.getTag();
                select_image_contanier.removeView(viewItem);
                doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_JIGSAW_IMAGEPICKER_DELETE);
                if (selectImages.contains(info1))
                    selectImages.remove(info1);

                sendHandleMessage(ImageSum);
                updateCollageButtonEnable();
            }
        });
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        params.leftMargin = bitmap.getWidth() - bitmap_remove.getWidth() / 2;
//        remove_image.setLayoutParams(params);
//        photo.setImageBitmap(bitmap);

        DisplayImageOptions options = new DisplayImageOptions.Builder().
                showImageOnLoading(BitmapHelper.getLoadingDrawable()).showImageOnFail(BitmapHelper.getLoadingDrawable())
                .considerExifParams(true)
                .cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565).build();

        ImageLoader.getInstance().displayImage("file://" + path, photo, options);

        viewItem.setTag(path);
        select_image_contanier.addView(viewItem);
        selectImages.add(path);
        sendHandleMessage(ImageSum);
//        sum.setText(selectImages.size());
        updateCollageButtonEnable();
    }
}
