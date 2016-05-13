package com.putao.camera.welcome.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.putao.camera.R;
import com.putao.camera.camera.ActivityCamera;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.welcome.CircleSwitchActivity;

public class SwitchFragment extends Fragment {
    private final long WAIT_TIME = 1 * 1000;
    int position;
    ProgressBar pbInit;
    Button bt_go;
    TextView tvTip;
    boolean fromAbout = false;

    public static SwitchFragment newInstance(Bundle bundle) {
        SwitchFragment fragment = new SwitchFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = LayoutInflater.from(getActivity()).inflate(R.layout.layout_switch_item, null);
        ImageView iv_logo = (ImageView) layout.findViewById(R.id.iv_logo);
        bt_go = (Button) layout.findViewById(R.id.bt_go);
        pbInit = (ProgressBar) layout.findViewById(R.id.pbInit);
        tvTip = (TextView) layout.findViewById(R.id.tvTip);
        position = getArguments().getInt("position");
        fromAbout = getArguments().getBoolean("fromAbout");
        iv_logo.setImageResource(CircleSwitchActivity.logos[position]);
        if ((position == CircleSwitchActivity.logos.length - 1)) {
            bt_go.setVisibility(View.VISIBLE);
        } else {
            bt_go.setVisibility(View.GONE);
        }

        bt_go.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fromAbout){
                    initRes();
                }else {
                    getActivity().finish();
                }
            }
        });


//        initRes();
        return layout;
    }

    private void initRes() {
//        boolean isFristUse = SharedPreferencesHelper.readBooleanValue(getActivity(), PuTaoConstants.PREFERENC_FIRST_USE_APPLICATION, true);
//        if ((position == CircleSwitchActivity.logos.length - 1)) {
            pbInit.setVisibility(View.VISIBLE);
            tvTip.setVisibility(View.VISIBLE);
            bt_go.setVisibility(View.GONE);
       /* } else {
            pbInit.setVisibility(View.GONE);
            tvTip.setVisibility(View.INVISIBLE);
            bt_go.setVisibility(View.VISIBLE);
            return;
        }*/
        new AsyncTask<Void, Integer, Void>() {
            int count = 0;

            @Override
            protected Void doInBackground(Void... params) {
                while (count <= 100) {
                    count++;
                    try {
                        Thread.sleep(WAIT_TIME / 100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    publishProgress(count);
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                if (values[0] <= 100) {
                    pbInit.setProgress(values[0]);
                } else {
                    pbInit.setProgress(values[0]);

                }
            }

            @Override
            protected void onPostExecute(Void aVoid) {
//                pbInit.setVisibility(View.GONE);
                tvTip.setVisibility(View.INVISIBLE);
                ActivityHelper.startActivity(getActivity(), ActivityCamera.class, new Bundle());
                getActivity().finish();
            }
        }.execute();
    }
}
