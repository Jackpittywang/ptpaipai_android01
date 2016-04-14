package com.putao.camera.editor;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.putao.camera.R;
import com.putao.camera.application.MainApplication;
import com.putao.camera.bean.StickerCategoryInfo;
import com.putao.camera.bean.StickerUnZipInfo;
import com.putao.camera.bean.WaterMarkIconInfo;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.editor.dialog.WaterTextDialog;
import com.putao.camera.editor.filtereffect.EffectCollection;
import com.putao.camera.editor.filtereffect.EffectImageTask;
import com.putao.camera.editor.filtereffect.GLEffectRender;
import com.putao.camera.editor.fragment.WaterMarkChoiceAdapter;
import com.putao.camera.editor.view.FilterEffectThumbnailView;
import com.putao.camera.editor.view.MyTextView;
import com.putao.camera.editor.view.NormalWaterMarkView;
import com.putao.camera.editor.view.TextWaterMarkView;
import com.putao.camera.editor.view.WaterMarkView;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.gps.CityMap;
import com.putao.camera.gps.GpsUtil;
import com.putao.camera.setting.watermark.management.MatterCenterActivity;
import com.putao.camera.setting.watermark.management.StickerPicAdapter;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.CommonUtils;
import com.putao.camera.util.DateUtil;
import com.putao.camera.util.DisplayHelper;
import com.putao.camera.util.FileUtils;
import com.putao.camera.util.Loger;
import com.putao.camera.util.SharedPreferencesHelper;
import com.putao.camera.util.StringHelper;
import com.putao.camera.util.WaterMarkHelper;
import com.sunnybear.library.controller.BasicFragmentActivity;
import com.sunnybear.library.view.recycler.BasicRecyclerView;
import com.sunnybear.library.view.recycler.listener.OnItemClickListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.OnClick;

public class PhotoEditorActivity extends BasicFragmentActivity implements View.OnClickListener {
    private FrameLayout photo_area_rl;
    private Button backBtn;
    private TextView tv_action, tv_save;
    //    private MyTextView btn_new_res;
    private List<WaterMarkView> mMarkViewList, mMarkViewTempList;
    private LinearLayout ll_picture_filter, choice_water_mark_ll, filter_contanier, opt_button_bar2, opt_button_bar, mark_content, mark_list_pager,
            mark_cate_contanier, ll_cut_image, rotate_image_ll, rotate_contanier, anti_clockwise, clockwise_spin, horizontal_flip, vertical_flip, ll_dynamic_filter, left_btn_ll, edit_ll_cancel, edit_ll_save;
    private ViewGroup title_bar_rl, option_bars;
    private BasicRecyclerView rv_articlesdetail_applyusers;
    // 相片编辑状态
    private HorizontalScrollView filter_scrollview;
    //    private GridView water_mark_collection_icon_gv;
    private int text_index = -1;
    private TextWaterMarkView waterView;
    private ImageView show_image, iv_hide_mark;
    private Bitmap originImageBitmap, corpOriginImageBitmap, filter_origin, ImageCropBitmap;
    private EditAction mEditAction = EditAction.NONE;
    private boolean mFlagMarkShow = true;
    private String mCurrentFilter = GLEffectRender.DEFAULT_EFFECT_ID;
    private String mTempFilter = GLEffectRender.DEFAULT_EFFECT_ID;
    private WaterMarkChoiceAdapter mWaterMarkChoiceAdapter;
    private StickerPicAdapter mStickerPicAdapter;

    //    private WaterMarkCategoryInfo mWaterMarkCategoryInfo;
    private StickerCategoryInfo mStickerCategoryInfo;
    //    private ArrayList<WaterMarkCategoryInfo> content = new ArrayList<WaterMarkCategoryInfo>();
    private ArrayList<StickerCategoryInfo> content = new ArrayList<StickerCategoryInfo>();
    private String photo_data;
    private int photoType;
    final List<View> filterEffectViews = new ArrayList<View>();
    List<TextView> filterNameViews = new ArrayList<TextView>();
    private boolean is_edited;
    private Bundle bundle;
    public static final int CROP_11 = 1;
    public static final int CROP_43 = 2;


    private enum EditAction {
        NONE, ACTION_CUT, ACTION_Mark, ACTION_FILTER, ACTION_ROTATE,
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_photo_editor;
    }

    @Override
    protected void onViewCreatedFinish(Bundle saveInstanceState) {
        doInitSubViews();
        Intent intent = this.getIntent();
        photo_data = intent.getStringExtra("photo_data");
        if (!StringHelper.isEmpty(photo_data)) {
            originImageBitmap = BitmapHelper.getInstance().getBitmapFromPathWithSize(photo_data, DisplayHelper.getScreenWidth(),
                    DisplayHelper.getScreenHeight());

            int filter_origin_size = DisplayHelper.getValueByDensity(120);
            filter_origin = BitmapHelper.getInstance().getCenterCropBitmap(photo_data, filter_origin_size, filter_origin_size);
        }

        ImageCropBitmap = BitmapHelper.imageCrop(originImageBitmap, photoType);
        show_image.setImageBitmap(ImageCropBitmap);
        loadFilters();
        mMarkViewList = new ArrayList<WaterMarkView>();
        mMarkViewTempList = new ArrayList<WaterMarkView>();
        mWaterMarkChoiceAdapter = new WaterMarkChoiceAdapter(mContext, mStickerCategoryInfo);
//        water_mark_collection_icon_gv.setAdapter(mWaterMarkChoiceAdapter);
//        setGridView();
       /* water_mark_collection_icon_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> watermarkMap = new HashMap<String, String>();
                watermarkMap.put(UmengAnalysisConstants.UMENG_COUNT_EVENT_WATER_MARK_CHOISE, mWaterMarkChoiceAdapter.getItem(position).imgName);

                Bundle bundle = new Bundle();
//                WaterMarkIconInfo info = mWaterMarkChoiceAdapter.getItem(position);
                StickerUnZipInfo info = mWaterMarkChoiceAdapter.getItem(position);
                bundle.putSerializable("iconRes", info);
                EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.WATER_MARK_ICON_CHOICE_REFRESH, bundle));
            }
        });*/
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mStickerPicAdapter = new StickerPicAdapter(mContext, null);
        rv_articlesdetail_applyusers.setAdapter(mStickerPicAdapter);
        rv_articlesdetail_applyusers.setLayoutManager(linearLayoutManager);
        loadWaterMarkCategories();
        rv_articlesdetail_applyusers.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Serializable serializable, int position) {
                Bundle bundle = new Bundle();
//                WaterMarkIconInfo info = mWaterMarkChoiceAdapter.getItem(position);
                StickerUnZipInfo info = mStickerPicAdapter.getItem(position);
                bundle.putSerializable("iconRes", info);
                EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.WATER_MARK_ICON_CHOICE_REFRESH, bundle));
            }
        });


       /* if (WaterMarkHelper.bHasNewWaterMarkUpdate == true) {
            btn_new_res.setShowRedPoint(true);
            btn_new_res.setVisibility(View.VISIBLE);
        } else {
            btn_new_res.setShowRedPoint(false);
            btn_new_res.setVisibility(View.VISIBLE);
        }*/

    }

    public void doInitSubViews() {
        left_btn_ll = (LinearLayout) findViewById(R.id.left_btn_ll);
        iv_hide_mark = (ImageView) findViewById(R.id.iv_hide_mark);
        ll_dynamic_filter = (LinearLayout) findViewById(R.id.ll_dynamic_filter);
        anti_clockwise = (LinearLayout) findViewById(R.id.anti_clockwise);
        clockwise_spin = (LinearLayout) findViewById(R.id.clockwise_spin);
        horizontal_flip = (LinearLayout) findViewById(R.id.horizontal_flip);
        vertical_flip = (LinearLayout) findViewById(R.id.vertical_flip);

        rotate_contanier = (LinearLayout) findViewById(R.id.rotate_contanier);
        rotate_image_ll = (LinearLayout) findViewById(R.id.rotate_image_ll);
        photo_area_rl = (FrameLayout) findViewById(R.id.photo_area_rl);
        opt_button_bar2 = (LinearLayout) findViewById(R.id.opt_button_bar2);
        opt_button_bar = (LinearLayout) findViewById(R.id.opt_button_bar);
        edit_ll_cancel = (LinearLayout) findViewById(R.id.edit_ll_cancel);
        edit_ll_save = (LinearLayout) findViewById(R.id.edit_ll_save);
        backBtn = (Button) findViewById(R.id.back_btn);
        tv_save = (TextView) findViewById(R.id.tv_save);
        show_image = (ImageView) findViewById(R.id.show_image);
        ll_cut_image = (LinearLayout) findViewById(R.id.ll_cut_image);
        choice_water_mark_ll = (LinearLayout) findViewById(R.id.choice_water_mark_ll);
        ll_picture_filter = (LinearLayout) findViewById(R.id.ll_picture_filter);
        title_bar_rl = (ViewGroup) findViewById(R.id.title_bar_rl);
        filter_scrollview = (HorizontalScrollView) findViewById(R.id.filter_scrollview);
        filter_contanier = (LinearLayout) findViewById(R.id.filter_contanier);
        option_bars = (ViewGroup) findViewById(R.id.option_bars);
        mark_content = (LinearLayout) findViewById(R.id.mark_content);
        mark_list_pager = (LinearLayout) findViewById(R.id.mark_list_pager);
        rv_articlesdetail_applyusers = (BasicRecyclerView) findViewById(R.id.rv_articlesdetail_applyusers);
        mark_cate_contanier = (LinearLayout) findViewById(R.id.mark_cate_contanier);
        tv_action = (TextView) findViewById(R.id.tv_action);
        filter_scrollview.setVisibility(View.GONE);
        EventBus.getEventBus().register(this);

    }

    @OnClick({R.id.ll_picture_filter, R.id.choice_water_mark_ll, R.id.tv_save, R.id.edit_ll_cancel,
            R.id.edit_ll_save, R.id.ll_cut_image, R.id.rotate_image_ll, R.id.anti_clockwise,
            R.id.clockwise_spin, R.id.horizontal_flip, R.id.vertical_flip, R.id.ll_dynamic_filter, R.id.iv_hide_mark,
            R.id.left_btn_ll})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_dynamic_filter:
                bundle = new Bundle();
                bundle.putString("photo_data", photo_data);
                ActivityHelper.startActivity(this, PhotoDynamicActivity.class, bundle);
                break;
            case R.id.ll_cut_image:
                bundle = new Bundle();
                bundle.putString("photo_data", photo_data);
                ActivityHelper.startActivity(this, PhotoEditorCutActivity.class, bundle);
                break;
            case R.id.choice_water_mark_ll:
                choice_water_mark_ll.setClickable(false);
                showWaterMarkContent();
//                opt_button_bar.setVisibility(View.GONE);
//                hideTitleAni();
                showMarkContent();
                tv_action.setText("贴纸");
                mEditAction = EditAction.ACTION_Mark;
                break;
            case R.id.ll_picture_filter:
                filter_scrollview.setVisibility(View.VISIBLE);
                hideTitleAni();
                tv_action.setText("滤镜");
                mEditAction = EditAction.ACTION_FILTER;
                break;
            case R.id.left_btn_ll: {
                showQuitTip();
            }
            break;
            case R.id.tv_save:
                save();
                break;
            case R.id.edit_ll_cancel:
                cancelEditing();
                break;
            case R.id.iv_hide_mark:
                choice_water_mark_ll.setClickable(true);
                opt_button_bar.setVisibility(View.VISIBLE);
                rotate_contanier.setVisibility(View.GONE);
                filter_scrollview.setVisibility(View.GONE);
                mark_content.setVisibility(View.GONE);
                break;
            case R.id.edit_ll_save:
                saveEditing();
                break;
           /* case R.id.btn_mark_hide:
                hideMarkContent();
                break;*/
            /*case R.id.btn_new_res:
                ActivityHelper.startActivity(mActivity, WaterMarkCategoryManagementActivity.class);
                finish();
                break;*/
            case R.id.rotate_image_ll:
                //旋转
                rotate_contanier.setVisibility(View.VISIBLE);
                hideTitleAni();
                tv_action.setText("旋转");
                mEditAction = EditAction.ACTION_ROTATE;
                break;
            case R.id.anti_clockwise:
                ImageCropBitmap = BitmapHelper.orientBitmap(ImageCropBitmap, ExifInterface.ORIENTATION_ROTATE_270);
                show_image.setImageBitmap(ImageCropBitmap);
                break;
            case R.id.clockwise_spin:
                ImageCropBitmap = BitmapHelper.orientBitmap(ImageCropBitmap, ExifInterface.ORIENTATION_ROTATE_90);
                show_image.setImageBitmap(ImageCropBitmap);
                break;
            case R.id.horizontal_flip:
                matrix = new Matrix();
                matrix.postScale(-1, 1);      /*水平翻转180度*/
                width = ImageCropBitmap.getWidth();
                height = ImageCropBitmap.getHeight();
                ImageCropBitmap = Bitmap.createBitmap(ImageCropBitmap, 0, 0, width, height, matrix, true);
                show_image.setImageBitmap(ImageCropBitmap);
                break;
            case R.id.vertical_flip:
                matrix = new Matrix();
                matrix.postScale(1, -1);/*垂直翻转180度*/
                width = ImageCropBitmap.getWidth();
                height = ImageCropBitmap.getHeight();
                ImageCropBitmap = Bitmap.createBitmap(ImageCropBitmap, 0, 0, width, height, matrix, true);
                show_image.setImageBitmap(ImageCropBitmap);

                break;
            default:
                break;
        }
    }

    void setGridView() {
        if (mStickerCategoryInfo != null) {
            int size = mStickerCategoryInfo.elements.size();
            int itemWidth = DisplayHelper.getScreenWidth() / 4;
            int gridviewWidth = 0;
            int columns = (size % 4 == 0) ? size / 4 : size / 4 + 1;
            if (columns < 4) {
                columns = 4;
            }
            gridviewWidth += columns * itemWidth;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gridviewWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
         /*   water_mark_collection_icon_gv.setLayoutParams(params);
            water_mark_collection_icon_gv.setColumnWidth(itemWidth);
            water_mark_collection_icon_gv.setHorizontalSpacing(0);
            water_mark_collection_icon_gv.setStretchMode(GridView.NO_STRETCH);
            water_mark_collection_icon_gv.setNumColumns(columns);*/
        }
    }

    private int mSelectMarkCategoryIndex = -1;

    /**
     * 从数据库读取已有的水印素材
     */
    private void initMarkCategoryInfos() {
        content.clear();

        Map<String, String> map = new HashMap<String, String>();
        map.put("type", "sticker");
//        map.put("isInner", "1");
//        List<WaterMarkCategoryInfo> camera_water_list = MainApplication.getDBServer().getWaterMarkCategoryInfoByWhere(map, false);
        List<StickerCategoryInfo> camera_water_list = MainApplication.getDBServer().getStickerCategoryInfoByWhere(map);
        content.addAll(camera_water_list);

        /*map = new HashMap<String, String>();
        map.put("type", WaterMarkCategoryInfo.photo);
        map.put("isInner", "0");
        camera_water_list = MainApplication.getDBServer().getWaterMarkCategoryInfoByWhere(map, true);
        if(camera_water_list.size()==0)return;
        content.addAll(1, camera_water_list);*/

        //Loger.d("chen+++++content.size()="+content.size());
        WaterMarkHelper.getStickerUnZipInfos(content);
    }

    private void loadWaterMarkCategories() {
        //读取已有水印
        initMarkCategoryInfos();

        mark_cate_contanier.removeAllViews();
        ImageView add = new ImageView(this);
        add.setImageResource(R.drawable.icon_capture_20_34);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ActivityHelper.startActivity(mContext, MatterCenterActivity.class);
                startActivity(MatterCenterActivity.class);
            }
        });

        for (int i = 0; i < content.size(); i++) {
            final StickerCategoryInfo info_temp = content.get(i);
            final MyTextView textView = new MyTextView(mContext);
//            textView.setText(info_temp.name);
            textView.setTag(info_temp);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            p.leftMargin = 30;
            p.rightMargin = 30;
            p.gravity = Gravity.CENTER;
            textView.setLayoutParams(p);
            textView.mIndex = i;
            if (0 == i)
                mStickerPicAdapter.replaceAll(info_temp.elements);
            //            if (info_temp.updated.equals("1")) {
            //                textView.setShowRedPoint(true);
            //            }
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStickerPicAdapter.replaceAll(info_temp.elements);
                    onWatermarkClicked(v, info_temp, textView);
                }
            });
            /*ImageView imageView=new ImageView(mActivity);
            imageView.setTag(info_temp);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            p.leftMargin = 30;
            p.rightMargin = 30;
            p.gravity = Gravity.CENTER;
            imageView.setLayoutParams(p);*/
           /* textView.mIndex = i;
            //            if (info_temp.updated.equals("1")) {
            //                textView.setShowRedPoint(true);
            //            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onWatermarkClicked(v, info_temp, textView);
                }
            });*/

//            mark_cate_contanier.addView(imageView);
            mark_cate_contanier.addView(textView);
        }
        mark_cate_contanier.addView(add);
        if (content.size() > 0) {
            mStickerCategoryInfo = content.get(0);
            mSelectMarkCategoryIndex = 0;
            updateMarkSelectColor();
            updateWaterList();
        }
    }

    /*private void onWatermarkClicked(View v, WaterMarkCategoryInfo info_temp, MyTextView textView) {
        mStickerCategoryInfo = (WaterMarkCategoryInfo) v.getTag();
        updateWaterList();
        mSelectMarkCategoryIndex = ((MyTextView) v).mIndex;
        updateMarkSelectColor();
    }*/
    private void onWatermarkClicked(View v, StickerCategoryInfo info_temp, MyTextView textView) {
        mStickerCategoryInfo = (StickerCategoryInfo) v.getTag();
        updateWaterList();
        mSelectMarkCategoryIndex = ((MyTextView) v).mIndex;
        updateMarkSelectColor();
    }

    void updateMarkSelectColor() {
        for (int i = 0; i < mark_cate_contanier.getChildCount() - 1; i++) {
            MyTextView view = (MyTextView) mark_cate_contanier.getChildAt(i);
            if (mSelectMarkCategoryIndex == view.mIndex) {
                view.setBackgroundResource(R.drawable.gray_btn_bg);
                view.setTextColor(Color.RED);
            } else {
                view.setBackgroundResource(R.drawable.background_view_blake);
                view.setTextColor(getResources().getColor(R.color.text_color_dark_898989));
            }
        }
        updateMarkSelectIcon();
    }

    void updateMarkSelectIcon() {
        for (int i = 0; i < mark_cate_contanier.getChildCount() - 1; i++) {
            MyTextView view = (MyTextView) mark_cate_contanier.getChildAt(i);
            final StickerCategoryInfo info_temp = (StickerCategoryInfo) view.getTag();
            Drawable bm_icon = null;
            if (!StringHelper.isEmpty(info_temp.cover_pic)) {
                String image_path;
                String info = info_temp.elements.get(0).imgName.substring(0, info_temp.elements.get(0).imgName.lastIndexOf("_"));
               /* if (mSelectMarkCategoryIndex == i) {
//                    image_path = WaterMarkHelper.getWaterMarkFilePath() + info_temp.icon_selected;
                    image_path =  FileUtils.getPutaoCameraPath()  + File.separator+info_temp.elements.get(0).zipName +File.separator+ info_temp.elements.get(0).imgName;
                } else {
//                    image_path = WaterMarkHelper.getWaterMarkFilePath() + info_temp.icon;
                    image_path = info_temp.cover_pic;
                }*/
                image_path = FileUtils.getPutaoCameraPath() + File.separator + info_temp.elements.get(0).zipName + File.separator + info + "_icon.png";

                Bitmap icon = BitmapHelper.getInstance().loadBitmap(image_path);
                Bitmap more_icon = BitmapFactory.decodeResource(getResources(), R.drawable.res_download_icon);
                if (icon == null || more_icon == null) return;
                float scale = (float) more_icon.getWidth() / icon.getWidth();
                bm_icon = new BitmapDrawable(icon);
                bm_icon.setBounds(0, 0, (int) (bm_icon.getIntrinsicWidth() * scale * DisplayHelper.getDensity()), (int) (bm_icon.getIntrinsicHeight() * scale * DisplayHelper.getDensity()));
            } else {
                bm_icon = getResources().getDrawable(R.drawable.edit_button_filter);
                bm_icon.setBounds(0, 0, bm_icon.getIntrinsicWidth(), bm_icon.getIntrinsicHeight());
            }
            if (bm_icon != null) {
                view.setCompoundDrawables(null, bm_icon, null, null);
            }
            if (mSelectMarkCategoryIndex == view.mIndex) {
                view.setTextColor(Color.RED);
            } else {
                view.setTextColor(getResources().getColor(R.color.text_color_dark_898989));
            }
        }
    }


    void updateWaterList() {
        mWaterMarkChoiceAdapter.setData(mStickerCategoryInfo);
//        setGridView();
        mWaterMarkChoiceAdapter.notifyDataSetChanged();
        if (!mFlagMarkShow) {
            showMarkContent();
        }
    }


    @Override
    protected String[] getRequestUrls() {
        return new String[0];
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getEventBus().unregister(this);
    }

    public void onEvent(BasePostEvent event) {
        switch (event.eventCode) {
            case PuTaoConstants.WATER_MARK_ICON_CHOICE_REFRESH: {
                Bundle bundle = event.bundle;
                String resName = "";
                StickerUnZipInfo iconInfo = null;
                if (bundle != null) {
                    iconInfo = (StickerUnZipInfo) bundle.getSerializable("iconRes");
                    resName = iconInfo.imgName;
                }
                try {
//                    String image_path = WaterMarkHelper.getWaterMarkFilePath() + resName;
                    String image_path = FileUtils.getPutaoCameraPath() + File.separator + iconInfo.zipName + File.separator + resName;
                    Bitmap bm = BitmapHelper.getInstance().loadBitmap(image_path);
                    hideMarkContent();
//                    if (iconInfo.type.equals(WaterMarkView.WaterType.TYPE_Normal)) {
                    addNormalWaterMarkView(iconInfo, bm);
                   /* } else if (iconInfo.type.equals(WaterMarkView.WaterType.TYPE_DISTANCE)
                            || iconInfo.type.equals(WaterMarkView.WaterType.TYPE_FESTIVAL)
                            || iconInfo.type.equals(WaterMarkView.WaterType.TYPE_TEXTEDIT)) {
                        addTextWaterMarkView(iconInfo, bm);
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
            case PuTaoConstants.WATER_FILTER_EFFECT_CHOICE_REFRESH: {
                Bundle bundle = event.bundle;
                String filterId = GLEffectRender.DEFAULT_EFFECT_ID;
                if (bundle != null) {
                    filterId = bundle.getString("filterId", GLEffectRender.DEFAULT_EFFECT_ID);
                }
                mTempFilter = filterId;
            }
            break;
            case PuTaoConstants.WATER_MARK_CITY_SELECTED:
                Bundle bundle1 = event.bundle;
                String city = bundle1.getString("city");
                updateDistanceViewText(city);
                break;
            case PuTaoConstants.WATER_MARK_TAKE_CANCEL:
                break;
            case PuTaoConstants.WATER_MARK_DATE_SELECTED:
                updateFestivalViewText(event.bundle);
                break;
            case PuTaoConstants.WATER_MARK_TEXT_EDIT:
                updateTextEditViewText(event.bundle);
                break;
            case PuTaoConstants.PHOTO_EDIT_CUT_FINISH:
                corpOriginImageBitmap = event.bundle.getParcelable("corpImage");
                if (mCurrentFilter == GLEffectRender.DEFAULT_EFFECT_ID) {
                    show_image.setImageBitmap(corpOriginImageBitmap);
                } else {
                    new EffectImageTask(corpOriginImageBitmap, mCurrentFilter, mFilterEffectListener).execute();
                }
                is_edited = true;
                break;


            case PuTaoConstants.FINISH_TO_MENU_PAGE:
                finish();
                break;
            case PuTaoConstants.UNZIP_FILE_FINISH:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadWaterMarkCategories();
                    }
                });
                break;
            default:
                break;
        }
    }

    /**
     * 更新水印文字
     *
     * @param bundle
     */
    private void updateTextEditViewText(Bundle bundle) {
        String watermark_text = bundle.getString("watermark_text");
        if (!StringHelper.isEmpty(watermark_text)) {
            waterView.setWaterTextByType(TextWaterMarkView.WaterTextEventType.TYPE_EDIT_TEXT, watermark_text);
        }
    }

    /**
     * 根据选择的节日设定节日View倒计时
     *
     * @param bundle
     */
    private void updateFestivalViewText(Bundle bundle) {
        String name = bundle.getString("name");
        String date = bundle.getString("date");
        if (!StringHelper.isEmpty(name)) {
            waterView.setWaterTextByType(TextWaterMarkView.WaterTextEventType.TYPE_SELECT_FESTIVAL_NAME, name);
        }
        if (!StringHelper.isEmpty(date)) {
            waterView.setWaterTextByType(TextWaterMarkView.WaterTextEventType.TYPE_SELECT_FESTIVAL_DATE,
                    String.valueOf(DateUtil.getDays(date, DateUtil.getStringDateShort())));
        }
    }

    /**
     * 根据当前选择城市更新界面上的文字信息
     *
     * @param city
     */
    private void updateDistanceViewText(String city) {
        CityMap.CityPositon pos = CityMap.getInstance().getLocationByCity(city);
        String current_city = SharedPreferencesHelper.readStringValue(mContext, PuTaoConstants.PREFERENCE_CURRENT_CITY);
        // Loger.d("current_city------------->" + current_city);
        double lat1 = 0, lng1 = 0, lat2 = 0, lng2 = 0;
        try {
            lat1 = Double.valueOf(SharedPreferencesHelper.readStringValue(mContext, PuTaoConstants.PREFERENC_LOCATION_LATITUDE, "0"));
            lng1 = Double.valueOf(SharedPreferencesHelper.readStringValue(mContext, PuTaoConstants.PREFERENC_LOCATION_LONGITUDE, "0"));
            lng2 = Double.valueOf(pos.longitude);
            lat2 = Double.valueOf(pos.latitude);
        } catch (Exception e) {
        }
        waterView.setWaterText(text_index, city);
        if (!StringHelper.isEmpty(current_city)) {
            waterView.setWaterTextByType(TextWaterMarkView.WaterTextEventType.TYPE_SELECT_CURRENT_CITY, current_city);
        } else {
            current_city = waterView.getWaterTextByType(TextWaterMarkView.WaterTextEventType.TYPE_SELECT_CURRENT_CITY);
            CityMap.CityPositon c_pos = CityMap.getInstance().getLocationByCity(current_city);
            lat1 = Double.parseDouble(c_pos.latitude);
            lng1 = Double.parseDouble(c_pos.longitude);
        }
        waterView.setWaterTextByType(TextWaterMarkView.WaterTextEventType.TYPE_SELECT_NONE, GpsUtil.GetDistance(lat1, lng1, lat2, lng2) + " 公里");
    }

    private void addTextWaterMarkView(WaterMarkIconInfo iconInfo, Bitmap bm) {
        final TextWaterMarkView mMarkView = new TextWaterMarkView(mContext, bm, iconInfo.textElements, iconInfo, true);
        mMarkView.setTextOnclickListener(new TextWaterMarkView.TextOnClickListener() {
            @Override
            public void onclicked(WaterMarkIconInfo markIconInfo, int index) {
                text_index = index;
                waterView = mMarkView;
                if (markIconInfo.type.equals(WaterMarkView.WaterType.TYPE_DISTANCE)) {
//                    ActivityHelper.startActivity(mContext, CitySelectActivity.class);
                    startActivity(CitySelectActivity.class);
                } else if (markIconInfo.type.equals(WaterMarkView.WaterType.TYPE_FESTIVAL)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("name", waterView.getWaterTextByType(TextWaterMarkView.WaterTextEventType.TYPE_SELECT_FESTIVAL_NAME));
                    bundle.putString("date", waterView.getWaterTextByType(TextWaterMarkView.WaterTextEventType.TYPE_SELECT_FESTIVAL_DATE));
//                    ActivityHelper.startActivity(mContext, FestivalSelectActivity.class, bundle);
                    startActivity(FestivalSelectActivity.class, bundle);
                } else if (markIconInfo.type.equals(WaterMarkView.WaterType.TYPE_TEXTEDIT)) {
                    showWaterTextEditDialog(waterView.getWaterTextByType(TextWaterMarkView.WaterTextEventType.TYPE_EDIT_TEXT));
                }
            }

            @Override
            public void onRemoveClick(WaterMarkView view) {
                removeWaterView(view);
            }
        });
        mMarkView.setTag(iconInfo.id);
        if (iconInfo.type.equals(WaterMarkView.WaterType.TYPE_DISTANCE)) {
            String cur_city = SharedPreferencesHelper.readStringValue(mContext, PuTaoConstants.PREFERENCE_CURRENT_CITY);
            if (!StringHelper.isEmpty(cur_city)) {
                mMarkView.setWaterTextByType(TextWaterMarkView.WaterTextEventType.TYPE_SELECT_CURRENT_CITY, cur_city);
            }
            /*if (!GpsUtil.checkGpsState(mContext)) {
                showToast("打开GPS，测测离家还有多远!");
            }*/
        } else if (iconInfo.type.equals(WaterMarkView.WaterType.TYPE_FESTIVAL)) {
            String date = mMarkView.getWaterTextByType(TextWaterMarkView.WaterTextEventType.TYPE_SELECT_FESTIVAL_DATE);
            String new_date = "";
            try {
                new_date = String.valueOf(DateUtil.getDays(date, DateUtil.getStringDateShort()));
            } catch (Exception e) {
            }
            if (!StringHelper.isEmpty(new_date)) {
                mMarkView.setWaterTextByType(TextWaterMarkView.WaterTextEventType.TYPE_SELECT_FESTIVAL_DATE, new_date);
            }
        }
        addWaterView(mMarkView);
    }

    void showWaterTextEditDialog(String def_str) {
        final WaterTextDialog dialog = new WaterTextDialog(this, DisplayHelper.getScreenWidth(), 180, R.layout.dialog_watertext_edit,
                R.style.dialog_style);
        final TextView mMessage = (TextView) dialog.findViewById(R.id.et_input);
        ImageView btn_close = (ImageView) dialog.findViewById(R.id.btn_close);
        ImageView btn_ok = (ImageView) dialog.findViewById(R.id.btn_ok);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("watermark_text", mMessage.getText().toString());
                EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.WATER_MARK_TEXT_EDIT, bundle));
                dialog.dismiss();
            }
        });
        mMessage.setText(def_str);
        mMessage.findFocus();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(mMessage, 0);
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mMessage.getWindowToken(), 0);
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 移除View
     *
     * @param view
     */
    void removeWaterView(WaterMarkView view) {
        mMarkViewTempList.remove(view);
        photo_area_rl.removeView(view);
        mMarkViewList.remove(view);
    }

    /**
     * 添加水印
     *
     * @param view
     */
    void addWaterView(WaterMarkView view) {
        photo_area_rl.addView(view);
        mMarkViewList.add(view);
        mMarkViewTempList.add(view);
    }

    /**
     * 新增一个普通的水印
     *
     * @param iconInfo
     * @param bm
     */
    private void addNormalWaterMarkView(WaterMarkIconInfo iconInfo, Bitmap bm) {
        NormalWaterMarkView mMarkView = new NormalWaterMarkView(mContext, bm, true);
        (mMarkView).setOnRemoveWaterListener(new WaterMarkView.OnRemoveWaterListener() {
            @Override
            public void onRemoveClick(WaterMarkView view) {
                if (mMarkViewList.contains(view)) {
//                    doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_WATER_MARK_DELETE);
                    removeWaterView(view);
                }
            }
        });
        mMarkView.setTag(iconInfo.id);
        addWaterView(mMarkView);
    }

    private void addNormalWaterMarkView(StickerUnZipInfo iconInfo, Bitmap bm) {
        NormalWaterMarkView mMarkView = new NormalWaterMarkView(mContext, bm, true);
        (mMarkView).setOnRemoveWaterListener(new WaterMarkView.OnRemoveWaterListener() {
            @Override
            public void onRemoveClick(WaterMarkView view) {
                if (mMarkViewList.contains(view)) {
//                    doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_WATER_MARK_DELETE);
                    removeWaterView(view);
                }
            }
        });
        //mMarkView.setTag(iconInfo.id);
//        mMarkView.setTag(iconInfo.imgName);
        addWaterView(mMarkView);
    }

    public void loadFilters() {
        List<String> filterEffectNameList = new ArrayList<String>();
        filterEffectNameList.addAll(Arrays.asList(getResources().getStringArray(R.array.filter_effect)));
        if (filter_origin == null) {
            filter_origin = zoomSmall(((BitmapDrawable) getResources().getDrawable(R.drawable.filter_none)).getBitmap());
        }
        for (final String item : filterEffectNameList) {
            new EffectImageTask(filter_origin, item, new EffectImageTask.FilterEffectListener() {
                @Override
                public void rendered(Bitmap bitmap) {
                    if (bitmap != null) {
                        AddFilterView(item, bitmap);
                    }
                }
            }).execute();
        }
    }

    private void AddFilterView(String item, Bitmap bitmap_sample) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.filter_item, null);
        FilterEffectThumbnailView simple_image = (FilterEffectThumbnailView) view.findViewById(R.id.filter_preview);
        simple_image.setImageBitmap(bitmap_sample);
        TextView tv_filter_name = (TextView) view.findViewById(R.id.filter_name);
        tv_filter_name.setText(EffectCollection.getFilterName(item));
        tv_filter_name.setTag(item);
        view.setTag(item);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Umeng事件统计
                HashMap<String, String> filterMap = new HashMap<String, String>();
                filterMap.put((String) view.getTag(), (String) view.getTag());
                mTempFilter = (String) view.getTag();
                new EffectImageTask(corpOriginImageBitmap != null ? corpOriginImageBitmap : ImageCropBitmap, mTempFilter,
                        new EffectImageTask.FilterEffectListener() {
                            @Override
                            public void rendered(Bitmap bitmap) {
                                if (bitmap != null) {
                                    show_image.setImageBitmap(bitmap);
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

    private static Bitmap zoomSmall(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.65f, 0.65f);
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    public void save() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在保存图片...");
        progressDialog.show();
        Bitmap bitmap = createBitmapWithWater();
        final File pictureFile = CommonUtils.getOutputMediaFile();
        FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(pictureFile);
            // keep full quality of the image
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mMarkViewList.size() > 0) {
            for (int i = 0; i < mMarkViewList.size(); i++) {
                photo_area_rl.removeView(mMarkViewList.get(i));
            }
            mMarkViewList.clear();
        }
        MediaScannerConnection.scanFile(this, new String[]{pictureFile.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                Loger.d("scanFile" + "-> uri=" + uri);
                Bundle bundle = new Bundle();
                bundle.putString("savefile", pictureFile.toString());
                bundle.putString("from", "editor");
                EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.PHOTO_CONTENT_PROVIDER_REFRESH, bundle));
                progressDialog.dismiss();
//                ActivityHelper.startActivity(mContext, PhotoShareActivity.class, bundle);
                startActivity(PhotoShareActivity.class, bundle);
                finish();
            }
        });
    }

    private Bitmap createBitmapWithWater() {
        if (mMarkViewList.size() > 0) {
            for (WaterMarkView v : mMarkViewList) {
                v.setEditState(false);
            }
        }
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
        if (corpOriginImageBitmap != null) {
            ImageCropBitmap = corpOriginImageBitmap;
        }
        if (ImageCropBitmap.getHeight() < area_height && ImageCropBitmap.getWidth() < area_width) {
            actual_width = ImageCropBitmap.getWidth();
            actual_height = ImageCropBitmap.getHeight();
        } else {
            float origin_ratio = (float) ImageCropBitmap.getWidth() / ImageCropBitmap.getHeight();
            float current_ratio = (float) area_width / area_height;
            if (origin_ratio >= current_ratio) {
                actual_width = area_width;
                actual_height = (int) (ImageCropBitmap.getHeight() * ((float) actual_width / ImageCropBitmap.getWidth()));
            } else {
                actual_height = area_height;
                actual_width = (int) (ImageCropBitmap.getWidth() * ((float) actual_height) / ImageCropBitmap.getHeight());
            }
        }
        int cut_x = (area_width - actual_width) / 2;
        int cut_y = (area_height - actual_height) / 2;
        return Bitmap.createBitmap(new_bitmap, cut_x, cut_y, actual_width, actual_height);
    }

    private void showWaterMarkContent() {
        mark_content.setVisibility(View.VISIBLE);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mark_content.getLayoutParams();
//        params.width = DisplayHelper.getScreenWidth();
//        params.height = DisplayHelper.getScreenHeight() / 2 + 350;
    }

    void hideMarkContent() {
        mFlagMarkShow = false;
        ObjectAnimator.ofFloat(mark_list_pager, "translationY", 0, mark_content.getHeight()).setDuration(500).start();
    }

    void showMarkContent() {
        mFlagMarkShow = true;

//        applyBlur();

        ObjectAnimator.ofFloat(mark_list_pager, "translationY", (mark_content.getHeight()), 0).setDuration(500).start();
    }

    private void blur(Bitmap bkg, View view) {
        long startMs = System.currentTimeMillis();
        float scaleFactor = 32;
        float radius = 2;

        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor), (int) (view.getMeasuredHeight() / scaleFactor),
                Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0 - title_bar_rl.getHeight() - option_bars.getHeight(), paint);
        canvas.drawColor(0x88ffffff);

        overlay = com.putao.camera.util.FastBlur.doBlur(overlay, (int) radius, true);
        view.setBackgroundDrawable(new BitmapDrawable(getResources(), overlay));
//        Loger.d("blur()++++++time=" + (System.currentTimeMillis() - startMs) + "ms");
    }

    private void applyBlur() {
        mark_list_pager.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                photo_area_rl.setDrawingCacheEnabled(true);
                photo_area_rl.buildDrawingCache();
                final Bitmap bmp = photo_area_rl.getDrawingCache();

                mark_list_pager.getViewTreeObserver().removeOnPreDrawListener(this);

                blur(bmp, mark_list_pager);
                photo_area_rl.setDrawingCacheEnabled(false);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        showQuitTip();
    }

    void showQuitTip() {
        if (!is_edited) {
            finish();
            return;
        }
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


    private int width;
    private int height;
    private Matrix matrix;

    private void cancelEditing() {
        showTitleAni();
        rotate_contanier.setVisibility(View.GONE);
        filter_scrollview.setVisibility(View.GONE);
        mark_content.setVisibility(View.GONE);
        if (mEditAction == EditAction.ACTION_Mark) {
            if (mMarkViewTempList.size() > 0) {
                for (int i = 0; i < mMarkViewTempList.size(); i++) {
                    removeWaterView(mMarkViewTempList.get(i));
                }
            }
            mMarkViewTempList.clear();
        } else if (mEditAction == EditAction.ACTION_FILTER) {
            new EffectImageTask(ImageCropBitmap, mCurrentFilter, mFilterEffectListener).execute();
        }
        if (mEditAction == EditAction.ACTION_Mark) {
//            doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_WATER_MARK_BACKOUT);
        } else if (mEditAction == EditAction.ACTION_FILTER) {
//            doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_FILTER_BACKOUT);
        }
        mEditAction = EditAction.NONE;
    }

    private void saveEditing() {
        showTitleAni();
        rotate_contanier.setVisibility(View.GONE);
        filter_scrollview.setVisibility(View.GONE);
        mark_content.setVisibility(View.GONE);
        mMarkViewTempList.clear();
        mCurrentFilter = mTempFilter;
        if (mEditAction == EditAction.ACTION_Mark) {
//            doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_WATER_MARK_CONFIRM);
        } else if (mEditAction == EditAction.ACTION_FILTER) {
//            doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_FILTER_CONFIRM);
        }
        mEditAction = EditAction.NONE;
        is_edited = true;
    }

    void hideMenuAni() {
        opt_button_bar.setLayoutParams(new RelativeLayout.LayoutParams(option_bars.getWidth(), option_bars.getHeight()));
        ObjectAnimator.ofFloat(opt_button_bar, "translationY", -option_bars.getHeight(), 0).setDuration(500).start();
        mark_content.setLayoutParams(new RelativeLayout.LayoutParams(mark_content.getWidth(), mark_content.getHeight()));
        ObjectAnimator.ofFloat(mark_content, "translationY", 0, -mark_content.getHeight()).setDuration(500).start();

    }

    void showMenuAni() {
        mark_content.setLayoutParams(new RelativeLayout.LayoutParams(mark_content.getWidth(), mark_content.getHeight()));
        ObjectAnimator.ofFloat(mark_content, "translationY", -mark_content.getHeight(), 0).setDuration(500).start();
    }

    void showTitleAni() {
        photo_area_rl.setLayoutParams(new RelativeLayout.LayoutParams(photo_area_rl.getWidth(), photo_area_rl.getHeight()));
        title_bar_rl.setLayoutParams(new RelativeLayout.LayoutParams(title_bar_rl.getWidth(), title_bar_rl.getHeight()));
        opt_button_bar.setLayoutParams(new RelativeLayout.LayoutParams(option_bars.getWidth(), option_bars.getHeight()));
        opt_button_bar2.setLayoutParams(new RelativeLayout.LayoutParams(option_bars.getWidth(), option_bars.getHeight()));
        ObjectAnimator.ofFloat(title_bar_rl, "translationY", -(title_bar_rl.getHeight()), 0).setDuration(500).start();
        ObjectAnimator.ofFloat(photo_area_rl, "translationY", 0, title_bar_rl.getHeight()).setDuration(500).start();
        ObjectAnimator.ofFloat(opt_button_bar, "translationY", -option_bars.getHeight(), 0).setDuration(500).start();
        ObjectAnimator.ofFloat(opt_button_bar2, "translationY", 0, option_bars.getHeight()).setDuration(500).start();
    }

    void hideTitleAni() {
        photo_area_rl.setLayoutParams(new RelativeLayout.LayoutParams(photo_area_rl.getWidth(), photo_area_rl.getHeight()));
        title_bar_rl.setLayoutParams(new RelativeLayout.LayoutParams(title_bar_rl.getWidth(), title_bar_rl.getHeight()));
        opt_button_bar.setLayoutParams(new RelativeLayout.LayoutParams(option_bars.getWidth(), option_bars.getHeight()));
        opt_button_bar2.setLayoutParams(new RelativeLayout.LayoutParams(option_bars.getWidth(), option_bars.getHeight()));
        ObjectAnimator.ofFloat(opt_button_bar2, "translationY", 0, option_bars.getHeight()).setDuration(10).start();
        opt_button_bar2.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(title_bar_rl, "translationY", 0, -(title_bar_rl.getHeight())).setDuration(500).start();
        ObjectAnimator.ofFloat(photo_area_rl, "translationY", title_bar_rl.getHeight(), 0).setDuration(500).start();
        ObjectAnimator.ofFloat(opt_button_bar, "translationY", 0, option_bars.getHeight()).setDuration(500).start();
        ObjectAnimator.ofFloat(opt_button_bar2, "translationY", -option_bars.getHeight(), 0).setDuration(500).start();
    }

    EffectImageTask.FilterEffectListener mFilterEffectListener = new EffectImageTask.FilterEffectListener() {
        @Override
        public void rendered(Bitmap bitmap) {
            if (bitmap != null) {
                show_image.setImageBitmap(bitmap);
            }
        }
    };


}
