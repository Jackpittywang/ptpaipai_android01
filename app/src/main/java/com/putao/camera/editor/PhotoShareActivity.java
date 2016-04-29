package com.putao.camera.editor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.putao.account.AccountHelper;
import com.putao.camera.R;
import com.putao.camera.album.AlbumPhotoSelectActivity;
import com.putao.camera.base.PTXJActivity;
import com.putao.camera.camera.ActivityCamera;
import com.putao.camera.constants.UploadApi;
import com.putao.camera.constants.UserApi;
import com.putao.camera.setting.watermark.management.TemplateManagemenActivity;
import com.putao.camera.share.ShareTools;
import com.putao.camera.user.LoginActivity;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.ToasterHelper;
import com.sunnybear.library.model.http.UploadFileTask;
import com.sunnybear.library.model.http.callback.JSONObjectCallback;
import com.sunnybear.library.model.http.callback.SimpleFastJsonCallback;
import com.sunnybear.library.util.FileUtils;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/*
import com.putao.camera.thirdshare.ShareTools;
import com.putao.camera.thirdshare.dialog.ThirdShareDialog;
*/

/**
 * Created by jidongdong on 15/3/3.
 */
//public class PhotoShareActivity extends PTXJActivity implements View.OnClickListener, ThirdShareDialog.ThirdShareDialogProcessListener {

public class PhotoShareActivity extends PTXJActivity implements View.OnClickListener {

    private String filepath;
    //    public static ShareTools mShareTools;
    private String from;
    private String imgpath;
     ProgressDialog progressDialog;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_photo_share;
    }

    @Override
    protected void onViewCreatedFinish(Bundle saveInstanceState) {
        addNavigation();
        progressDialog = new ProgressDialog(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            filepath = bundle.getString("savefile");
            from = bundle.getString("from");
            imgpath = bundle.getString("imgpath");
        }

        if(from.equals("complete")){
//            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在处理...");
            progressDialog.show();
            tag = 0;
            checkSha1(filepath);
        }
//        mShareTools = new ShareTools(this, filepath);
        //loadShareImage();
        //showPathToast();
        handler.sendEmptyMessageDelayed(0x100,5000);


    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0x100){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        updataVideo();
                    }
                });
            }

        }
    };

    private void updataVideo(){
        MediaScannerConnection.scanFile(mContext, new String[]{filepath}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {

            }
        });
    }

    @Override
    protected String[] getRequestUrls() {
        return new String[0];
    }


 /*   @Override

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
//        btn_go_chartlet = (LinearLayout) findViewById(R.id.btn_go_chartlet);
        btn_go_camera = (LinearLayout) findViewById(R.id.btn_go_camera);
        btn_go_collage = (LinearLayout) findViewById(R.id.btn_go_collage);
        btn_go_movie = (LinearLayout) findViewById(R.id.btn_go_movie);
        addOnClickListener(btn_back, btn_home, share_btn_friend, share_btn_sina, share_btn_qq, share_btn_qzone, share_btn_wechat, btn_go_camera,  btn_go_collage, btn_go_movie);
    }*/
   /* @Override
    public void doInitData() {

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            filepath = bundle.getString("savefile");
            from = bundle.getString("from");
            tv_filepath.setText("图片保存在" + filepath);
            videoPath=bundle.getString("videoPath");
        }
        mShareTools = new ShareTools(mActivity, filepath);
        //loadShareImage();
        //showPathToast();
    }*/

//    void showPathToast() {
//        if (!StringHelper.isEmpty(filepath)) {
//            ObjectAnimator.ofFloat(save_tips, "alpha", 0, 1).setDuration(4000).start();
//        }
//    }


    @Override
    public void onRightAction() {
        super.onRightAction();
        ActivityHelper.startActivity(this, ActivityCamera.class);
        finish();
    }

    @Override
    public void onLeftAction() {
        super.onLeftAction();
    }

    private int tag;

    @OnClick({R.id.share_btn_friend, R.id.share_btn_qq, R.id.share_btn_sina, R.id.share_btn_wechat, R.id.rl_beautify, R.id.rl_take_photo, R.id.rl_template})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_btn_friend:
                if (isAppInstalled(mContext, "com.tencent.mm")) {
                    if (from.equals("dynamic")) {
                        if (!AccountHelper.isLogin()) {
                            ToasterHelper.showShort(this, "请登录葡萄账户", R.drawable.img_blur_bg);
                            Bundle bundle = new Bundle();
                            bundle.putString("from", "share");
                           /* bundle.putString("path", filepath);
                            bundle.putString("imgpath",imgpath);*/
                            ActivityHelper.startActivity(this, LoginActivity.class, bundle);
//                            finish();
                        } else if (AccountHelper.isLogin()) {
                            progressDialog.setMessage("正在处理...");
                            progressDialog.show();
                            tag = 0;
                            checkSha1(filepath);
//                            ShareTools.wechatWebShare(this, true,null,null, imgpath,url);
                        }
                    } else {
//                        mShareTools.sendBitmapToWeixin(true);
                        ShareTools.newInstance(WechatMoments.NAME).setImagePath(filepath).execute(mContext);
                    }

                } else {
                    Toast.makeText(mContext, "未安装微信", Toast.LENGTH_SHORT).show();
                }
//
                break;
            case R.id.share_btn_qq:
                if (isAppInstalled(mContext, "com.tencent.mobileqq")) {
                    if (from.equals("dynamic")) {
                        if (!AccountHelper.isLogin()) {
                            ToasterHelper.showShort(this, "请登录葡萄账户", R.drawable.img_blur_bg);
                            Bundle bundle = new Bundle();
                            bundle.putString("from", "share");
                            ActivityHelper.startActivity(this, LoginActivity.class, bundle);
                        } else if (AccountHelper.isLogin()) {
                            progressDialog.setMessage("正在处理...");
                            progressDialog.show();
                            tag = 1;
                            checkSha1(filepath);

                        }
                    } else {
                        ShareTools.newInstance(QQ.NAME)
                                .setImagePath(filepath)
                                .execute(mContext);
                    }
                } else {
                    Toast.makeText(mContext, "未安装QQ", Toast.LENGTH_SHORT).show();
                }
                break;
           /* case R.id.share_btn_qzone:
                if (isAppInstalled(mContext, "com.tencent.mobileqq"))
                    mShareTools.doShareToQzone();
                else {
                    Toast.makeText(mContext, "未安装QQ", Toast.LENGTH_SHORT).show();
                }
                break;*/
            case R.id.share_btn_sina:
                if (isAppInstalled(mContext, "com.sina.weibo")) {
                    if (from.equals("dynamic")) {
                        if (!AccountHelper.isLogin()) {
                            ToasterHelper.showShort(this, "请登录葡萄账户", R.drawable.img_blur_bg);
                            Bundle bundle = new Bundle();
                            bundle.putString("from", "share");
                            ActivityHelper.startActivity(this, LoginActivity.class, bundle);
                        } else if (AccountHelper.isLogin()) {
                            progressDialog.setMessage("正在处理...");
                            progressDialog.show();
                            tag = 2;
                            checkSha1(filepath);
                        }
                    } else {
                        ShareTools.newInstance(SinaWeibo.NAME)
                                .setImagePath(filepath)
                                .execute(mContext);
//                    mShareTools.doShareToWeibo();
                    }
                } else {
                    Toast.makeText(mContext, "未安装新浪微博", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.share_btn_wechat:
                if (isAppInstalled(mContext, "com.tencent.mm")) {
                    if (from.equals("dynamic")) {
                        if (!AccountHelper.isLogin()) {
                            ToasterHelper.showShort(this, "请登录葡萄账户", R.drawable.img_blur_bg);
                            Bundle bundle = new Bundle();
                            bundle.putString("from", "share");
                            ActivityHelper.startActivity(this, LoginActivity.class, bundle);
                        } else if (AccountHelper.isLogin()) {
                            progressDialog.setMessage("正在处理...");
                            progressDialog.show();
                            tag = 3;
                            checkSha1(filepath);
                        }
                    } else {
                        ShareTools.newInstance(Wechat.NAME).setImagePath(filepath).execute(mContext);
                    }
                } else {
                    Toast.makeText(mContext, "未安装微信", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rl_take_photo:
                ActivityHelper.startActivity(this, ActivityCamera.class);
                finish();
                break;
            /*case R.id.btn_go_chartlet:
                if ("collage".equals(from)||"connect".equals(from)) {
                    ActivityHelper.startActivity(mActivity, CollageSampleSelectActivity.class);
                } else {
                    ActivityHelper.startActivity(mActivity, AlbumPhotoSelectActivity.class);
                }
//                finish();
                break;*/
            case R.id.rl_beautify:
                ActivityHelper.startActivity(this, AlbumPhotoSelectActivity.class);
                finish();
                break;
            case R.id.rl_template:
                ActivityHelper.startActivity(this, TemplateManagemenActivity.class);
                finish();
                break;

            default:
                break;
        }

    }

 /*   @Override
    public File onSave() {
        return new File(filepath);
    }*/

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

    private String uploadToken;//上传token
    private File uploadFile;//上传文件
    private String sha1;//上传文件sha1

    private void checkSha1(String uploadFilePath) {
        uploadFile = new File(uploadFilePath);
        sha1 = FileUtils.getSHA1ByFile(uploadFile);

        networkRequest(UploadApi.checkSha1(sha1), new JSONObjectCallback() {
            @Override
            public void onSuccess(String url, JSONObject result) {
                String hash = result.getString("hash");
                if (StringUtils.isEmpty(hash))
                    getUploadToken();
                else
                    upload("mp4", hash, hash);
            }
            @Override
            public void onCacheSuccess(String url, JSONObject result) {

            }

            @Override
            public void onFailure(String url, int statusCode, String msg) {

            }
        });
    }


    /**
     * 获得上传参数
     */
    private void getUploadToken() {
        networkRequest(UserApi.getUploadToken(), new SimpleFastJsonCallback<String>(String.class, null) {
            @Override
            public void onSuccess(String url, String result) {
                JSONObject jsonObject = JSON.parseObject(result);
                uploadToken = jsonObject.getString("uploadToken");
                Logger.d(uploadToken);
                uploadFile();
            }
        });
    }

    /**
     * 上传文件
     */
    private void uploadFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                UploadApi.uploadFile(uploadToken, sha1, uploadFile, new UploadFileTask.UploadCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {

                        Logger.d(result.toJSONString());
                        Bundle bundle = new Bundle();
                        bundle.putString("ext", result.getString("ext"));
                        bundle.putString("filename", result.getString("filename"));
                        bundle.putString("hash", result.getString("hash"));
                        //上传PHP服务器
                        mHandler.sendMessage(Message.obtain(mHandler, 0x01, bundle));
                    }
                });
            }
        }).start();
    }

//    private String url;

    /**
     * 上传PHP服务器
     */
    private void upload(String ext, String filename, String filehash) {
        networkRequest(UserApi.userMedia(ext, filename, filehash, "VIDEO"),
                new SimpleFastJsonCallback<String>(String.class, loading) {
                    @Override
                    public void onSuccess(String url, String result) {
//                        url = result;
                        String video_url = JSONObject.parseObject(result).getString("media_url");
                        progressDialog.dismiss();
                        switch (tag) {
                            case 0:
                                ShareTools.wechatWebShare(mContext, false, null, null, imgpath, video_url);
                                break;
                            case 1:
                                ShareTools.OnQQZShare(mContext, true, null, null, imgpath, video_url);
                                break;
                            case 2:
                                ShareTools.OnWeiboShare(mContext, null, imgpath,video_url);
                                break;
                            case 3:
                                ShareTools.wechatWebShare(mContext, true, "视频分享", null, imgpath, video_url);
                                break;
                        }

                    }

                    @Override
                    public void onFailure(String url, int statusCode, String msg) {
                        super.onFailure(url, statusCode, msg);
                    }
                });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = (Bundle) msg.obj;
            //上传PHP服务器
            upload(bundle.getString("ext"), bundle.getString("filename"), bundle.getString("hash"));

        }
    };


}
