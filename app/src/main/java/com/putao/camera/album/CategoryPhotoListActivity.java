package com.putao.camera.album;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.putao.camera.R;
import com.putao.camera.album.adapter.CategoryPhotoAdapter;
import com.putao.camera.album.view.PhotoListItemClickListener;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.bean.PhotoInfo;
import com.putao.camera.collage.adapter.GalleryListAdapter;
import com.putao.camera.collage.mode.GalleryEntity;
import com.putao.camera.collage.util.CollagePhotoUtil;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.constants.UmengAnalysisConstants;
import com.putao.camera.editor.PhotoEditorActivity;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.movie.MoviePhotoCutActivity;
import com.putao.camera.util.Loger;
import com.putao.camera.util.PhotoLoaderHelper;
import com.putao.camera.util.StringHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;


public class CategoryPhotoListActivity extends BaseActivity implements PhotoListItemClickListener, View.OnClickListener {
    private ListView body_lv;
    private ImageView empty_iv;
    private Map<String, ArrayList<PhotoInfo>> mPhotoMapCategoryByDate;
    private final static int SET_ALBUM_PHOTOS = 1;
    private final static int GET_ALBUM_PHOTOS = 2;
    private final static int SetGalleryList = 3;
    private Handler mHandler;
    private CategoryPhotoAdapter mCategoryPhotoAdapter;
    private boolean bMultiSelectState = false;
    private Button back_btn, btn_album_del, feedback_btn;
    private ListView layout_gallery_list;
    private LinearLayout sl_gallery_list;
    ArrayList<GalleryEntity> mGalleryList;
    private boolean mGalleryShow = false;
    private RelativeLayout gallery_list_panel;

    boolean isFromMovie = false;
    boolean isFromConnectPhoto = false;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_photo_list;
    }

    @Override
    public void doInitSubViews(View view) {
        body_lv = (ListView) this.findViewById(R.id.body_lv);
        back_btn = (Button) this.findViewById(R.id.back_btn);
        btn_album_del = (Button) this.findViewById(R.id.btn_album_del);
        feedback_btn = (Button) this.findViewById(R.id.feedback_btn);
        addOnClickListener(back_btn, btn_album_del, feedback_btn);
        empty_iv = (ImageView) this.findViewById(R.id.body_iv_none);

        EventBus.getEventBus().register(this);

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
    }

    @Override
    public void doInitData() {

        Intent intent = this.getIntent();
        isFromMovie = intent.getBooleanExtra("from_movie", false);
        isFromConnectPhoto = intent.getBooleanExtra("from_connect_photo", false);

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
                    case SetGalleryList:
                        loadGalleryList();
                        break;
                    default:
                        break;
                }
            }
        };
        mCategoryPhotoAdapter = new CategoryPhotoAdapter(mContext);
        mCategoryPhotoAdapter.setPhotoListItemClickListener(this);
        body_lv.setAdapter(mCategoryPhotoAdapter);
        queryLocalPhoto();
        //UmengUpdateHelper.getInstance().autoUpdate(this);
        getGallery();
    }

    void showGalleryList() {
        ObjectAnimator.ofFloat(sl_gallery_list, "translationX", 0, sl_gallery_list.getWidth()).setDuration(10).start();
        sl_gallery_list.setVisibility(View.VISIBLE);
        gallery_list_panel.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(sl_gallery_list, "translationX", sl_gallery_list.getWidth(), 0).setDuration(300).start();
        mGalleryShow = true;
    }

    void hideGalleryLsit() {
        ObjectAnimator.ofFloat(sl_gallery_list, "translationX", 0, sl_gallery_list.getWidth()).setDuration(300).start();
        mGalleryShow = false;
        gallery_list_panel.postDelayed(new Runnable() {
            @Override
            public void run() {
                gallery_list_panel.setVisibility(View.GONE);
            }
        }, 550);
    }

    void loadGalleryList() {
//        ArrayList<GalleryEntity> galleryList = CollagePhotoUtil.QueryALLGalleryList(mActivity);
//        for (GalleryEntity item : galleryList) {
//            if (item.getBucket_name().equalsIgnoreCase("watermark")) {
//                continue;
//            }
//            View view = LayoutInflater.from(mActivity).inflate(R.layout.gallery_list_item, null);
//            ImageView imageView = (ImageView) view.findViewById(R.id.gallery_image);
//            TextView textView = (TextView) view.findViewById(R.id.gallery_name);
//            Bitmap bitmap = BitmapHelper.getInstance().getCenterCropBitmap(item.getImage_path(), 200, 200);
////            int min_size = Math.min(bitmap.getWidth(), bitmap.getHeight());
////            if (min_size < 200) {
////                Matrix matrix = new Matrix();
////                float ratio = 200.0f / min_size;
////                matrix.postScale(ratio, ratio);
////                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
////            }
//            imageView.setImageBitmap(bitmap);
//            imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    GalleryEntity entity = (GalleryEntity) v.getTag();
////                    reSetAlbumData(entity.getBucket_id());
//                    queryLocalPhotoByBUCKET_ID(entity.getBucket_id());
//                    hideGalleryLsit();
//                }
//            });
//            textView.setText(item.getBucket_name() + "(" + item.getCount() + ")");
//            imageView.setTag(item);
//            layout_gallery_list.addView(view);
//        }

        mGalleryList = CollagePhotoUtil.QueryALLGalleryList(mActivity);
        GalleryListAdapter galleryAdapter = new GalleryListAdapter(mContext, mGalleryList);
        layout_gallery_list.setAdapter(galleryAdapter);
        layout_gallery_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GalleryEntity entity = mGalleryList.get(position);
                queryLocalPhotoByBUCKET_ID(entity.getBucket_id());
                hideGalleryLsit();
            }
        });
    }

    void getGallery() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                mGalleryList = CollagePhotoUtil.QueryALLGalleryList(mContext);
                sendHandleMessage(SetGalleryList);
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        EventBus.getEventBus().unregister(this);
    }

    void doRefreshAlbumData() {
        bMultiSelectState = false;
        mCategoryPhotoAdapter.setMultSelectState(bMultiSelectState);
        mCategoryPhotoAdapter.setData(mPhotoMapCategoryByDate);
        mCategoryPhotoAdapter.notifyDataSetChanged();
        //        mPhotoAdapter.notifyDataSetChanged();
        btn_album_del.setVisibility(View.GONE);
        //        /**
        //         * 应用相册空时，显示提示
        //         */
        if (mPhotoMapCategoryByDate.size() > 0) {
            empty_iv.setVisibility(View.INVISIBLE);
        } else {
            empty_iv.setVisibility(View.VISIBLE);
        }
    }

    void queryLocalPhotoByBUCKET_ID(final int bucket_id) {
        final ProgressDialog dialog = new AlbumProcessDialog(mContext, "正在读取选中相册的照片...").Get();
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPhotoMapCategoryByDate = PhotoLoaderHelper.getInstance(mActivity).getPhotoMapCategoryByDate(true, bucket_id);
                sendHandleMessage(SET_ALBUM_PHOTOS);
                dialog.dismiss();
            }
        }).start();

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
    public void onBackPressed() {
        if (bMultiSelectState) {
            queryLocalPhoto();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPhotoListItemClick(PhotoInfo info) {
        if (!bMultiSelectState)//编辑状态下，屏蔽单击事件
        {
            if (isFromMovie) {
                //跳转到movie剪切
                Intent intent = new Intent(this, MoviePhotoCutActivity.class);
                intent.putExtra("photo_data", info._DATA);
                this.startActivity(intent);
            } else if (isFromConnectPhoto) {
                Bundle bundle = new Bundle();
                bundle.putString("photo_path", info._DATA);
                EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.EVENT_CONNECT_PHOTO_SELECT, bundle));
                finish();
            } else {//图片编辑
                Intent intent = new Intent(this, PhotoEditorActivity.class);
                intent.putExtra("select_photo_id", info._ID);
                intent.putExtra("photo_data", info._DATA);
                intent.putExtra("from", "category");
                this.startActivity(intent);
            }
        }
    }

    @Override
    public void onPhotoListItemLongClick(PhotoInfo info) {
        /*
         * Umeng事件统计
         */
        doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_PHOTO_LIST_SELECT.toString());
        bMultiSelectState = !bMultiSelectState;
        mCategoryPhotoAdapter.setMultSelectState(bMultiSelectState);
        mCategoryPhotoAdapter.ChangePhotoCheckedState(info, true);
        mCategoryPhotoAdapter.notifyDataSetChanged();
        btn_album_del.setVisibility(bMultiSelectState ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onCheckedChanged(PhotoInfo PhotoInfo, boolean isChecked) {
        mCategoryPhotoAdapter.ChangePhotoCheckedState(PhotoInfo, isChecked);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_album_del:
                /*
                 * Umeng事件统计
                 */
                doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_PHOTO_LIST_DELETE.toString());
                doDelAlbumPhotos();
                break;
            case R.id.back_btn:
                /*
                 * Umeng事件统计
                 */
                doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_PHOTO_LIST_BACK.toString());
                finish();
                break;
            case R.id.feedback_btn:
                /*FeedbackAgent agent = new FeedbackAgent(mContext);
                agent.startFeedbackActivity();*/
                /*Intent intent = new Intent();
                intent.setClass(mContext, ConversationDetailActivity.class);
                String id = new FeedbackAgent(mContext).getDefaultConversation().getId();
                intent.putExtra(FeedbackFragment.BUNDLE_KEY_CONVERSATION_ID, id);
                startActivity(intent);*/
                //startActivity(new Intent(mContext, UmengFeedbackActivity.class));

                if (mGalleryShow) {
                    hideGalleryLsit();
                } else {
                    showGalleryList();
                }
                break;
            default:
                break;
        }
    }

    void doDelAlbumPhotos() {
        final ArrayList<PhotoInfo> photolist = mCategoryPhotoAdapter.queryAllSelectedPhotoInfo();
        if (photolist.size() <= 0) {
            return;
        }
        final ProgressDialog showdialog = new AlbumProcessDialog(mContext, "正在删除照片...").Get();
        showdialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Iterator<PhotoInfo> iterator = photolist.iterator();
                while (iterator.hasNext()) {
                    PhotoInfo info = iterator.next();
                    Loger.d("this file will be deleted::" + info._DATA);
                    if (!StringHelper.isEmpty(info._DATA)) {
                        File file = new File(info._DATA);
                        try {
                            file.delete();
                            int result = PhotoLoaderHelper.DeleteFileFromDB(info._ID);
                            Loger.d("delete file ok::" + result);
                        } catch (Exception e) {
                            Loger.d("delete file failed::" + info._DATA);
                        }
                    }
                }
                showdialog.dismiss();
                sendHandleMessage(GET_ALBUM_PHOTOS);
            }
        }).start();
    }


    public void onEvent(BasePostEvent event) {
        switch (event.eventCode) {
            case PuTaoConstants.PHOTO_CONTENT_PROVIDER_REFRESH:
                sendHandleMessage(GET_ALBUM_PHOTOS);
                break;
            case PuTaoConstants.GALLERY_GO_TO_TAKE_PICTURE_CLOSE:
                this.finish();
                break;
            default:
                break;
        }
    }
}
