package com.putao.camera.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.putao.account.AccountHelper;
import com.putao.camera.R;
import com.putao.camera.application.MainApplication;
import com.putao.camera.base.PTXJActivity;
import com.putao.camera.base.SelectPopupWindow;
import com.putao.camera.bean.UserInfo;
import com.putao.camera.constants.UploadApi;
import com.putao.camera.constants.UserApi;
import com.putao.camera.editor.PhotoShareActivity;
import com.putao.camera.menu.MenuActivity;
import com.putao.camera.util.ActivityHelper;
import com.sunnybear.library.controller.eventbus.EventBusHelper;
import com.sunnybear.library.model.http.UploadFileTask;
import com.sunnybear.library.model.http.callback.JSONObjectCallback;
import com.sunnybear.library.model.http.callback.SimpleFastJsonCallback;
import com.sunnybear.library.util.FileUtils;
import com.sunnybear.library.util.ImageUtils;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.StringUtils;
import com.sunnybear.library.util.ToastUtils;
import com.sunnybear.library.view.image.ImageDraweeView;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 完善用户信息
 * Created by guchenkai on 2015/11/29.
 */
public class CompleteActivity extends PTXJActivity implements View.OnClickListener {
    //    public static final String EVENT_USER_INFO_SAVE_SUCCESS = "user_info_save_success";
    public static final String NICK_NAME = "nick_name";
    public static final String USER_INFO = "user_info";
    @Bind(R.id.ll_main)
    LinearLayout ll_main;
    @Bind(R.id.rl_header_icon)
    RelativeLayout rl_header_icon;
    @Bind(R.id.iv_header_icon)
    ImageDraweeView iv_header_icon;
    @Bind(R.id.rl_nick_name)
    RelativeLayout rl_nick_name;
    @Bind(R.id.tv_nick_name)
    TextView tv_nick_name;
    @Bind(R.id.rl_user_info)
    RelativeLayout rl_user_info;
    @Bind(R.id.tv_user_info)
    TextView tv_user_info;


    private SelectPopupWindow mSelectPopupWindow;
    private final int CAMERA_REQCODE = 1;
    private final int ALBUM_REQCODE = 2;
    private final int CHANGE_NICK = 3;
    private final int CHANGE_INFO = 4;
    //=====================上传相关===========================
    private String uploadToken;//上传token
    private File uploadFile;//上传文件
    private String sha1;//上传文件sha1

    private String filePath;//头像文件路径
    private String nick_name;//用户昵称
    private String profile;//个人简介
    private String from;
    private String path;
    private String imgpath;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = (Bundle) msg.obj;
            //上传PHP服务器
            upload(bundle.getString("ext"), bundle.getString("filename"), bundle.getString("hash"));
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_complete;
    }

    @Override
    public void onViewCreatedFinish(Bundle savedInstanceState) {
        addNavigation();
        filePath = MainApplication.sdCardPath + File.separator + "head_icon.jpg";
        initInfo();
//        IndexActivity.isNotRefreshUserInfo = false;

        mSelectPopupWindow = new SelectPopupWindow(mContext) {
            @Override
            public void onFirstClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQCODE);
            }

            @Override
            public void onSecondClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, ALBUM_REQCODE);
            }
        };

        Intent intent = this.getIntent();
        if (intent != null) {
            from = intent.getStringExtra("from");
            path = intent.getStringExtra("savefile");
            imgpath = intent.getStringExtra("imgpath");
        }

    }

    private void initInfo() {
        networkRequest(UserApi.getUserInfo(), new SimpleFastJsonCallback<UserInfo>(UserInfo.class, loading) {
            @Override
            public void onSuccess(String url, UserInfo result) {
                String re = result.toString();

                iv_header_icon.setImageURL(result.getHead_img());
                tv_nick_name.setText(result.getNick_name());
                tv_user_info.setText(result.getProfile().isEmpty() ? "这个用户很懒" : result.getProfile());
                AccountHelper.setUserInfo(result);
                loading.dismiss();
                if (from.equals("share")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("from", "complete");
                    bundle.putString("savefile", path);
                    bundle.putString("imgpath", imgpath);
                    ActivityHelper.startActivity(CompleteActivity.this, PhotoShareActivity.class, bundle);
                    finish();
                }


            }

            @Override
            public void onFailure(String url, int statusCode, String msg) {
                super.onFailure(url, statusCode, msg);
                ToastUtils.showToastLong(mContext, "登录失败请重新登录");
                if (from.equals("share")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("from", "complete");
                    bundle.putString("savefile", path);
                    bundle.putString("imgpath", imgpath);
                    ActivityHelper.startActivity(CompleteActivity.this, PhotoShareActivity.class, bundle);
                    finish();
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityHelper.startActivity(this, MenuActivity.class);
    }

    @Override
    protected String[] getRequestUrls() {
        return new String[0];
    }

    @OnClick({R.id.rl_header_icon, R.id.rl_nick_name, R.id.rl_user_info})
    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.rl_header_icon://选择用户头像
                mSelectPopupWindow.show(ll_main);
                break;
            case R.id.rl_nick_name://修改用户昵称
                bundle.putString(NICK_NAME, tv_nick_name.getText().toString());
                Intent nickIntent = new Intent(this, NickActivity.class);
                nickIntent.putExtra(NICK_NAME, bundle);
                startActivityForResult(nickIntent, CHANGE_NICK);
                break;
            case R.id.rl_user_info://修改用户简介
                bundle.putString(USER_INFO, tv_user_info.getText().toString());
                Intent infoiIntent = new Intent(this, UserInfoActivity.class);
                infoiIntent.putExtra(USER_INFO, bundle);
                startActivityForResult(infoiIntent, CHANGE_INFO);
                break;
        }
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
     * 校检sha1
     *
     * @param uploadFilePath 上传文件路径
     */
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
                    upload("jpg", hash, hash);
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

    /**
     * 上传PHP服务器
     */
    private void upload(String ext, String filename, String filehash) {
        networkRequest(UserApi.userEdit(ext, filename, filehash),
                new SimpleFastJsonCallback<String>(String.class, loading) {
                    @Override
                    public void onSuccess(String url, String result) {
                        Logger.i("保存用户信息");
//                        EventBusHelper.post(EVENT_USER_INFO_SAVE_SUCCESS, EVENT_USER_INFO_SAVE_SUCCESS);
                        EventBusHelper.post(LoginActivity.EVENT_LOGIN, LoginActivity.EVENT_LOGIN);
//                        startActivity(IndexActivity.class);
//                        finish();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        EventBusHelper.post(EVEVT_USER_INFO, EVEVT_USER_INFO);
        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            switch (requestCode) {
                case CAMERA_REQCODE://相机选择
                    Bundle bundle = data.getExtras();
                    bitmap = (Bitmap) bundle.get("data");
//                    bitmap = ImageUtils.getSmallBitmap(picturePath, 320, 320);
                    iv_header_icon.setDefaultImage(bitmap);
                    ImageUtils.bitmapOutSdCard(bitmap, filePath);
                    checkSha1(filePath);
                    break;
                case ALBUM_REQCODE://相册选择
//                    ToastUtils.showToastShort(this, "系统图库返回");

                    Uri selectedImage = data.getData();
                    String picturePath = ImageUtils.getImageAbsolutePath(CompleteActivity.this, selectedImage);

                    Logger.d(picturePath);
                    bitmap = ImageUtils.getSmallBitmap(picturePath, 320, 320);
                    iv_header_icon.resize(320, 320).setDefaultImage(bitmap);
                    ImageUtils.bitmapOutSdCard(bitmap, filePath);
                    checkSha1(filePath);
                    break;
            }
        }
        switch (requestCode) {
            case CHANGE_NICK:
                if (resultCode == 1) {
                    EventBusHelper.post(LoginActivity.EVENT_LOGIN, LoginActivity.EVENT_LOGIN);
                    initInfo();
                }
                break;
            case CHANGE_INFO:
                if (resultCode == 1)
                    initInfo();
                break;
        }
    }


    @Override
    public void onLeftAction() {
        super.onLeftAction();
    }
}
