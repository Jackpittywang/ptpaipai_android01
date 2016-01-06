
package com.putao.camera.album.adapter;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.putao.camera.R;
import com.putao.camera.album.view.PhotoListItemClickListener;
import com.putao.camera.album.view.PhotoListItemView;
import com.putao.camera.bean.PhotoInfo;
import com.putao.camera.util.CommonUtils;

public class PhotoAdapter extends BaseAdapter {
    Context context;
    Map<String, ArrayList<PhotoInfo>> mHashMap;
    ArrayList<ArrayList<PhotoInfo>> mArrayList;
    private PhotoListItemClickListener mPhotoListItemClickListener;
    boolean bMultiSelectState = false;

    public PhotoAdapter(Context mContext, PhotoListItemClickListener listener, Map<String, ArrayList<PhotoInfo>> map) {
        this.context = mContext;
        mPhotoListItemClickListener = listener;
        setData(map);
    }

    public void setData(Map<String, ArrayList<PhotoInfo>> map) {
        this.mHashMap = map;
        this.mArrayList = new ArrayList<ArrayList<PhotoInfo>>(map.values());
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
        for (int i = 0; i < mArrayList.size(); i++) {
            ArrayList<PhotoInfo> photos = mArrayList.get(i);
            for (int j = 0; j < photos.size(); j++) {
                if (photos.get(j)._ID.equals(info._ID)) {
                    mArrayList.get(i).get(j).Checked = checked;
                    return;
                }
            }
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mArrayList.size();
    }

    @Override
    public ArrayList<PhotoInfo> getItem(int position) {
        // TODO Auto-generated method stub
        return mArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        PhotoItemHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_photo_date_category_item, null);
            holder = new PhotoItemHolder();
            holder.date_tv = (TextView) convertView.findViewById(R.id.date_tv);
            holder.photo_list_item_view = (PhotoListItemView) convertView.findViewById(R.id.photo_list_item_view);
            convertView.setTag(holder);
        } else {
            holder = (PhotoItemHolder) convertView.getTag();
        }
        String date = CommonUtils.parseTime(getItem(position).get(0).getDate_Added(), "yyyy-MM-dd");
        holder.date_tv.setText(date);
        holder.photo_list_item_view.setListItemView(getItem(position), mPhotoListItemClickListener, bMultiSelectState);
        return convertView;
    }

    class PhotoItemHolder {
        public TextView date_tv;
        public PhotoListItemView photo_list_item_view;
    }
}
