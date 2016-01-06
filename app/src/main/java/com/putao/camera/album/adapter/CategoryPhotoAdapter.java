
package com.putao.camera.album.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.putao.camera.R;
import com.putao.camera.album.view.CategoryPhotoListItemInfo;
import com.putao.camera.album.view.PhotoListItemClickListener;
import com.putao.camera.album.view.PhotoListSelectItemView;
import com.putao.camera.bean.PhotoInfo;
import com.putao.camera.util.DateUtil;

public class CategoryPhotoAdapter extends BaseAdapter {
    private Map<String, ArrayList<PhotoInfo>> mHashMap;
    private Context mContext;
    private PhotoListItemClickListener mPhotoListItemClickListener;
    private boolean bMultiSelectState = false;
    private ArrayList<CategoryPhotoListItemInfo> mCategoryPhotoListItemInfoArray;
    private int cur_date_bg_res = 0;

    public CategoryPhotoAdapter(Context aContext) {
        this.mContext = aContext;
    }

    public void setPhotoListItemClickListener(PhotoListItemClickListener aPhotoListItemClickListener) {
        mPhotoListItemClickListener = aPhotoListItemClickListener;
    }

    public void setData(Map<String, ArrayList<PhotoInfo>> map) {
        this.mHashMap = map;
        processData();
    }

    @Override
    public int getCount() {
        if (mCategoryPhotoListItemInfoArray != null) {
            return mCategoryPhotoListItemInfoArray.size();
        }
        return 0;
    }

    void processData() {
        mCategoryPhotoListItemInfoArray = new ArrayList<CategoryPhotoListItemInfo>();
        Iterator iter = mHashMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            ArrayList<PhotoInfo> dateArray = (ArrayList<PhotoInfo>) entry.getValue();
            CategoryPhotoListItemInfo categroyInfo = null;
            int size = dateArray.size();
            for (int i = 0; i < size; i++) {
                int index = i % 3;
                if (index == 0) {
                    categroyInfo = new CategoryPhotoListItemInfo();
                    mCategoryPhotoListItemInfoArray.add(categroyInfo);
                    categroyInfo.setDate((String) entry.getKey());
                }
                //最后的照片显示最前面
                int lastIndex = size - 1 - i;
                switch (index) {
                    case 0:
                        categroyInfo.setPhotoInfo1(dateArray.get(lastIndex));
                        break;
                    case 1:
                        categroyInfo.setPhotoInfo2(dateArray.get(lastIndex));
                        break;
                    case 2:
                        categroyInfo.setPhotoInfo3(dateArray.get(lastIndex));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public CategoryPhotoListItemInfo getItem(int position) {
        // TODO Auto-generated method stub
        return mCategoryPhotoListItemInfoArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public void setMultSelectState(boolean state) {
        bMultiSelectState = state;
    }

    /**
     * 根据PhotoInfo 更新选择状态
     *
     * @param info
     * @param checked
     */
    public void ChangePhotoCheckedState(PhotoInfo info, boolean checked) {
        //                for (int i = 0; i < mArrayList.size(); i++)
        //                {
        //                    ArrayList<PhotoInfo> photos = mArrayList.get(i);
        //                    for (int j = 0; j < photos.size(); j++)
        //                    {
        //                        if (photos.get(j)._ID.equals(info._ID))
        //                        {
        //                            mArrayList.get(i).get(j).Checked = checked;
        //                            return;
        //                        }
        //                    }
        //                }
        for (int i = 0; i < mCategoryPhotoListItemInfoArray.size(); i++) {
            CategoryPhotoListItemInfo categoryInfo = mCategoryPhotoListItemInfoArray.get(i);
            if (categoryInfo.getPhotoInfo1() != null && categoryInfo.getPhotoInfo1()._ID.equals(info._ID)) {
                categoryInfo.getPhotoInfo1().Checked = checked;
            }
            if (categoryInfo.getPhotoInfo2() != null && categoryInfo.getPhotoInfo2()._ID.equals(info._ID)) {
                categoryInfo.getPhotoInfo2().Checked = checked;
            }
            if (categoryInfo.getPhotoInfo3() != null && categoryInfo.getPhotoInfo3()._ID.equals(info._ID)) {
                categoryInfo.getPhotoInfo3().Checked = checked;
            }
        }
    }

    public ArrayList<PhotoInfo> queryAllSelectedPhotoInfo() {
        ArrayList<PhotoInfo> allArray = new ArrayList<PhotoInfo>();
        for (int i = 0; i < mCategoryPhotoListItemInfoArray.size(); i++) {
            CategoryPhotoListItemInfo categoryInfo = mCategoryPhotoListItemInfoArray.get(i);
            if (categoryInfo.getPhotoInfo1() != null && categoryInfo.getPhotoInfo1().Checked) {
                allArray.add(categoryInfo.getPhotoInfo1());
            }
            if (categoryInfo.getPhotoInfo2() != null && categoryInfo.getPhotoInfo2().Checked) {
                allArray.add(categoryInfo.getPhotoInfo2());
            }
            if (categoryInfo.getPhotoInfo3() != null && categoryInfo.getPhotoInfo3().Checked) {
                allArray.add(categoryInfo.getPhotoInfo3());
            }
        }
        return allArray;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        PhotoItemHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_photo_date_category2_item, null);
            holder = new PhotoItemHolder();
//            holder.date_tv = (TextView) convertView.findViewById(R.id.date_tv);
            holder.week_tv = (TextView) convertView.findViewById(R.id.week_tv);
            holder.day_tv = (TextView) convertView.findViewById(R.id.day_tv);
            holder.title_rl = (RelativeLayout) convertView.findViewById(R.id.title_rl);
            holder.photo_list_item1_view = (PhotoListSelectItemView) convertView.findViewById(R.id.photo_list_item1_view);
            holder.photo_list_item2_view = (PhotoListSelectItemView) convertView.findViewById(R.id.photo_list_item2_view);
            holder.photo_list_item3_view = (PhotoListSelectItemView) convertView.findViewById(R.id.photo_list_item3_view);


            convertView.setTag(holder);
        } else {
            holder = (PhotoItemHolder) convertView.getTag();
        }
        CategoryPhotoListItemInfo aCategoryPhotoListItemInfo = getItem(position);
        if (needTitle(position)) {
//            if (cur_date_bg_res == R.drawable.album_list_title_date1_bg) {
//                cur_date_bg_res = R.drawable.album_list_title_date2_bg;
//            } else {
//                cur_date_bg_res = R.drawable.album_list_title_date1_bg;
//            }
//            holder.title_rl.setBackgroundResource(cur_date_bg_res);
            String str_date = aCategoryPhotoListItemInfo.getDate();

            int month = DateUtil.getMonth(DateUtil.getDate(str_date));
            int day = DateUtil.getDay(DateUtil.getDate(str_date));
            String week = DateUtil.getWeekSting(DateUtil.getDate(str_date));
            holder.week_tv.setText("(" + week + ")");
            holder.day_tv.setText(str_date);
            holder.title_rl.setVisibility(View.VISIBLE);
        } else {
            holder.title_rl.setVisibility(View.GONE);
        }
        if (aCategoryPhotoListItemInfo.getPhotoInfo1() != null) {
            holder.photo_list_item1_view.setVisibility(View.VISIBLE);
            holder.photo_list_item1_view.setPhotoInfo(aCategoryPhotoListItemInfo.getPhotoInfo1());
            holder.photo_list_item1_view.setPhotoListItemClickListener(mPhotoListItemClickListener);
            holder.photo_list_item1_view.setViewEditStatus(bMultiSelectState);
        } else {
            holder.photo_list_item1_view.setVisibility(View.INVISIBLE);
        }
        if (aCategoryPhotoListItemInfo.getPhotoInfo2() != null) {
            holder.photo_list_item2_view.setVisibility(View.VISIBLE);
            holder.photo_list_item2_view.setPhotoInfo(aCategoryPhotoListItemInfo.getPhotoInfo2());
            holder.photo_list_item2_view.setPhotoListItemClickListener(mPhotoListItemClickListener);
            holder.photo_list_item2_view.setViewEditStatus(bMultiSelectState);
        } else {
            holder.photo_list_item2_view.setVisibility(View.INVISIBLE);
        }
        if (aCategoryPhotoListItemInfo.getPhotoInfo3() != null) {
            holder.photo_list_item3_view.setVisibility(View.VISIBLE);
            holder.photo_list_item3_view.setPhotoInfo(aCategoryPhotoListItemInfo.getPhotoInfo3());
            holder.photo_list_item3_view.setPhotoListItemClickListener(mPhotoListItemClickListener);
            holder.photo_list_item3_view.setViewEditStatus(bMultiSelectState);
        } else {
            holder.photo_list_item3_view.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    private boolean needTitle(int position) {
        // 第一个肯定是分类
        if (position == 0) {
            return true;
        }
        // 边界处理
        if (position < 0) {
            return false;
        }
        // 当前  // 上一个
        CategoryPhotoListItemInfo currentEntity = (CategoryPhotoListItemInfo) getItem(position);
        CategoryPhotoListItemInfo previousEntity = (CategoryPhotoListItemInfo) getItem(position - 1);
        if (null == currentEntity || null == previousEntity) {
            return false;
        }
        String currentTitle = currentEntity.getDate();
        String previousTitle = previousEntity.getDate();
        if (null == previousTitle || null == currentTitle) {
            return false;
        }
        // 当前item分类名和上一个item分类名不同，则表示两item属于不同分类
        if (currentTitle.equals(previousTitle)) {
            return false;
        }
        return true;
    }

    class PhotoItemHolder {
        public RelativeLayout title_rl;
        public TextView date_tv, week_tv, day_tv;
        public PhotoListSelectItemView photo_list_item1_view;
        public PhotoListSelectItemView photo_list_item2_view;
        public PhotoListSelectItemView photo_list_item3_view;


    }
}