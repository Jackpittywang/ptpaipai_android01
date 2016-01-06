//

package com.putao.camera.album.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.putao.camera.bean.PhotoInfo;
import com.putao.camera.util.DisplayHelper;

public class PhotoListItemView extends ViewGroup {
    private ArrayList<PhotoInfo> mArrayPhotoInfo;
    private Context mContext;
    int cellWidthSize = 0, cellHeightSize = 0;
    int width, height;

    public PhotoListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int index = 0; index < getChildCount(); index++) {
            final View child = getChildAt(index);
            child.measure(MeasureSpec.makeMeasureSpec(cellWidthSize, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(cellHeightSize, MeasureSpec.EXACTLY));
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean arg0, int left, int top, int right, int bottom) {
        // TODO Auto-generated method stub
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            childView.layout(left, top, left + cellWidthSize, top + cellHeightSize);
            if ((i + 1) % 3 == 0) {
                left = 0;
                top += cellHeightSize;
            } else {
                left += cellWidthSize;
            }
        }
    }

    public void setListItemView(ArrayList<PhotoInfo> arrayInfo, PhotoListItemClickListener Clicklistener, boolean isEditting) {
        this.removeAllViews();
        mArrayPhotoInfo = arrayInfo;
        for (int i = mArrayPhotoInfo.size(); i > 0; i--) {
            PhotoInfo info = mArrayPhotoInfo.get(i - 1);
            PhotoListSelectItemView photoListSelectItemView = new PhotoListSelectItemView(mContext, Clicklistener);
            photoListSelectItemView.setPhotoInfo(info);
            photoListSelectItemView.setViewEditStatus(isEditting);
            this.addView(photoListSelectItemView);
        }
        width = DisplayHelper.getScreenWidth();
        cellWidthSize = width / 3;
        cellHeightSize = (int) (cellWidthSize * 1.5);
        height = ((this.getChildCount() - 1) / 3 + 1) * cellHeightSize;
        this.invalidate();
    }

    public ArrayList<PhotoInfo> getSelectedViewPhotoInfos() {
        ArrayList<PhotoInfo> array = new ArrayList<PhotoInfo>();
        for (int i = 0; i < getChildCount(); i++) {
            PhotoListSelectItemView photoListSelectItemView = ((PhotoListSelectItemView) getChildAt(i));
            if (photoListSelectItemView.isChecked()) {
                array.add(photoListSelectItemView.getPhotoInfo());
            }
        }
        return array;
    }

    public void recyleAllImageView() {
        for (int i = 0; i < getChildCount(); i++) {
            ((PhotoListSelectItemView) getChildAt(i)).recyleBitmap();
        }
    }
}
