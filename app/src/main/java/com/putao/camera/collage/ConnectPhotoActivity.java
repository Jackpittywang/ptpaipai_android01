package com.putao.camera.collage;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.putao.camera.R;
import com.putao.camera.album.AlbumPhotoSelectActivity;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.bean.CollageConfigInfo;
import com.putao.camera.collage.util.CollageHelper;
import com.putao.camera.collage.util.NinePatchChunk;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.editor.PhotoShareActivity;
import com.putao.camera.editor.view.MyTextView;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.setting.watermark.management.CollageManagementActivity;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.CommonUtils;
import com.putao.camera.util.DisplayHelper;
import com.putao.camera.util.Loger;
import com.putao.camera.util.StringHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by jidongdong on 15/1/27.
 */
public class ConnectPhotoActivity extends BaseActivity implements View.OnClickListener {
    private ArrayList<String> selectImages;
    private Button back_btn, btn_save, btn_fold;
    private LinearLayout joint_image_layout, cur_cate_samples, panel_sample_list;
    private HorizontalScrollView sl_sample_list;
    private CollageConfigInfo mCollageConfigInfo;
    private ArrayList<ImageView> mask_view_list;
    private MyTextView btn_new_res;
    private CollageConfigInfo.ConnectImageInfo mConnectImageInfo;
    DisplayMetrics metrics;
    NinePatchDrawable mMaskDrawable;
    private boolean mIsExpand = true;
    AlertDialog dialog;
    ImageView mSelectImageView;


    @Override
    public int doGetContentViewId() {
        return R.layout.activity_join_image;
    }

    @Override
    public void doInitSubViews(View view) {
        btn_save = queryViewById(R.id.btn_save);
        back_btn = queryViewById(R.id.back_btn);
        joint_image_layout = queryViewById(R.id.joint_image_layout);
        panel_sample_list = queryViewById(R.id.panel_sample_list);
        cur_cate_samples = queryViewById(R.id.cur_cate_samples);
        btn_fold = queryViewById(R.id.btn_fold);
        sl_sample_list = queryViewById(R.id.sl_sample_list);
        btn_new_res = queryViewById(R.id.btn_new_res);
        btn_new_res.setVisibility(View.GONE);
        addOnClickListener(btn_save, back_btn, btn_fold, btn_new_res);
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        EventBus.getEventBus().register(this);
    }

    @Override
    public void doInitData() {
        mask_view_list = new ArrayList<ImageView>();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            selectImages = (ArrayList<String>) bundle.getSerializable("images");
            mConnectImageInfo = (CollageConfigInfo.ConnectImageInfo) bundle.getSerializable("sampleinfo");
            setBackground();
            initCollageView();
        }
        mCollageConfigInfo = CollageHelper.getCollageConfigInfoFromDB(mContext);

        loadSamples();
    }


    private void loadSamples() {
        if (mCollageConfigInfo == null) {
            return;
        }
        ArrayList<CollageConfigInfo.ConnectImageInfo> list = mCollageConfigInfo.content.connect_image;
        if (list.size() > 0) {
            for (CollageConfigInfo.ConnectImageInfo item : list) {
                ImageView imageView = new ImageView(mActivity);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(160, 240);
                params.leftMargin = 10;
                params.rightMargin = 10;
                params.topMargin = 3;
                params.bottomMargin = 3;
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setTag(item);
                String path = CollageHelper.getCollageFilePath() + item.sample_image;
                Bitmap bitmap = BitmapHelper.getInstance().getCenterCropBitmap(path, 160, 240);
                imageView.setImageBitmap(bitmap);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CollageConfigInfo.ConnectImageInfo item_Info = (CollageConfigInfo.ConnectImageInfo) v.getTag();
                        mConnectImageInfo = item_Info;
                        setBackground();
                    }
                });
                cur_cate_samples.addView(imageView);
            }
        }
    }


    void setBackground() {
        if (mConnectImageInfo == null)
            return;
        String folder = "xxhdpi/";
        switch (metrics.densityDpi) {
            case DisplayMetrics.DENSITY_XHIGH:
                folder = "xhdpi/";
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                break;
        }
        String path = CollageHelper.getCollageFilePath() + folder + mConnectImageInfo.background_image;
        String mask_path = CollageHelper.getCollageFilePath() + folder + mConnectImageInfo.mask_image;
//        BitmapDrawable drawable = (BitmapDrawable) BitmapDrawable.createFromPath(path);
        Bitmap bitmap_background = BitmapHelper.getInstance().getBitmapFromPath(path);
        if (bitmap_background == null) {
            showToast("拼接模板资源丢啦");
            return;
        }
        byte[] chunk = bitmap_background.getNinePatchChunk();
//        NinePatch np = new NinePatch(bitmap_background, chunk, null);
        if (NinePatch.isNinePatchChunk(chunk)) {
            NinePatchDrawable npdrawable = new NinePatchDrawable(getResources(),
                    bitmap_background, chunk, NinePatchChunk.deserialize(chunk).mPaddings, null);
            npdrawable.setTargetDensity(metrics);
            joint_image_layout.setBackgroundDrawable(npdrawable);
        }
        Bitmap bitmap_mask = BitmapHelper.getInstance().getBitmapFromPath(mask_path);
        mMaskDrawable = new NinePatchDrawable(getResources(), bitmap_mask, bitmap_mask.getNinePatchChunk(), NinePatchChunk.deserialize(bitmap_mask.getNinePatchChunk()).mPaddings, null);
        if (mask_view_list.size() > 0) {
            for (ImageView v : mask_view_list) {
                v.setBackgroundDrawable(mMaskDrawable);
            }
        }
    }

    public void initCollageView() {
        if (selectImages != null) {
            for (int i = 0; i < selectImages.size(); i++) {
                if (!StringHelper.isEmpty(selectImages.get(i))) {
                    new LoadImagesTask(selectImages.get(i)).execute();
                }
            }
        }
    }

    private Bitmap getBitmap(String path) {
        Bitmap bitmap = BitmapHelper.getInstance().getBitmapFromPath(path);
        if (bitmap != null) {
            if (true || bitmap.getWidth() > DisplayHelper.getScreenWidth()) {
                Matrix matrix = new Matrix();
                float scale = ((float) DisplayHelper.getScreenWidth() * ((1080 - 180) / 1080f)) / bitmap.getWidth();
                matrix.postScale(scale, scale);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
            }
        }
        return bitmap;
    }

    private class LoadImagesTask extends AsyncTask<Integer, Integer, Bitmap> {
        private String mUrl;

        public LoadImagesTask(String url) {
            mUrl = url;
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            return getBitmap(mUrl);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                FrameLayout frameLayout = new FrameLayout(mActivity);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, bitmap.getHeight());
                params.topMargin = 5;
                params.bottomMargin = 25;
                frameLayout.setLayoutParams(params);
                FrameLayout.LayoutParams img_params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                ImageView imageView = new ImageView(mActivity);
                imageView.setLayoutParams(img_params);
                imageView.setImageBitmap(bitmap);
                imageView.setBackgroundColor(Color.WHITE);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Loger.d("id:::" + v.toString());
                        mSelectImageView = (ImageView) v;
                        showPhotoOptdialog();
                    }
                });
                frameLayout.addView(imageView);
                ImageView mask_imageView = new ImageView(mActivity);
                mask_imageView.setScaleType(ImageView.ScaleType.MATRIX);
                mask_imageView.setLayoutParams(img_params);
                mask_imageView.setBackgroundDrawable(mMaskDrawable);
                mask_imageView.setTag(mUrl);

                frameLayout.addView(mask_imageView);
                mask_view_list.add(mask_imageView);
                joint_image_layout.addView(frameLayout);
            }
        }
    }

    void showPhotoOptdialog() {
        dialog = new AlertDialog.Builder(mContext).create();
        dialog.show();
        View parent = LayoutInflater.from(this).inflate(R.layout.layout_collage_image_opreate_dialog, null);
        Button btn_replace = queryViewById(parent, R.id.btn_replace);
        Button btn_mirror = queryViewById(parent, R.id.btn_mirror);
        Window window = dialog.getWindow();
        window.setContentView(parent);
        dialog.setCanceledOnTouchOutside(true);
        btn_replace.setOnClickListener(this);
        btn_mirror.setOnClickListener(this);
    }


    private void saveAndShareImage() {
        Bitmap bitmap = Bitmap.createBitmap(joint_image_layout.getWidth(), joint_image_layout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        joint_image_layout.draw(canvas);
        File pictureFile = CommonUtils.getOutputMediaFile();
        FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(pictureFile);
            // keep full quality of the image
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            // success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MediaScannerConnection.scanFile(this,
                new String[]{pictureFile.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Bundle bundle = new Bundle();
                        bundle.putString("from", "connect");
                        bundle.putString("savefile",path);
                        ActivityHelper.startActivity(mActivity, PhotoShareActivity.class, bundle);
                    }
                });

    }

    @Override
    public void onBackPressed() {
        showQuitTip();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getEventBus().unregister(this);
    }

    public void onEvent(BasePostEvent event) {
        switch (event.eventCode) {
            case PuTaoConstants.EVENT_CONNECT_PHOTO_SELECT:
                Bundle bundle = event.bundle;
                String path = bundle.getString("photo_path");
                if (!StringHelper.isEmpty(path)) {
                    mSelectImageView.setImageBitmap(getBitmap(path));
                }
                break;
            default:
                break;
        }
    }

    @SuppressLint("CommitTransaction")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                showQuitTip();
                break;
            case R.id.btn_save:
                saveAndShareImage();
                break;
            case R.id.btn_fold:
                if (mIsExpand) {
                    btn_fold.setBackgroundDrawable(getResources().getDrawable(R.drawable.template_button_fold2));
                    ObjectAnimator.ofFloat(panel_sample_list, "translationY", 0, sl_sample_list.getHeight()).setDuration(300).start();
                    mIsExpand = false;
                } else {
                    btn_fold.setBackgroundDrawable(getResources().getDrawable(R.drawable.template_button_fold));
                    ObjectAnimator.ofFloat(panel_sample_list, "translationY", sl_sample_list.getHeight(), 0).setDuration(300).start();
                    mIsExpand = true;
                }
                break;
            case R.id.btn_new_res:
                ActivityHelper.startActivity(mActivity, CollageManagementActivity.class);
                break;
            case R.id.btn_replace:
                Bundle bundle = new Bundle();
                bundle.putBoolean("from_connect_photo", true);
                ActivityHelper.startActivity(mActivity, AlbumPhotoSelectActivity.class, bundle);
                dialog.dismiss();
                break;
            case R.id.btn_mirror:
                if (mSelectImageView != null) {
                    Matrix matrix = new Matrix();
                    matrix.postScale(-1, 1);
                    Bitmap bitmap = ((BitmapDrawable) mSelectImageView.getDrawable()).getBitmap();
                    mSelectImageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
                }
                dialog.dismiss();
                break;
            default:
                break;
        }
    }

    void showQuitTip() {
        new AlertDialog.Builder(mContext).setTitle("提示").setMessage("确认放弃当前编辑吗？").setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }
}
