
package com.putao.camera.constants;

public class PuTaoConstants {
    public static final boolean isDebug = true;
    public static final int PHOTO_CONTENT_PROVIDER_REFRESH = 0x001;
    public static final int WATER_MARK_ICON_CHOICE_REFRESH = 0x002;
    // 从相片浏览页面跳转到摄像头页面关闭当中页面
    public static final int GALLERY_GO_TO_TAKE_PICTURE_CLOSE = 0x003;
    public static final int WATER_FILTER_EFFECT_CHOICE_REFRESH = 0x004;
    public static final int WATER_MARK_TAKE_PHOTO = 0x005;
    public static final int WATER_MARK_TAKE_CANCEL = 0x006;
    // 选择城市
    public static final int WATER_MARK_CITY_SELECTED = 0X007;
    // 选择日期
    public static final int WATER_MARK_DATE_SELECTED = 0X008;
    // 水印气泡文字编辑
    public static final int WATER_MARK_TEXT_EDIT = 0X009;
    // 有声贺卡图片选择返回
    public static final int VOICE_PHOTO_CONTENT_PROVIDER_BACK = 0x00A;
    public static final int VOICE_PHOTO_TAKE_PHOTO_BACK = 0x00B;
    // 有声分享关闭按钮
    public static final int VOICE_PHOTO_SHARE_FINISH = 0x00C;
    public static final int DOWNLOAD_FILE_DOWNLOADING = 0x00D;
    public static final int DOWNLOAD_FILE_FINISH = 0x00E;
    public static final int UNZIP_FILE_FINISH = 0x012;
    public static final int PHOTO_EDIT_CUT_FINISH = 0x00F;


    //翻译Event
    public static final int MOVIE_CAPTION_TRANSLATE = 0x10;
    //Welcome 页面结束
    public static final int WELCOME_FINISH_EVENT = 0x11;
    //电影文字Dialog结束
    public static final int DIALOG_CAPTIONS_FINISH_EVENT = 0x12;
    //拼图-拍照结束
    public static final int COLLAGE_CAMERA_FINISH = 0x13;
    public static final int REFRESH_COLLAGE_MANAGEMENT_ACTIVITY = 0x013;
    public static final int REFRESH_WATERMARK_MANAGEMENT_ACTIVITY = 0x014;
    //选择拼接替换替换
    public static final int EVENT_CONNECT_PHOTO_SELECT = 0x15;
    public static final int EVENT_COLLAGE_PHOTO_SELECT = 0x16;


    public static final int FINISH_TO_MENU_PAGE = 0x17;
    public static final int FINISH_TO_MOVIE_MAKE_PAGE = 0x18;

    public static final int EVENT_FINISH_LOGO = 0x19;

    // 打开图片上面显示AR贴纸
    public static final int OPEN_AR_SHOW_ACTIVITY = 0x20;
    // 保存AR动态视频的时候，整组图片保存结束事件
    public static final int SAVE_AR_SHOW_IMAGE_COMPELTE = 0x21;

    public static final String CUT_TYPE = "CUT_TYPE";

    public static final String WEIBO_APP_KEY = "2876121208";
    //微信葡萄拍拍
    //	public static final String WEIXIN_APP_KEY = "wx1bcb58803cd0a297";
    //微信葡萄相机
    //    public static final String WEIXIN_APP_KEY = "wx6910ef6d3b3f7805";
    //微信葡萄亲子相机
    public static final String WEIXIN_APP_KEY = "wx1f67f2c75acfaf0c";
    public static final String QQ_APP_KEY = "1104075952";
    // PREFERENC Key
    //是否点击过拼图
    public static final String PREFERENC_FIRST_USE_VOICE = "PREFERENC_FIRST_USE_VOICE";

    public static final String PREFERENC_FIRST_USE_COLLAGE = "PREFERENC_FIRST_USE_COLLAGE";
    public static final String PREFERENC_FIRST_USE_APPLICATION = "PREFERENC_FIRST_USE_APPLICATION";
    public static final String PREFERENC_VERSION_CODE = "PREFERENC_VERSION_CODE";
    public static final String PREFERENC_WATERMARK_SRC_VERSION_CODE = "PREFERENC_WATERMARK_SRC_VERSION_CODE";
    // 拼图本地资源版本号
    public static final String PREFERENC_COLLAGE_SRC_VERSION_CODE = "PREFERENC_COLLAGE_SRC_VERSION_CODE";
    // 葡萄水印配置json文件
    public static final String PREFERENC_WATERMARK_JSON = "PREFERENC_WATERMARK_JSON";
    // 葡萄拼图配置json文件
    public static final String PREFERENC_COLLAGE_CONFIG_JSON = "PREFERENC_COLLAGE_JSON";
    /**
     * 经度
     */
    public static final String PREFERENC_LOCATION_LONGITUDE = "PREFERENC_LOCATION_LONGITUDE";
    /**
     * 维度
     */
    public static final String PREFERENC_LOCATION_LATITUDE = "PREFERENC_LOCATION_LATITUDE";
    //WIFI下资源自动更新设置
    public static final String PREFERENC_WIFI_AUTO_DOWNLOAD_SETTING = "PREFERENC_WIFI_AUTO_DOWNLOAD_SETTING";
    //移动网络下资源自动更新设置
    public static final String PREFERENC_MMCC_AUTO_DOWNLOAD_SETTING = "PREFERENC_MMCC_AUTO_DOWNLOAD_SETTING";
    //拍照声音设置
    public static final String PREFERENC_CAMERA_SOUND_SETTING = "PREFERENC_CAMERA_SOUND_SETTING";
    //打开后,启动应用直接进入相机
    public static final String PREFERENC_CAMERA_ENTER_SETTING = "PREFERENC_CAMERA_ENTER_SETTING";
    //进入相机拍照时,是否有水印
    public static final String PREFERENC_CAMERA_WATER_MARK_SETTING = "PREFERENC_CAMERA_WATER_MARK_SETTING";
    /**
     * 拍照添加水印最后一次选择索引
     */
    public static final String PREFERENC_WATERMARK_SCENE_INDEX = "PREFERENC_WATERMARK_SCENE_INDEX";
    public static final String PREFERENC_WATERMARK_SCENE_ICON_INDEX = "PREFERENC_WATERMARK_SCENE_ICON_INDEX";
    //电影拍照横屏提示
    public static final String PREFERENC_MOVIEW_CAMERA_ORIENTATION_HIDE = "PREFERENC_MOVIEW_CAMERA_ORIENTATION";
    //水印数据的版本
    public static final String PREFERENC_WATER_MARK_CONFIGINFO_VERSION = "PREFERENC_WATER_MARK_CONFIGINFO_VERSION";
    /**
     * 当前所在城市
     */
    public static final String PREFERENCE_CURRENT_CITY = "PREFERENCE_CURRENT_CITY";
    public static final String PAIAPI_PHOTOS_FOLDER = "PutaoCamera";
    public static final String PAIAPI_PHOTOS_CAMERA = "Camera";
    public static final String PAIAPI_PHOTOS_VIDEO = "Video";
    // 水印文件夹
    public static final String PAIPAI_WATERMARK_FLODER_NAME = "watermark";
    // 有声贺卡文件夹
    public static final String PAIPAI_VOICE_PHOTO_FLODER_NAME = "/voice_photo/";
    // 拼图资源文件夹名称
    public static final String PAIPAI_COLLAGE_FLODER_NAME = "collage";
    // 拼图资源路径
    public static final String PAIPAI_COLLAGE_RESOURCE_PATH = "/collage/";
    // 拼图配置文件名称
    public static final String PAIPAI_COLLAGE_CONFIG_NAME = "collage_config.json";
    // 拼图资源增量包路径
    public static final String PAIPAI_COLLAGE_UPDATE_PACKAGE_PATH = "/collage_unzip/";
    // 资源更新服务器Host
    public static final String PAIPAI_SERVER_HOST = "http://api.camera.putao.com/";
    // 有声贺卡资源上传host
    public static final String PAIPAI_SERVER_HOST_VOICE_UPLOAD = "http://api.camera.putao.im";

//    public static final String PAIPAI_SERVER_HOST = "http://api.camera.putao.com";

    // 升级包测试URL
    public static final String PAIPAI_UPDATE_PACKAGE_URL = "pub/camera/test/";
    // 升级包URL
    //public static final String PAIPAI_UPDATE_PACKAGE_URL = "pub/camera/watermark/";
    //官网地址
    public static final String ORG_WEBSITE_URL = "http://www.putao.com/";
    public static final String MOVIE_DEFAULT_CAPTION_URL = "http://ftp.putao.im/pub/camera/movelines.json";
    //百度翻译URL,使用示例:BAI_DU_TRANSLATE_URL+"你好"
    public static final String BAI_DU_TRANSLATE_URL = "http://openapi.baidu.com/public/2.0/bmt/translate?client_id=4FZ0ZGPf4xpRhGC3SlA266gQ&from=zh&to=en&q=";



}
