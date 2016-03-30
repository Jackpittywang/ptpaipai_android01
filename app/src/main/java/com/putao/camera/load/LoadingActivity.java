package com.putao.camera.load;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.putao.camera.R;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.util.ActivityHelper;

/**
 * Created by Administrator on 2016/3/30.
 */
public class LoadingActivity extends BaseActivity implements View.OnClickListener{
    private Button back_btn,right_btn;
    private TextView title_tv,register_tv,forget_password_tv;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_load;
    }

    @Override
    public void doInitSubViews(View view) {
        title_tv =queryViewById(R.id.title_tv);
        title_tv.setText("登录葡萄账户");
        right_btn = queryViewById(R.id.right_btn);
        right_btn.setVisibility(View.INVISIBLE);

        back_btn =queryViewById(R.id.back_btn);
        back_btn.setText("取消");

        register_tv=queryViewById(R.id.register_tv);
        forget_password_tv= queryViewById(R.id.forget_password_tv);

        addOnClickListener(back_btn,register_tv,forget_password_tv);

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
            case R.id.register_tv:
                ActivityHelper.startActivity(mActivity, RegisterActivity.class);
                break;
            case R.id.forget_password_tv:
                ActivityHelper.startActivity(mActivity, ForgetPasswordActivity.class);
                break;
        }
    }
}
