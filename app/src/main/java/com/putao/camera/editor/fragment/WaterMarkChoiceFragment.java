package com.putao.camera.editor.fragment;

import java.util.HashMap;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.putao.camera.R;
import com.putao.camera.base.BaseFragment;
import com.putao.camera.bean.WaterMarkCategoryInfo;
import com.putao.camera.bean.WaterMarkIconInfo;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.constants.UmengAnalysisConstants;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;

public class WaterMarkChoiceFragment extends BaseFragment implements OnItemClickListener {
    private GridView water_mark_collection_icon_gv;
    private HorizontalScrollView scroll_watermark;
    //    private ArrayList<WaterMarkChoiceItem> mWaterMarkChoiceItemArray;
    private WaterMarkChoiceAdapter mWaterMarkChoiceAdapter;
    private int mdType = 0;
    private WaterMarkCategoryInfo mWaterMarkCategoryInfo;

    @Override
    public int doGetContentViewId() {
        return R.layout.fragment_water_mark_choice;
    }

    public static WaterMarkChoiceFragment newInstance(Bundle bundle) {
        WaterMarkChoiceFragment f = new WaterMarkChoiceFragment();
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void doInitSubViews(View view) {
        water_mark_collection_icon_gv = (GridView) view.findViewById(R.id.water_mark_collection_icon_gv);
        scroll_watermark = (HorizontalScrollView) view.findViewById(R.id.scroll_watermark);
    }

    @Override
    public void doInitDataes() {
        Bundle bundle = this.getArguments();
        //        mWaterMarkChoiceItemArray = new ArrayList<WaterMarkChoiceItem>();
        // 加载水印效果
        if (bundle != null) {
            mdType = bundle.getInt("dtype", 0);
            mWaterMarkCategoryInfo = (WaterMarkCategoryInfo) bundle.getSerializable("WaterMarkCategoryInfo");
            //            String prefix_name = bundle.getString(WaterMarkChoiceDialogFragment.WATER_MARK_PREFIX_NAME);
            //            int start_index = bundle.getInt(WaterMarkChoiceDialogFragment.WATER_MARK_START_INDEX);
            //            int end_index = bundle.getInt(WaterMarkChoiceDialogFragment.WATER_MARK_END_INDEX);
            //            mWaterMarkChoiceItemArray.clear();
            //            if (prefix_name != null && prefix_name.equals("water_mark_list_thumbnail_tj"))
            //            {
            //                // 指定推荐
            //                mWaterMarkChoiceItemArray.add(createWaterMarkchoiceItem("water_mark_list_thumbnail_wz", "13"));
            //                mWaterMarkChoiceItemArray.add(createWaterMarkchoiceItem("water_mark_list_thumbnail_wz", "17"));
            //                mWaterMarkChoiceItemArray.add(createWaterMarkchoiceItem("water_mark_list_thumbnail_jr", "03"));
            //                mWaterMarkChoiceItemArray.add(createWaterMarkchoiceItem("water_mark_list_thumbnail_jr", "01"));
            //                mWaterMarkChoiceItemArray.add(createWaterMarkchoiceItem("water_mark_list_thumbnail_jr", "07"));
            //                mWaterMarkChoiceItemArray.add(createWaterMarkchoiceItem("water_mark_list_thumbnail_jr", "08"));
            //                mWaterMarkChoiceItemArray.add(createWaterMarkchoiceItem("water_mark_list_thumbnail_bq", "02"));
            //                mWaterMarkChoiceItemArray.add(createWaterMarkchoiceItem("water_mark_list_thumbnail_bq", "04"));
            //                mWaterMarkChoiceItemArray.add(createWaterMarkchoiceItem("water_mark_list_thumbnail_bq", "06"));
            //                mWaterMarkChoiceItemArray.add(createWaterMarkchoiceItem("water_mark_list_thumbnail_bq", "07"));
            //                mWaterMarkChoiceItemArray.add(createWaterMarkchoiceItem("water_mark_list_thumbnail_bq", "08"));
            //                mWaterMarkChoiceItemArray.add(createWaterMarkchoiceItem("water_mark_list_thumbnail_bq", "13"));
            //                mWaterMarkChoiceItemArray.add(createWaterMarkchoiceItem("water_mark_list_thumbnail_wz", "11"));
            //                mWaterMarkChoiceItemArray.add(createWaterMarkchoiceItem("water_mark_list_thumbnail_jr", "04"));
            //                mWaterMarkChoiceItemArray.add(createWaterMarkchoiceItem("water_mark_list_thumbnail_wz", "18"));
            //                mWaterMarkChoiceItemArray.add(createWaterMarkchoiceItem("water_mark_list_thumbnail_wz", "14"));
            //                mWaterMarkChoiceItemArray.add(createWaterMarkchoiceItem("water_mark_list_thumbnail_wz", "08"));
            //                mWaterMarkChoiceItemArray.add(createWaterMarkchoiceItem("water_mark_list_thumbnail_cy", "01"));
            //            }
            //            else
            //            {
            //                // 根据前缀名从res中获取
            //                for (int i = start_index; i <= end_index; i++)
            //                {
            //                    mWaterMarkChoiceItemArray.add(createWaterMarkchoiceItem(prefix_name, (i < 10 ? "0" + i : "" + i)));
            //                }
            //            }
            //            /*
            //             * Umeng事件统计
            //             */
            //            if (prefix_name != null && prefix_name.equals("water_mark_list_thumbnail_tj"))
            //            {
            //                UmengAnalysisHelper.getInstance().onEvent(mActivity, UmengAnalysisConstants.UMENG_COUNT_EVENT_WATER_MARK_SUGGEST);
            //            }
            //            else if (prefix_name != null && prefix_name.equals("water_mark_list_thumbnail_jr"))
            //            {
            //                UmengAnalysisHelper.getInstance().onEvent(mActivity, UmengAnalysisConstants.UMENG_COUNT_EVENT_WATER_MARK_FESTVIAL);
            //            }
            //            else if (prefix_name != null && prefix_name.equals("water_mark_list_thumbnail_bq"))
            //            {
            //                UmengAnalysisHelper.getInstance().onEvent(mActivity, UmengAnalysisConstants.UMENG_COUNT_EVENT_WATER_MARK_EXPRESSION);
            //            }
            //            else if (prefix_name != null && prefix_name.equals("water_mark_list_thumbnail_sh"))
            //            {
            //                UmengAnalysisHelper.getInstance().onEvent(mActivity, UmengAnalysisConstants.UMENG_COUNT_EVENT_WATER_MARK_LIFE);
            //            }
            //            else if (prefix_name != null && prefix_name.equals("water_mark_list_thumbnail_wz"))
            //            {
            //                UmengAnalysisHelper.getInstance().onEvent(mActivity, UmengAnalysisConstants.UMENG_COUNT_EVENT_WATER_MARK_CHARACTER);
            //            }
            //            else if (prefix_name != null && prefix_name.equals("water_mark_list_thumbnail_cy"))
            //            {
            //                UmengAnalysisHelper.getInstance().onEvent(mActivity, UmengAnalysisConstants.UMENG_COUNT_EVENT_WATER_MARK_COMMON);
            //            }
        }
        /*
        * Umeng事件统计
        */
//        if (mWaterMarkCategoryInfo.category.equals("推荐")) {
//            UmengAnalysisHelper.onEvent(mActivity, UmengAnalysisConstants.UMENG_COUNT_EVENT_WATER_MARK_SUGGEST);
//        } else if (mWaterMarkCategoryInfo.category.equals("节日")) {
//            UmengAnalysisHelper.onEvent(getActivity(), UmengAnalysisConstants.UMENG_COUNT_EVENT_WATER_MARK_FESTVIAL);
//        } else if (mWaterMarkCategoryInfo.category.equals("表情")) {
//            UmengAnalysisHelper.onEvent(mActivity, UmengAnalysisConstants.UMENG_COUNT_EVENT_WATER_MARK_EXPRESSION);
//        } else if (mWaterMarkCategoryInfo.category.equals("常用")) {
//            UmengAnalysisHelper.onEvent(mActivity, UmengAnalysisConstants.UMENG_COUNT_EVENT_WATER_MARK_LIFE);
//        } else if (mWaterMarkCategoryInfo.category.equals("文字")) {
//            UmengAnalysisHelper.onEvent(mActivity, UmengAnalysisConstants.UMENG_COUNT_EVENT_WATER_MARK_CHARACTER);
//        }

        mWaterMarkChoiceAdapter = new WaterMarkChoiceAdapter(mActivity, mWaterMarkCategoryInfo);
        water_mark_collection_icon_gv.setAdapter(mWaterMarkChoiceAdapter);
        setGridView();
        water_mark_collection_icon_gv.setOnItemClickListener(this);
    }

    void setGridView() {
        int size = mWaterMarkCategoryInfo.elements.size();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int itemWidth = dm.widthPixels / 3 - 10;
        int gridviewWidth = 10;
        int columns = (size % 2 == 0) ? size / 2 : size / 2 + 1;
        gridviewWidth += columns * itemWidth;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        water_mark_collection_icon_gv.setLayoutParams(params);
        water_mark_collection_icon_gv.setColumnWidth(itemWidth);
        water_mark_collection_icon_gv.setHorizontalSpacing(0);
        water_mark_collection_icon_gv.setStretchMode(GridView.NO_STRETCH);
        water_mark_collection_icon_gv.setNumColumns(columns);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, String> watermarkMap = new HashMap<String, String>();
        watermarkMap.put(UmengAnalysisConstants.UMENG_COUNT_EVENT_WATER_MARK_CHOISE, mWaterMarkChoiceAdapter.getItem(position).sample_image);
//        UmengAnalysisHelper.onEvent(mActivity, UmengAnalysisConstants.UMENG_COUNT_EVENT_WATER_MARK_CHOISE, watermarkMap);
        //        String sample_image = mWaterMarkChoiceAdapter.getItem(position).sample_image;
        //                watermarkMap.put(getResources().getResourceEntryName(iconRes), getResources().getResourceEntryName(iconRes));
        //        UmengAnalysisHelper.getInstance().onEvent(mActivity, UmengAnalysisConstants.UMENG_COUNT_EVENT_WATER_MARK_CHOISE, watermarkMap);
        Bundle bundle = new Bundle();
        WaterMarkIconInfo info = mWaterMarkChoiceAdapter.getItem(position);
        bundle.putSerializable("iconRes", info);
        EventBus.getEventBus().post(
                new BasePostEvent((mdType == 0) ? PuTaoConstants.WATER_MARK_ICON_CHOICE_REFRESH : PuTaoConstants.WATER_MARK_TAKE_PHOTO, bundle));
        //WaterMarkChoiceDialogFragment dialogFragment = (WaterMarkChoiceDialogFragment) this.getParentFragment();
        //dialogFragment.dismiss();
    }
}