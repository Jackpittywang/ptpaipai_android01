
package com.putao.camera.editor;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.putao.camera.R;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.editor.popup.DatePickPop;
import com.putao.camera.editor.popup.DatePickPop.OnDateSelectListener;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.util.CommonUtils;
import com.putao.camera.util.DateUtil;
import com.putao.camera.util.StringHelper;

/**
 * Created by jidongdong on 15/1/16.
 */
public class FestivalSelectActivity extends BaseActivity implements View.OnClickListener {
    private Button cancleBtn, okBtn;
    private RelativeLayout festivalLayout, dateLayout;
    private TextView dateTV;
    private EditText festivalET;
    private View maskView;
    private DatePickPop datePickPop;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_festival_select;
    }

    @Override
    public void doInitSubViews(View view) {
        cancleBtn = (Button) findViewById(R.id.btn_cancle);
        okBtn = (Button) findViewById(R.id.btn_ok);
        dateLayout = (RelativeLayout) findViewById(R.id.layout_date);
        festivalET = (EditText) findViewById(R.id.edittext_festival);
        dateTV = (TextView) findViewById(R.id.textview_date);
        maskView = findViewById(R.id.mask1);
        addOnClickListener(cancleBtn, okBtn, dateLayout);
    }

    @Override
    public void doInitData() {
        dateTV.setText(getNowDate());
        /*
         * 时间选择器
         */
        datePickPop = new DatePickPop(this);
        datePickPop.setOnDismissListener(popDismissListener);
        datePickPop.setOnDateSelectListener(onDateSelectListener);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String name = bundle.getString("name");
            String date = bundle.getString("date");
            if (!StringHelper.isEmpty(name)) {
                festivalET.setText(name);
            }
            //TODO:目前传递的只有天数，没有日期格式的字符串
            if (!StringHelper.isEmpty(date)) {
                dateTV.setText(DateUtil.getStringDateShortAfterDays(Integer.parseInt(date)));
            }
        }
    }

    private String getNowDate() {
        return DateUtil.getStringDateShort();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancle:
                finish();
                break;
            case R.id.btn_ok:
                Bundle bundle = new Bundle();
                bundle.putString("date", dateTV.getText().toString());
                bundle.putString("name", festivalET.getText().toString());
                EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.WATER_MARK_DATE_SELECTED, bundle));
                finish();
                break;
            case R.id.layout_date:
                maskView.setVisibility(View.VISIBLE);
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    datePickPop.setTime(df.parse(String.valueOf(dateTV.getText())).getTime());
                } catch (ParseException e) {
                    datePickPop.setTime(System.currentTimeMillis());
                }
                datePickPop.showAtLocation(findViewById(R.id.root1), Gravity.CENTER, 0, 0);
                /*datePickPop.showAtLocation(findViewById(R.id.root1), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);*/
                break;
        }
    }

    private OnDismissListener popDismissListener = new OnDismissListener() {
        @Override
        public void onDismiss() {
            maskView.setVisibility(View.GONE);
        }
    };
    private OnDateSelectListener onDateSelectListener = new OnDateSelectListener() {
        @Override
        public void onSet(long timeInMillis) {
            dateTV.setText(CommonUtils.parseTime(timeInMillis));
            String festival = DateUtil.getFestival(timeInMillis).trim();
            if (festival.length() != 0) {
                festivalET.setText(festival);
            }
        }

        @Override
        public void onCancle() {
        }
    };
}
