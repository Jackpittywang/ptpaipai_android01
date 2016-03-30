package com.putao.camera.load;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.putao.camera.R;
import com.putao.camera.base.BaseActivity;

/**
 * Created by Administrator on 2016/3/30.
 */
public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener{
    private Button back_btn,right_btn;
    private TextView title_tv;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_forget_password;
    }

    @Override
    public void doInitSubViews(View view) {
        title_tv =queryViewById(R.id.title_tv);
        title_tv.setText("忘记密码");
        right_btn = queryViewById(R.id.right_btn);
        right_btn.setVisibility(View.INVISIBLE);

        back_btn =queryViewById(R.id.back_btn);



        addOnClickListener(back_btn);

    }

    @Override
    public void doInitData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;

        }
    }
}
