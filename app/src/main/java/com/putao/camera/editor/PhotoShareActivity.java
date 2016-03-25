package com.putao.camera.editor;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.putao.camera.R;
import com.putao.camera.album.AlbumPhotoSelectActivity;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.camera.ActivityCamera;
import com.putao.camera.collage.CollageSampleSelectActivity;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.menu.MenuActivity;
import com.putao.camera.movie.MovieCameraActivity;
import com.putao.camera.thirdshare.ShareTools;
import com.putao.camera.thirdshare.dialog.ThirdShareDialog;
import com.putao.camera.util.ActivityHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jidongdong on 15/3/3.
 */
public class PhotoShareActivity extends BaseActivity implements View.OnClickListener, ThirdShareDialog.ThirdShareDialogProcessListener {

    private Button btn_back, btn_home, share_btn_sina, share_btn_wechat, share_btn_friend, share_btn_qq, share_btn_qzone;
    private String filepath;
    private TextView tv_filepath;
    private LinearLayout btn_go_chartlet, btn_go_camera, btn_go_collage, btn_go_movie;
    private TextView title_tv;
    public static ShareTools mShareTools;
    private String from;


    @Override

    public int doGetContentViewId() {
        return R.layout.activity_photo_share;
    }

    @Override
    public void doInitSubViews(View view) {
        btn_back = (Button) findViewById(R.id.back_btn);
        title_tv = (TextView) findViewById(R.id.title_tv);
        title_tv.setText("保存");
        btn_home = (Button) findViewById(R.id.right_btn);
        btn_home.setBackgroundResource(R.drawable.share_button_home);
        share_btn_sina = (Button) findViewById(R.id.share_btn_sina);
        share_btn_wechat = (Button) findViewById(R.id.share_btn_wechat);
        share_btn_friend = (Button) findViewById(R.id.share_btn_friend);
        share_btn_qq = (Button) findViewById(R.id.share_btn_qq);
        share_btn_qzone = (Button) findViewById(R.id.share_btn_qzone);
        tv_filepath = (TextView) findViewById(R.id.tv_filepath);
        btn_go_chartlet = (LinearLayout) findViewById(R.id.btn_go_chartlet);
        btn_go_camera = (LinearLayout) findViewById(R.id.btn_go_camera);
        btn_go_collage = (LinearLayout) findViewById(R.id.btn_go_collage);
        btn_go_movie = (LinearLayout) findViewById(R.id.btn_go_movie);
        addOnClickListener(btn_back, btn_home, share_btn_friend, share_btn_sina, share_btn_qq, share_btn_qzone, share_btn_wechat, btn_go_camera, btn_go_chartlet, btn_go_collage, btn_go_movie);
    }

    @Override
    public void doInitData() {

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            filepath = bundle.getString("savefile");

            from = bundle.getString("from");
            tv_filepath.setText("图片保存在" + filepath);
        }
        mShareTools = new ShareTools(mActivity, filepath);
        //loadShareImage();
        //showPathToast();
    }

//    void showPathToast() {
//        if (!StringHelper.isEmpty(filepath)) {
//            ObjectAnimator.ofFloat(save_tips, "alpha", 0, 1).setDuration(4000).start();
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                ActivityHelper.startActivity(mActivity, ActivityCamera.class);
                finish();
                break;
            case R.id.right_btn:
//                ActivityHelper.startActivity(mActivity, MenuActivity.class);
//                BasePostEvent
//                EventBus.getEventBus().post();
                Bundle bundle = new Bundle();
                EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.FINISH_TO_MENU_PAGE, bundle));
                ActivityHelper.startActivity(mActivity, MenuActivity.class);
                finish();
                break;
            case R.id.share_btn_friend:
                mShareTools.sendBitmapToWeixin(true);
//                Toast.makeText(mContext, "分享成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.share_btn_qq:
                if (isAppInstalled(mContext, "com.tencent.mobileqq"))
                    mShareTools.doShareToQQ();
                else {
                    Toast.makeText(mContext, "未安装QQ", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.share_btn_qzone:
                if (isAppInstalled(mContext, "com.tencent.mobileqq"))
                    mShareTools.doShareToQzone();
                else {
                    Toast.makeText(mContext, "未安装QQ", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.share_btn_sina:
                if (isAppInstalled(mContext, "com.sina.weibo"))
                    mShareTools.doShareToWeibo();
                else {
                    Toast.makeText(mContext, "未安装新浪微博", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.share_btn_wechat:
                if (isAppInstalled(mContext, "com.tencent.mm"))
                    mShareTools.sendBitmapToWeixin(false);
                else {
                    Toast.makeText(mContext, "未安微信", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_go_camera:
                ActivityHelper.startActivity(mActivity, ActivityCamera.class);
//                ActivityHelper.startActivity(mActivity, MenuActivity.class);
//                finish();
                break;
            case R.id.btn_go_chartlet:
                if ("collage".equals(from)||"connect".equals(from)) {
                    ActivityHelper.startActivity(mActivity, CollageSampleSelectActivity.class);
                } else {
                    ActivityHelper.startActivity(mActivity, AlbumPhotoSelectActivity.class);
                }
//                finish();
                break;
            case R.id.btn_go_collage:
                ActivityHelper.startActivity(mActivity, CollageSampleSelectActivity.class);
//                finish();
                break;
            case R.id.btn_go_movie:
                ActivityHelper.startActivity(mActivity, MovieCameraActivity.class);
//                finish();
                break;

            default:
                break;
        }
    }

    @Override
    public File onSave() {
        return new File(filepath);
    }

    public boolean isAppInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        List<String> pName = new ArrayList<String>();
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);
    }
}
