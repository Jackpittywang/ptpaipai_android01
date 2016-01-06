package com.putao.camera.editor;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.putao.camera.R;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.editor.adapter.CityAdapter;
import com.putao.camera.editor.adapter.CityCategoryAdapter;
import com.putao.camera.editor.adapter.CitySearchAdapter;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.gps.CityMap;
import com.putao.camera.util.ActivityHelper;

/**
 * Created by jidongdong on 15/1/16.
 */
public class CitySelectActivity extends BaseActivity implements OnClickListener {
    private LinearLayout cityListLayout;
    private LinearLayout citySearchLayout;
    private Button citySearchCancleBtn;
    private EditText citySearchET;
    private ImageView clearIV;
    private TextView citySearchTipTV;
    private ListView citySearchListView;
    //
    private ListView city_listview;
    private EditText et_input;
    private Button btn_search, btn_cancle;
    //
    private CityAdapter mAdapter;
    private ArrayList<String> hotCityList = new ArrayList<String>();
    private ArrayList<String> allCityList = new ArrayList<String>();
    private CitySearchAdapter searchCityAdapter;
    private ArrayList<String> searchCityList = new ArrayList<String>();

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_city_select;
    }

    @Override
    public void doInitData() {
        /*
         * 城市列表
         */
        hotCityList.add("北京");
        hotCityList.add("上海");
        hotCityList.add("广州");
        hotCityList.add("深圳");
        allCityList = CityMap.getInstance().getAllCity();
        /*mAdapter = new CityAdapter(mContext, allCityList);
        city_listview.setAdapter(mAdapter);*/
        cityCategoryAdapter.addCategory("主要城市", new CityAdapter(mContext, hotCityList));
        cityCategoryAdapter.addCategory("所有城市", new CityAdapter(mContext, allCityList));
        city_listview.setAdapter(cityCategoryAdapter);
        /*
         * 搜索城市
         */
        searchCityAdapter = new CitySearchAdapter(mContext, searchCityList);
        citySearchListView.setAdapter(searchCityAdapter);
    }

    @Override
    public void doInitSubViews(View view) {
        btn_cancle = (Button) findViewById(R.id.btn_cancle);
        cityListLayout = (LinearLayout) findViewById(R.id.layout_city_list);
        citySearchLayout = (LinearLayout) findViewById(R.id.layout_city_search);
        citySearchCancleBtn = (Button) findViewById(R.id.btn_city_search_cancle);
        citySearchET = (EditText) findViewById(R.id.edittext_city_search);
        clearIV = (ImageView) findViewById(R.id.imageview_clear);
        citySearchTipTV = (TextView) findViewById(R.id.textview_city_search);
        citySearchListView = (ListView) findViewById(R.id.listview_city_search);
        et_input = (EditText) findViewById(R.id.et_input);
        city_listview = (ListView) findViewById(R.id.list_city);
        city_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemType = cityCategoryAdapter.getItemViewType(position);
                if (itemType <= 0) {
                } else {
                    Bundle bundle = new Bundle();
                    if (itemType == 1) {
                        bundle.putString("city", hotCityList.get(position - itemType));
                    } else if (itemType == 2) {
                        bundle.putString("city", allCityList.get(position - hotCityList.size() - itemType));
                    }
                    EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.WATER_MARK_CITY_SELECTED, bundle));
                    finish();
                }
            }
        });
        et_input.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (cityListLayout.getVisibility() == View.VISIBLE) {
                    cityListLayout.setVisibility(View.GONE);
                }
                if (citySearchLayout.getVisibility() == View.GONE) {
                    citySearchLayout.setVisibility(View.VISIBLE);
                }
                et_input.clearFocus();
                citySearchET.requestFocus();
                ActivityHelper.showInputKeyboard(mContext, et_input);
            }
        });
        citySearchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(final Editable s) {
                // TODO Auto-generated method stub
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (searchCityList != null) {
                            searchCityList.clear();
                        }
                        if (s.toString().trim().length() != 0) {
                            searchCityList.addAll(CityMap.getInstance().getCityListByKey(citySearchET.getText().toString()));
                        }
                        citySearchHandler.sendEmptyMessage(0);
                    }
                }).start();
                /*
                 * 删除显示
                 */
                if (s.toString().length() == 0) {
                    clearIV.setVisibility(View.INVISIBLE);
                } else {
                    clearIV.setVisibility(View.VISIBLE);
                }
            }
        });
        citySearchCancleBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (cityListLayout.getVisibility() == View.GONE) {
                    cityListLayout.setVisibility(View.VISIBLE);
                }
                if (citySearchLayout.getVisibility() == View.VISIBLE) {
                    citySearchLayout.setVisibility(View.GONE);
                }
                citySearchET.setText("");
                citySearchET.clearFocus();
                et_input.requestFocus();
                ActivityHelper.hideInputKeyboard(mContext, citySearchET);
            }
        });
        citySearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("city", searchCityList.get(position));
                EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.WATER_MARK_CITY_SELECTED, bundle));
                finish();
            }
        });
        clearIV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                citySearchET.setText("");
            }
        });

        addOnClickListener(btn_cancle);
    }

    private Handler citySearchHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            searchCityAdapter.notifyDataSetChanged();
            /*
             * 搜索提示
             */
            if (citySearchET.getText().toString().trim().length() != 0) {
                if (searchCityList.size() == 0) {
                    citySearchTipTV.setVisibility(View.VISIBLE);
                } else {
                    citySearchTipTV.setVisibility(View.INVISIBLE);
                }
            } else {
                citySearchTipTV.setVisibility(View.INVISIBLE);
            }
        }

        ;
    };
    private CityCategoryAdapter cityCategoryAdapter = new CityCategoryAdapter() {
        @Override
        protected View getTitleView(String title, int index, View convertView, ViewGroup parent) {
            TextView titleTV;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.listitem_city_select_title, null);
                titleTV = (TextView) convertView.findViewById(R.id.textview_city_category_title);
                convertView.setTag(titleTV);
            } else {
                titleTV = (TextView) convertView.getTag();
            }
            titleTV.setText(title);
            return convertView;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancle:
                finish();
                break;
        }
    }
}
