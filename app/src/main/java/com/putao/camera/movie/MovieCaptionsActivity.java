package com.putao.camera.movie;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.putao.camera.R;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.http.CacheRequest;
import com.putao.camera.movie.adapter.MovieCaptionListAdapter;
import com.putao.camera.movie.model.MovieCaption;
import com.putao.camera.movie.model.MovieCaptionConfig;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.DisplayHelper;
import com.putao.camera.util.Loger;
import com.putao.camera.util.NetType;
import com.putao.camera.util.StringHelper;

/**
 * Created by jidongdong on 15/3/16.
 */
public class MovieCaptionsActivity extends BaseActivity implements View.OnClickListener {
    private Button back_btn, right_btn;
    private LinearLayout btn_translate, subtitle_button_more;
    private EditText et_caption_zh, et_caption_en;
    private MovieCaptionListAdapter mMovieCaptionListAdapter;
    private ListView list_caption_default;
    private LinearLayout layout_caption_list;
    private RelativeLayout layout_content, title_bar_rl;
    private TextView tv_caption_zh_count, tv_caption_en_count;
    private boolean isfold = false;
    private TextView title_tv;
    private ImageView point_before_text;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_movie_captions_editor;
    }

    @Override
    public void doInitSubViews(View view) {

        back_btn = (Button) findViewById(R.id.back_btn);
        title_tv = (TextView) this.findViewById(R.id.title_tv);
        title_tv.setText("字幕编辑");
        right_btn = (Button) findViewById(R.id.right_btn);
        right_btn.setText("完成");

        btn_translate = (LinearLayout) findViewById(R.id.btn_translate);
        et_caption_en = (EditText) findViewById(R.id.et_caption_en);
        et_caption_zh = (EditText) findViewById(R.id.et_caption_zh);
        subtitle_button_more = (LinearLayout) findViewById(R.id.subtitle_button_more);
        list_caption_default = (ListView) findViewById(R.id.list_caption_default);
        layout_caption_list = (LinearLayout) findViewById(R.id.layout_caption_list);
        layout_content = (RelativeLayout) findViewById(R.id.layout_content);
        title_bar_rl = (RelativeLayout) findViewById(R.id.title_bar_rl);
        tv_caption_zh_count = (TextView) findViewById(R.id.tv_caption_zh_count);
        tv_caption_en_count = (TextView) findViewById(R.id.tv_caption_en_count);
        point_before_text = (ImageView) findViewById(R.id.point_before_text);
        point_before_text.setImageBitmap(BitmapHelper.getCircleBitmap(Color.WHITE, 20));

        addOnClickListener(back_btn, right_btn, btn_translate, subtitle_button_more);

        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(et_caption_en, 0);

        EventBus.getEventBus().register(this);

        et_caption_zh.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tv_caption_zh_count.setText(getZhCountString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_caption_en.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tv_caption_en_count.setText(getEnCountString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    protected String getZhCountString() {
        String cSting = null;
        int length = et_caption_zh.getText().toString().length();
        int total = getResources().getInteger(R.integer.move_zh_length);
        cSting = length + "/" + total;
        return cSting;
    }

    protected String getEnCountString() {
        String cSting = null;
        int length = et_caption_en.getText().toString().length();
        int total = getResources().getInteger(R.integer.move_en_length);
        cSting = length + "/" + total;
        return cSting;
    }

    @Override
    public void doInitData() {
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            String text_zh = bundle.getString("text_zh");
            String text_en = bundle.getString("text_en");
            if (StringHelper.isEmpty(text_zh) || text_zh.equals(getResources().getString(R.string.movie_default_zh))) {
                et_caption_zh.setHint(text_zh);
            } else {
                et_caption_zh.setText(text_zh);
            }
            if (StringHelper.isEmpty(text_en) || text_en.equals(getResources().getString(R.string.movie_default_en))) {
                et_caption_en.setHint(text_en);
            } else {
                et_caption_en.setText(text_en);
            }
        }

        tv_caption_zh_count.setText(getZhCountString());
        tv_caption_en_count.setText(getEnCountString());

        initCaptionList();
    }

    void initCaptionList() {
        try {
            final MovieCaptionConfig movieCaptionConfig = MovieCaption.newInstance().getMovieCaptionConfig(4);
            if (movieCaptionConfig != null) {
                mMovieCaptionListAdapter = new MovieCaptionListAdapter(mContext, movieCaptionConfig.movieLines);
                list_caption_default.setAdapter(mMovieCaptionListAdapter);
                mMovieCaptionListAdapter.notifyDataSetChanged();
                list_caption_default.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MovieCaptionConfig.MovieCaptionItem item = movieCaptionConfig.movieLines.get(position);
                        et_caption_en.setText(item.en_line);
                        et_caption_zh.setText(item.cn_line);
                    }
                });
            } else {
                layout_caption_list.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onEvent(BasePostEvent event) {
        switch (event.eventCode) {
            case PuTaoConstants.DIALOG_CAPTIONS_FINISH_EVENT:
                Bundle bundle = event.bundle;
                if (bundle != null) {
                    String cn_line = (String) bundle.get("cn_line");
                    String en_line = (String) bundle.get("en_line");
                    et_caption_zh.setText(cn_line);
                    et_caption_en.setText(en_line);
                }
                break;
            default:
                break;
        }

    }

    void translateToEnglish() {
        final String zh_text = et_caption_zh.getText().toString();
        if (StringHelper.isEmpty(zh_text)) {
            showToast("请输入中文字幕");
            return;
        }
        if (NetType.getNetworkType(this) == -1) {
            showToast("无法翻译,网络");
            return;
        }
        final String url = PuTaoConstants.BAI_DU_TRANSLATE_URL + zh_text;
        CacheRequest.ICacheRequestCallBack translateRequest = new CacheRequest.ICacheRequestCallBack() {
            @Override
            public void onSuccess(int whatCode, JSONObject json) {
                super.onSuccess(whatCode, json);
                try {
                    JSONArray translate_array = json.getJSONArray("trans_result");
                    if (translate_array.length() > 0) {
                        String text_en = translate_array.getJSONObject(0).getString("dst");
                        et_caption_en.setText(text_en);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(int whatCode, int statusCode, String responseString) {
                super.onFail(whatCode, statusCode, responseString);
            }

        };
        HashMap<String, String> map = new HashMap<String, String>();
        CacheRequest mCacheRequest = new CacheRequest(url, map, translateRequest) {
            @Override
            public String getUrlString() {
                return url;
            }
        };
        mCacheRequest.startGetRequest();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout_caption_list.getLayoutParams();
            Loger.d("content_height:" + layout_content.getHeight());
            params.height = DisplayHelper.getScreenHeight() - getNavigationBarHeight() - DisplayHelper.getStatusBarHeight(mContext) - title_bar_rl.getHeight()
                    - layout_content.getHeight();
            layout_caption_list.setLayoutParams(params);
        }
    }

    int getNavigationBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    @Override
    public void onBackPressed() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_caption_en.getWindowToken(), 0);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.right_btn:
                Bundle bundle = new Bundle();
                bundle.putString("text_zh", et_caption_zh.getText().toString());
                bundle.putString("text_en", et_caption_en.getText().toString());
                EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.MOVIE_CAPTION_TRANSLATE, bundle));
                finish();
                break;
            case R.id.btn_translate:
                translateToEnglish();
                break;
            case R.id.subtitle_button_more:
                Dialog dialog = new CaptionListDialog(this, R.style.dialog_movie_caption_list);
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialog.show();

                // 设置Pad为0,到边
                Window win = dialog.getWindow();
                win.getDecorView().setPadding(0, 0, 0, 0);
                WindowManager.LayoutParams lp = win.getAttributes();
                lp.width = WindowManager.LayoutParams.FILL_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                win.setAttributes(lp);

                break;
        }
    }
}
