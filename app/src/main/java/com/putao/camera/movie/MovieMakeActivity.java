
package com.putao.camera.movie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.putao.camera.R;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.constants.UmengAnalysisConstants;
import com.putao.camera.editor.PhotoShareActivity;
import com.putao.camera.editor.filtereffect.EffectCollection;
import com.putao.camera.editor.filtereffect.EffectImageTask;
import com.putao.camera.editor.view.FilterEffectThumbnailView;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.CommonUtils;
import com.putao.camera.util.DisplayHelper;
import com.putao.camera.util.StringHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jidongdong on 15/3/16.
 */
public class MovieMakeActivity extends BaseActivity implements View.OnClickListener {
    private Button back_btn, btn_save, camera_btn;
    private ImageView show_image;
    private LinearLayout filter_contanier, layout_caption;
    private Bitmap originImageBitmap, filter_origin;
    private FrameLayout photo_area_rl;
    private TextView tv_caption_zh, tv_caption_en;
    String photo_data;
    private int black_edge_height = DisplayHelper.getValueByDensity(35);
    final List<View> filterEffectViews = new ArrayList<View>();
    List<TextView> filterNameViews = new ArrayList<TextView>();
    private TextView title_tv;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_movie_photo_editor;
    }

    @Override
    public void doInitSubViews(View view) {
        back_btn = (Button) findViewById(R.id.back_btn);
        title_tv = (TextView) this.findViewById(R.id.title_tv);
        title_tv.setText("选择滤镜");
        btn_save = (Button) findViewById(R.id.right_btn);
        btn_save.setText("保存");
        camera_btn = queryViewById(R.id.camera_btn);
        camera_btn.setVisibility(View.VISIBLE);
        show_image = (ImageView) findViewById(R.id.show_image);
        filter_contanier = (LinearLayout) findViewById(R.id.filter_contanier);
        photo_area_rl = (FrameLayout) findViewById(R.id.photo_area_rl);
        layout_caption = (LinearLayout) findViewById(R.id.layout_caption);
        tv_caption_zh = (TextView) findViewById(R.id.tv_caption_zh);
        tv_caption_en = (TextView) findViewById(R.id.tv_caption_en);
        addOnClickListener(back_btn, btn_save, layout_caption, camera_btn);
        EventBus.getEventBus().register(this);
    }

    @Override
    public void doInitData() {
        Intent intent = this.getIntent();
        if (intent != null) {
            photo_data = intent.getStringExtra("photo_data");
        }
        loadFilters();
    }

    void setDefaultFilter() {
        new EffectImageTask(originImageBitmap, "crossprocess", new EffectImageTask.FilterEffectListener() {
            @Override
            public void rendered(Bitmap bitmap) {
                if (bitmap != null) {
                    setMovieImage(bitmap);
                }
            }
        }).execute();
    }

    public void loadFilters() {
        List<String> filterEffectNameList = new ArrayList<String>();
        filterEffectNameList.addAll(Arrays.asList(getResources().getStringArray(R.array.filter_effect)));
        if (!StringHelper.isEmpty(photo_data)) {
            int filter_origin_size = DisplayHelper.getValueByDensity(120);
            filter_origin = BitmapHelper.getInstance().getCenterCropBitmap(photo_data, filter_origin_size, filter_origin_size);
            for (final String item : filterEffectNameList) {
                new EffectImageTask(filter_origin, item, new EffectImageTask.FilterEffectListener() {
                    @Override
                    public void rendered(Bitmap bitmap) {
                        if (bitmap != null) {
                            AddFilterView(filterEffectViews, item, bitmap);
                        }
                    }
                }).execute();
            }
        } else {
            for (String item : filterEffectNameList) {
                Bitmap bitmap_sample = zoomSmall(((BitmapDrawable) getResources().getDrawable(EffectCollection.getFilterSample(item))).getBitmap());
                AddFilterView(filterEffectViews, item, bitmap_sample);
            }
        }
    }

    private void AddFilterView(final List<View> filterEffectViews, String item, Bitmap bitmap_sample) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.filter_item, null);
        FilterEffectThumbnailView simple_image = (FilterEffectThumbnailView) view.findViewById(R.id.filter_preview);
        simple_image.setImageBitmap(bitmap_sample);
        TextView tv_filter_name = (TextView) view.findViewById(R.id.filter_name);
        tv_filter_name.setText(EffectCollection.getMovieFilterName(item));
        view.setTag(item);
        tv_filter_name.setTag(item);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Umeng事件统计
                HashMap<String, String> filterMap = new HashMap<String, String>();
                filterMap.put((String) view.getTag(), (String) view.getTag());
//                UmengAnalysisHelper.onEvent(mActivity, UmengAnalysisConstants.UMENG_COUNT_EVENT_FILTER_CHOISE.toString(), filterMap);
                String mFilter = (String) view.getTag();
                new EffectImageTask(originImageBitmap, mFilter, new EffectImageTask.FilterEffectListener() {
                    @Override
                    public void rendered(Bitmap bitmap) {
                        if (bitmap != null) {
                            setMovieImage(bitmap);
                        }
                    }
                }).execute();
                // 边框
                for (View viewTemp : filterEffectViews) {
                    FilterEffectThumbnailView aRoundCornnerImageView = ((FilterEffectThumbnailView) viewTemp.findViewById(R.id.filter_preview));
                    if ((viewTemp.getTag()).equals(view.getTag())) {
                        aRoundCornnerImageView.setPhotoSelected(true);
                    } else {
                        aRoundCornnerImageView.setPhotoSelected(false);
                    }
                }
                for (TextView tv : filterNameViews) {
                    if (tv.getTag().equals(view.getTag())) {
                        tv.setTextColor(Color.RED);
                    } else {
                        tv.setTextColor(getResources().getColor(R.color.text_color_dark_898989));
                    }
                }
            }
        });
        filter_contanier.addView(view);
        filterEffectViews.add(view);
        filterNameViews.add(tv_filter_name);
    }

    private void setMovieImage(Bitmap bitmap) {
        Rect black_border_top = new Rect(0, 0, bitmap.getWidth(), black_edge_height);
        Rect black_border_bottom = new Rect(0, bitmap.getHeight() - black_edge_height, bitmap.getWidth(), bitmap.getHeight());
        Canvas canvas = new Canvas(bitmap);
        Paint bPaint = new Paint();
        bPaint.setColor(Color.BLACK);
        bPaint.setAntiAlias(true);
        canvas.drawRect(black_border_top, bPaint);
        canvas.drawRect(black_border_bottom, bPaint);
        show_image.setImageBitmap(bitmap);
    }

    private static Bitmap zoomSmall(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.65f, 0.65f);
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            int area_width = photo_area_rl.getWidth();
            int area_height = photo_area_rl.getHeight();
            if (!StringHelper.isEmpty(photo_data)) {
                if (originImageBitmap == null) {
                    originImageBitmap = BitmapHelper.getInstance().getBitmapFromPathWithSize(photo_data, area_width, area_height);
                    float ratio_origin = (float) originImageBitmap.getWidth() / originImageBitmap.getHeight();
                    float ratio_area = (float) area_width / area_height;
                    float rat = 1.0f;
                    if (ratio_origin >= ratio_area) {
                        rat = (float) area_width / originImageBitmap.getWidth();
                    } else {
                        rat = (float) area_height / originImageBitmap.getHeight();
                    }
                    Matrix matrix = new Matrix();
                    matrix.postScale(rat, rat);
                    originImageBitmap = Bitmap.createBitmap(originImageBitmap, 0, 0, originImageBitmap.getWidth(), originImageBitmap.getHeight(),
                            matrix, false).copy(Bitmap.Config.ARGB_8888, true);
                    show_image.setLayoutParams(new RelativeLayout.LayoutParams(originImageBitmap.getWidth(), originImageBitmap.getHeight()));
                    setMovieImage(originImageBitmap);
                    setDefaultFilter();
                }
            }
        }
    }

    public void save() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在保存图片...");
        progressDialog.show();
        Bitmap bitmap = createBitmapWithCaptions();
        final File pictureFile = CommonUtils.getOutputMediaFile();
        FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(pictureFile);
            // keep full quality of the image
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        MediaScannerConnection.scanFile(this, new String[]{pictureFile.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                Bundle bundle = new Bundle();
                bundle.putString("savefile", pictureFile.toString());
                EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.PHOTO_CONTENT_PROVIDER_REFRESH, bundle));
                progressDialog.dismiss();
                finish();
                ActivityHelper.startActivity(mActivity, PhotoShareActivity.class, bundle);
            }
        });
    }

    private Bitmap createBitmapWithCaptions() {
        layout_caption.setBackgroundColor(getResources().getColor(R.color.transparent));
        Bitmap new_bitmap = null;
        new_bitmap = Bitmap.createBitmap(photo_area_rl.getWidth(), photo_area_rl.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(new_bitmap);
        photo_area_rl.draw(canvas);
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        int area_width = photo_area_rl.getWidth();
        int area_height = photo_area_rl.getHeight();
        int actual_width = 0, actual_height = 0;
        if (originImageBitmap.getHeight() < area_height && originImageBitmap.getWidth() < area_width) {
            actual_width = originImageBitmap.getWidth();
            actual_height = originImageBitmap.getHeight();
        } else {
            float origin_ratio = (float) originImageBitmap.getWidth() / originImageBitmap.getHeight();
            float current_ratio = (float) area_width / area_height;
            if (origin_ratio >= current_ratio) {
                actual_width = area_width;
                actual_height = (int) (originImageBitmap.getHeight() * ((float) actual_width / originImageBitmap.getWidth()));
            } else {
                actual_height = area_height;
                actual_width = (int) (originImageBitmap.getWidth() * ((float) actual_height) / originImageBitmap.getHeight());
            }
        }
        int cut_x = (area_width - actual_width) / 2;
        int cut_y = (area_height - (actual_height)) / 2;
        return Bitmap.createBitmap(new_bitmap, cut_x, cut_y, actual_width, actual_height);
    }

    public void onEvent(BasePostEvent event) {
        switch (event.eventCode) {
            case PuTaoConstants.MOVIE_CAPTION_TRANSLATE:
                Bundle bundle = event.bundle;
                String text_zh = bundle.getString("text_zh");
                String text_en = bundle.getString("text_en");
                if (!StringHelper.isEmpty(text_zh)) {
                    tv_caption_zh.setText(text_zh);
                } else {
                    tv_caption_zh.setText(getResources().getString(R.string.movie_default_zh));
                }
                if (!StringHelper.isEmpty(text_en)) {
                    tv_caption_en.setText(text_en);
                } else {
                    tv_caption_en.setText(getResources().getString(R.string.movie_default_en));
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.right_btn:
                save();
                break;
            case R.id.layout_caption: {
                Bundle bundle = new Bundle();
                bundle.putString("text_zh", tv_caption_zh.getText().toString());
                bundle.putString("text_en", tv_caption_en.getText().toString());
                ActivityHelper.startActivity(mActivity, MovieCaptionsActivity.class, bundle);
                break;
            }
            case R.id.camera_btn: {
                finish();
                Bundle bundle = new Bundle();
                EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.FINISH_TO_MOVIE_MAKE_PAGE, bundle));
                break;
            }
            default:
                break;
        }
    }
}
