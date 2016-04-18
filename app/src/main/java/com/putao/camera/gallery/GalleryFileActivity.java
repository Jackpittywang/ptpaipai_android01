/*
 Copyright (c) 2012 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.putao.camera.gallery;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import com.putao.camera.R;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.bean.PhotoInfo;
import com.putao.camera.camera.view.RedPointButton;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.editor.PhotoEditorActivity;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.PhotoLoaderHelper;
import com.putao.widget.touchgallery.GalleryWidget.BasePagerAdapter.OnItemChangeListener;
import com.putao.widget.touchgallery.GalleryWidget.FilePagerAdapter;
import com.putao.widget.touchgallery.GalleryWidget.GalleryViewPager;

import java.util.ArrayList;

//import com.putao.camera.constants.UmengAnalysisConstants;
//import com.putao.camera.thirdshare.dialog.ThirdShareDialog;

public class GalleryFileActivity extends BaseActivity implements View.OnClickListener {
    private GalleryViewPager mViewPager;
    private RedPointButton choice_water_mark_btn;
    private Button choice_filter_btn, back_btn, camera_btn, album_share_btn;
    private ArrayList<PhotoInfo> mPhotosArray;
    private ArrayList<String> mPathItems;
    private int mCurrentIndex = 0;
    private FilePagerAdapter pagerAdapter;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_gallery_list_shower;
    }

    @Override
    public void doInitSubViews(View view) {
        mViewPager = (GalleryViewPager) findViewById(R.id.viewer);
        choice_water_mark_btn = (RedPointButton) findViewById(R.id.choice_water_mark_btn);
        choice_filter_btn = (Button) findViewById(R.id.btn_picture_filter);
        back_btn = (Button) findViewById(R.id.back_btn);
        camera_btn = (Button) findViewById(R.id.camera_btn);
        album_share_btn = (Button) findViewById(R.id.album_share_btn);
        choice_water_mark_btn.setOnClickListener(this);
        choice_filter_btn.setOnClickListener(this);
        back_btn.setOnClickListener(this);
        camera_btn.setOnClickListener(this);
        album_share_btn.setOnClickListener(this);
    }

    @Override
    public void doInitData() {
        mPathItems = new ArrayList<String>();
        String select_photo_id = this.getIntent().getStringExtra("select_photo_id");
        mPhotosArray = PhotoLoaderHelper.getInstance(this).getPhotoInfoArray();
        for (int i = 0; i < mPhotosArray.size(); i++) {
            PhotoInfo info = mPhotosArray.get(i);
            mPathItems.add(info._DATA);
            if (select_photo_id.equals(info._ID)) {
                mCurrentIndex = i;
            }
        }
        pagerAdapter = new FilePagerAdapter(this, mPathItems);
        pagerAdapter.setOnItemChangeListener(new OnItemChangeListener() {
            @Override
            public void onItemChange(int currentPosition) {
                /*
                 * Umeng事件统计
                 */
//                if (mCurrentIndex > currentPosition)
//                {
//                    UmengAnalysisHelper.onEvent(mActivity, UmengAnalysisConstants.UMENG_COUNT_EVENT_PHOTO_PREVIOUS);
//                }
//                else if (mCurrentIndex < currentPosition)
//                {
//                    UmengAnalysisHelper.onEvent(mActivity, UmengAnalysisConstants.UMENG_COUNT_EVENT_PHOTO_NEXT);
//                }
                mCurrentIndex = currentPosition;
            }
        });
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(mCurrentIndex);
    }

    public String getCurrentPath() {
        return mPathItems.get(pagerAdapter.getCurrentPosition());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choice_water_mark_btn: {
                /*
                 * Umeng事件统计
                 */
//                UmengAnalysisHelper.onEvent(mActivity, UmengAnalysisConstants.UMENG_COUNT_EVENT_PHOTO_WATER_MARK);
                finish();
                int index = mViewPager.getCurrentItem();
                PhotoInfo info = mPhotosArray.get(index);
                Bundle bundle = new Bundle();
                bundle.putSerializable("photoinfo", info);
                bundle.putBoolean("bShowWaterMark", true);
                ActivityHelper.startActivity(mActivity, PhotoEditorActivity.class, bundle, true);
            }
            break;
            case R.id.back_btn:
                /*
                 * Umeng事件统计
                 */
//                UmengAnalysisHelper.onEvent(mActivity, UmengAnalysisConstants.UMENG_COUNT_EVENT_PHOTO_BACK);
                finish();
                break;
            case R.id.btn_picture_filter: {
                /*
                 * Umeng事件统计
                 */
//                UmengAnalysisHelper.onEvent(mActivity, UmengAnalysisConstants.UMENG_COUNT_EVENT_PHOTO_FILTER);
                finish();
                int index = mViewPager.getCurrentItem();
                PhotoInfo info = mPhotosArray.get(index);
                Bundle bundle = new Bundle();
                bundle.putSerializable("photoinfo", info);
                bundle.putBoolean("bShowFilter", true);
                ActivityHelper.startActivity(mActivity, PhotoEditorActivity.class, bundle, true);
            }
            break;
            case R.id.camera_btn:
                /*
                 * Umeng事件统计
                 */
//                UmengAnalysisHelper.onEvent(mActivity, UmengAnalysisConstants.UMENG_COUNT_EVENT_PHOTO_CAMERA);
                finish();
                Bundle bundle = new Bundle();
                EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.GALLERY_GO_TO_TAKE_PICTURE_CLOSE, bundle));
                break;
            case R.id.album_share_btn:
                /*
                 * Umeng事件统计
                 */
//                UmengAnalysisHelper.onEvent(mActivity, UmengAnalysisConstants.UMENG_COUNT_EVENT_PHOTO_SHARE);
//                ThirdShareDialog dialog = new ThirdShareDialog();
                Bundle bundle1 = new Bundle();
                bundle1.putString("url", getCurrentPath());
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                dialog.show(ft, bundle1);
                //                sendMultiMessage(true, false, false, false, false, false);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //        Loger.i("GalleryFileActivity onDestroy():-----------------");
        //        Loger.i("GalleryFileActivity getCurrentItem():-----------------"+mViewPager.getCurrentItem());
        //        int index = mViewPager.getCurrentItem();
        //        View view = mViewPager.getChildAt(index);
        //
        //        if (view != null)
        //        {
        //            Bitmap bitmap = ((BitmapDrawable) ((FileTouchImageView) view).mImageView.getDrawable()).getBitmap();
        //            bitmap.recycle();
        //            Loger.i("bitmap.recycle();:-----------------");
        //        }
        System.gc();
    }
    //    private void recyleImageView()
    //    {
    //        for (int i = 0; i < pagerAdapter.getCount(); i++)
    //        {
    //            View view = getViewByPosition(i, body_lv);
    //            PhotoListItemView aPhotoListItemView = (PhotoListItemView) view.findViewById(R.id.photo_list_item_view);
    //            aPhotoListItemView.recyleAllImageView();
    //        }
    //    }
    //
    //    public View getViewByPosition(int pos ,GalleryViewPager aViewPager)
    //    {
    //        aViewPager
    //        mViewPager.getViewByPosition()
    //        final int firstListItemPosition = listView.getFirstVisiblePosition();
    //        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;
    //        if (pos < firstListItemPosition || pos > lastListItemPosition)
    //        {
    //            return listView.getAdapter().getView(pos, null, listView);
    //        }
    //        else
    //        {
    //            final int childIndex = pos - firstListItemPosition;
    //            return listView.getChildAt(childIndex);
    //        }
    //    }
}