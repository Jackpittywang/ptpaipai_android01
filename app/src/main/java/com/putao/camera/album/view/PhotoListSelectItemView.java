
package com.putao.camera.album.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.putao.camera.R;
import com.putao.camera.bean.PhotoInfo;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.Loger;
import com.putao.camera.util.PhotoLoaderHelper;

public class PhotoListSelectItemView extends RelativeLayout {
    private HighLightImageView photo_body_iv;
    private CheckBox photo_selected_cb;
    private PhotoInfo mPhotoInfo;
    private Bitmap mBitmap;
    private PhotoListItemClickListener itemClickListener;
    private boolean isEditState = false;

    public PhotoListSelectItemView(Context context, PhotoListItemClickListener listener) {
        super(context);
        itemClickListener = listener;
        init(context);
    }

    public void setPhotoListItemClickListener(PhotoListItemClickListener listener) {
        itemClickListener = listener;
    }

    public PhotoListSelectItemView(Context context, AttributeSet attr) {
        super(context, attr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_photo_list_select_item, this);
        photo_body_iv = (HighLightImageView) this.findViewById(R.id.photo_body_iv);
        photo_selected_cb = (CheckBox) this.findViewById(R.id.photo_selected_cb);
        photo_selected_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (itemClickListener != null)
                    itemClickListener.onCheckedChanged(mPhotoInfo, isChecked);
            }
        });
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Loger.d("photo item onclick.......EditState:" + isEditState);
                if (itemClickListener != null) {
                    itemClickListener.onPhotoListItemClick(mPhotoInfo);
                }
                if (isEditState) {
                    if (itemClickListener != null) {
                        photo_selected_cb.setChecked(!mPhotoInfo.Checked);
                    }
                }
            }
        });
        this.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (itemClickListener != null)
                    itemClickListener.onPhotoListItemLongClick(mPhotoInfo);
                return true;
            }
        });
    }

    public void setPhotoInfo(PhotoInfo info) {
        mPhotoInfo = info;
        this.setTag(info);
        queryImageView(info._ID_LONG);
        photo_selected_cb.setChecked(info.Checked);
    }

    Handler aHanlder = new Handler() {
        public void handleMessage(Message msg) {
            setImageBitmap(mBitmap, mPhotoInfo);
        }

        ;
    };

    public void queryImageView111(final String id) {
        mBitmap = PhotoLoaderHelper.getThumbnailLocalBitmap(id);
        setImageBitmap(mBitmap, mPhotoInfo);
    }

    public void queryImageView(long id) {

        //                String imageUrl = PhotoLoaderHelper.getLocalThumbnailPath(id);
        String imageUrl = PhotoLoaderHelper.getThumbnailPathForLocalFile(id);
        //显示图片的配置    
        DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(BitmapHelper.getLoadingDrawable()).showImageOnFail(BitmapHelper.getLoadingDrawable())
                .cacheInMemory(true).cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        photo_body_iv.setScaleType(ScaleType.CENTER_CROP);
        photo_body_iv.setTag(mPhotoInfo);
        ImageLoader.getInstance().displayImage(imageUrl, photo_body_iv, options);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    void setImageBitmap(Bitmap bitmap, PhotoInfo info) {
        photo_body_iv.setImageBitmap(bitmap);
        photo_body_iv.setScaleType(ScaleType.CENTER_CROP);
        photo_body_iv.setTag(info);
    }

    public void setViewEditStatus(boolean bEdit) {
        isEditState = bEdit;
        photo_body_iv.setViewEditStatus(bEdit);
        photo_selected_cb.setVisibility(bEdit ? View.VISIBLE : View.GONE);
    }

    public void recyleBitmap() {
        if (mBitmap != null) {
            mBitmap.recycle();
        }
    }

    public boolean isChecked() {
        return photo_selected_cb.isChecked();
    }

    public PhotoInfo getPhotoInfo() {
        return mPhotoInfo;
    }
}
