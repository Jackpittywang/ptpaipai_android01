package com.putao.camera.umengfb;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.putao.camera.R;
import com.putao.camera.base.BaseActivity;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.SyncListener;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.Reply;

import java.util.List;

public class UmengFeedbackActivity extends BaseActivity implements OnClickListener {
    private Button backBtn;
    private EditText feedbackET;
    private Button submitBtn;
    private FeedbackAgent feedbackAgent;
    private Conversation conversation;
    private TextView tv_count_limit;
    private TextView title_tv;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_umeng_fb;
    }

    @Override
    public void doInitSubViews(View view) {
        backBtn = (Button) findViewById(R.id.back_btn);
        title_tv = (TextView) this.findViewById(R.id.title_tv);
        title_tv.setText("意见反馈");
        feedbackET = (EditText) findViewById(R.id.umeng_fb_reply_content);
        submitBtn = (Button) findViewById(R.id.right_btn);
        submitBtn.setText("发送");
        tv_count_limit = (TextView) findViewById(R.id.tv_count_limit);
        backBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        feedbackET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() == 0) {
                    submitBtn.setEnabled(false);
                } else {
                    submitBtn.setEnabled(true);
                }
                tv_count_limit.setText(s.toString().length() + "/100");
            }
        });
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(feedbackET, 0);
    }

    @Override
    public void doInitData() {

        feedbackAgent = new FeedbackAgent(mContext);
        feedbackAgent.sync();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.right_btn:
                send();
                break;
            case R.id.back_btn:
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(feedbackET.getWindowToken(), 0);
                finish();
                break;
            default:
                break;
        }
    }

    private void send() {
        String content = feedbackET.getText().toString().trim();
        if (content.equals("")) {
            showToast("亲，请输入您宝贵滴意见！");
            return;
        } else {
            conversation = feedbackAgent.getDefaultConversation();
            conversation.addUserReply(content);
            conversation.sync(new SyncListener() {
                @Override
                public void onSendUserReply(List<Reply> arg0) {
                    feedbackET.setText("");
                    showToast("发送成功，正在处理您的请求，谢谢！");
                }

                @Override
                public void onReceiveDevReply(List<Reply> arg0) {
                }
            });
        }
    }
}
