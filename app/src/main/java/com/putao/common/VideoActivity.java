package com.putao.common;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.putao.camera.R;
import com.putao.video.VideoHelper;

/**
 * 视频拼接示例
 * Created by guchenkai on 2016/1/6.
 */
public class VideoActivity extends Activity {
    private TextView tv_xml;

//    private File[] pictureFileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ntest);

        tv_xml = (TextView) findViewById(R.id.tv_xml);

//        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Camera");
//        pictureFileList = file.listFiles();

        final String command = "-f image2 -i " + Environment.getExternalStorageDirectory() + "/test_video/image%02d.jpg"
                + " -vcodec mpeg4 -r 5 -b 200k -s 480x360 " + Environment.getExternalStorageDirectory() + "/test_video/out.mp4";

        VideoHelper.getInstance(this).exectueFFmpegCommand(command.split(" "), new ExecuteBinaryResponseHandler() {
            @Override
            public void onFailure(String s) {
                Toast.makeText(VideoActivity.this, "处理失败:" + s, Toast.LENGTH_LONG).show();
                Log.e("处理失败", s);
            }

            @Override
            public void onSuccess(String s) {
                Toast.makeText(VideoActivity.this, "处理成功:" + s, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProgress(String s) {
                Log.d("VideoActivity", "progress : " + s);
            }

            @Override
            public void onStart() {
                Log.d("VideoActivity", "Started command : ffmpeg " + command);
            }

            @Override
            public void onFinish() {
                Toast.makeText(VideoActivity.this, "处理完成", Toast.LENGTH_LONG).show();
            }
        });
    }
}
