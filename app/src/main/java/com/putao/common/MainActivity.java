package com.putao.common;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.putao.camera.R;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {
    private TextView tv_xml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ntest);

        tv_xml = (TextView) findViewById(R.id.tv_xml);
        Animation animation = XmlUtils.xmlToModel(readAssetsFile("set.xml"), "animation", Animation.class);
        tv_xml.setText(animation.toString());
    }

    private String readAssetsFile(String fileName) {
        String result = null;
        try {
            InputStream is = getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            result = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
