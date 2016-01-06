
package com.putao.camera.album;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView.RecyclerListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.putao.camera.R;
import com.putao.camera.album.adapter.PhotoAdapter;
import com.putao.camera.album.view.PhotoListItemClickListener;
import com.putao.camera.album.view.PhotoListItemView;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.bean.PhotoInfo;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.gallery.GalleryFileActivity;
import com.putao.camera.umengfb.UmengFeedbackActivity;
import com.putao.camera.util.Loger;
import com.putao.camera.util.PhotoLoaderHelper;
import com.putao.camera.util.StringHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
//import com.putao.camera.util.UmengAnalysisHelper;

public class PhotoListActivity extends BaseActivity implements PhotoListItemClickListener, View.OnClickListener, RecyclerListener {
    private ListView body_lv;
    private ImageView nonePhotoIV;
    private Map<String, ArrayList<PhotoInfo>> mPhotoMapCategoryByDate;
    private boolean bMultiSelectState = false;
    private Button back_btn, btn_album_del, feedback_btn;
    private PhotoAdapter mPhotoAdapter;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SetAlbumPhotos:
                    RefreshAlbumData();
                    break;
                case GetAlbumPhotos:
                    getData();
                    break;
                default:
                    break;
            }
        }

        ;
    };
    private final static int SetAlbumPhotos = 1;
    private final static int GetAlbumPhotos = 2;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_photo_list;
    }

    @Override
    public void doInitSubViews(View view) {
        body_lv = (ListView) this.findViewById(R.id.body_lv);
        body_lv.setRecyclerListener(this);
        nonePhotoIV = (ImageView) this.findViewById(R.id.body_iv_none);
        back_btn = (Button) this.findViewById(R.id.back_btn);
        btn_album_del = (Button) this.findViewById(R.id.btn_album_del);
        feedback_btn = (Button) this.findViewById(R.id.feedback_btn);
        addOnClickListener(back_btn, btn_album_del, feedback_btn);
        EventBus.getEventBus().register(this);
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void doInitData() {
        mPhotoMapCategoryByDate = new HashMap<String, ArrayList<PhotoInfo>>();
        mPhotoAdapter = new PhotoAdapter(mContext, this, mPhotoMapCategoryByDate);
        body_lv.setAdapter(mPhotoAdapter);
        getData();

    }

    @Override
    public void onBackPressed() {
        if (bMultiSelectState) {
            getData();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getEventBus().unregister(this);
    }

    void RefreshAlbumData() {
        bMultiSelectState = false;
        mPhotoAdapter.setMultSelectState(bMultiSelectState);
        mPhotoAdapter.setData(mPhotoMapCategoryByDate);
        mPhotoAdapter.notifyDataSetChanged();
        btn_album_del.setVisibility(View.GONE);
        /**
         * 应用相册空时，显示提示
         */
        if (mPhotoMapCategoryByDate.size() > 0) {
            nonePhotoIV.setVisibility(View.INVISIBLE);
        } else {
            nonePhotoIV.setVisibility(View.VISIBLE);
        }
    }

    void getData() {
        final ProgressDialog dialog = new AlbumProcessDialog(mContext, "正在读取拍拍照片...").Get();
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPhotoMapCategoryByDate = PhotoLoaderHelper.getInstance(mActivity).getPhotoMapCategoryByDate(true);
                sendHandleMessage(SetAlbumPhotos);
                dialog.dismiss();
            }
        }).start();
    }

    private void recyleImageView() {
        for (int i = 0; i < mPhotoAdapter.getCount(); i++) {
            View view = getViewByPosition(i, body_lv);
            PhotoListItemView aPhotoListItemView = (PhotoListItemView) view.findViewById(R.id.photo_list_item_view);
            aPhotoListItemView.recyleAllImageView();
        }
    }

    @Override
    public void onPhotoListItemClick(PhotoInfo info) {
        if (!bMultiSelectState)//编辑状态下，屏蔽单击事件
        {
            //            Bundle bundle = new Bundle();
            //            bundle.putSerializable("photoinfo", info);
            //            ActivityHelper.startActivity(mActivity, PhotoEditorActivity.class, bundle);
            Intent intent = new Intent(this, GalleryFileActivity.class);
            intent.putExtra("select_photo_id", info._ID);
            this.startActivity(intent);
        }
    }

    @Override
    public void onPhotoListItemLongClick(PhotoInfo info) {
        /*
         * Umeng事件统计
         */
//        UmengAnalysisHelper.onEvent(mContext, UmengAnalysisConstants.UMENG_COUNT_EVENT_PHOTO_LIST_SELECT.toString());
        bMultiSelectState = !bMultiSelectState;
        mPhotoAdapter.setMultSelectState(bMultiSelectState);
        mPhotoAdapter.ChangePhotoCheckedState(info, true);
        mPhotoAdapter.notifyDataSetChanged();
        btn_album_del.setVisibility(bMultiSelectState ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onCheckedChanged(PhotoInfo PhotoInfo, boolean isChecked) {
        mPhotoAdapter.ChangePhotoCheckedState(PhotoInfo, isChecked);
    }

    public ArrayList<PhotoInfo> queryAllSelectedPath() {
        ArrayList<PhotoInfo> allArray = new ArrayList<PhotoInfo>();
        for (int i = 0; i < mPhotoAdapter.getCount(); i++) {
            View view = getViewByPosition(i, body_lv);
            PhotoListItemView aPhotoListItemView = (PhotoListItemView) view.findViewById(R.id.photo_list_item_view);
            ArrayList<PhotoInfo> photoInfoArray = aPhotoListItemView.getSelectedViewPhotoInfos();
            allArray.addAll(photoInfoArray);
        }
        return allArray;
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;
        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    void doDelAlbumPhotos() {
        final ArrayList<PhotoInfo> photolist = queryAllSelectedPath();
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
                sendHandleMessage(GetAlbumPhotos);
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
            case R.id.btn_album_del:
                /*
                 * Umeng事件统计
                 */
//                UmengAnalysisHelper.onEvent(mContext, UmengAnalysisConstants.UMENG_COUNT_EVENT_PHOTO_LIST_DELETE.toString());
                doDelAlbumPhotos();
                break;
            case R.id.back_btn:
                /*
                 * Umeng事件统计
                 */
//                UmengAnalysisHelper.onEvent(mContext, UmengAnalysisConstants.UMENG_COUNT_EVENT_PHOTO_LIST_BACK.toString());
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
                startActivity(new Intent(mContext, UmengFeedbackActivity.class));
                break;
            default:
                break;
        }
    }

    public void onEvent(BasePostEvent event) {
        switch (event.eventCode) {
            case PuTaoConstants.PHOTO_CONTENT_PROVIDER_REFRESH:
                sendHandleMessage(GetAlbumPhotos);
                break;
            case PuTaoConstants.GALLERY_GO_TO_TAKE_PICTURE_CLOSE:
                this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onMovedToScrapHeap(View view) {
        PhotoListItemView aPhotoListItemView = (PhotoListItemView) view.findViewById(R.id.photo_list_item_view);
        aPhotoListItemView.recyleAllImageView();
    }
}
