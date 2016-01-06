package com.putao.camera.welcome.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.putao.camera.R;
import com.putao.camera.event.EventBus;
import com.putao.camera.menu.MenuActivity;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.welcome.CircleSwitchActivity;

public class SwitchFragment extends Fragment {
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
        Button bt_go = (Button) layout.findViewById(R.id.bt_go);
        int position = getArguments().getInt("position");
        final boolean fromAbout = getArguments().getBoolean("fromAbout");
        iv_logo.setImageResource(CircleSwitchActivity.logos[position]);
        if (!fromAbout && (position == CircleSwitchActivity.logos.length - 1)) {
            bt_go.setVisibility(View.VISIBLE);
        } else {
            bt_go.setVisibility(View.GONE);
        }

//        if(fromAbout&&position == CircleSwitchActivity.logos.length - 1){
//            iv_logo.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    
//                    GestureDetector mGestureDetector = new GestureDetector(getActivity(),new SimpleOnGestureListener(){
//                        @Override
//                        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                            Loger.d("chen,mGestureDetector:onFling -----" + (e2.getAction()) + ",(" + e1.getX() + "," + e1.getY() + ") ,("
//                                    + e2.getX() + "," + e2.getY() + ")");
//                            return super.onFling(e1, e2, velocityX, velocityY);
//                        }
//                      
//                        @Override
//                        public boolean onDown(MotionEvent e) {
//                            Loger.i("chen,mGestureDetector:onDown-----" + e.toString());
//                            Bundle bundle = new Bundle();
//                            EventBus.getEventBus().post(
//                                  new BasePostEvent(PuTaoConstants.WELCOME_FINISH_EVENT, bundle));
//                            return false;
//                        }   
//                        public boolean onSingleTapUp(MotionEvent e) {
//                            Loger.i("chen,mGestureDetector:onSingleTapUp-----" + e.toString());
//                            return false;
//                        }
//                        
//                        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//                            Loger.i("chen,mGestureDetector:onScroll-----" + e1.toString()+",,,,,"+e2.toString());
//                          return false;  
//                        }
//                        
//                    });
//                    
//                    mGestureDetector.onTouchEvent(event);
//                    return false;
//                }
//            });   
//            
//        }


        bt_go.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Bundle bundle = new Bundle();
                ActivityHelper.startActivity(getActivity(), MenuActivity.class, bundle);
                getActivity().finish();
            }
        });
        return layout;
    }
}
