package com.putao.camera.camera.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.putao.camera.R;

/**
 * Created by jidongdong on 15/5/27.
 */
public class StarsView extends FrameLayout {
    private ImageView start_1, start_2, start_3, start_4, start_5, start_6;
    private TextView tv_text;
    private AnimatorSet set;
    private final String ScaleX = "scaleX";
    private final String ScaleY = "scaleY";
    private final String Alpha = "alpha";
    private PlayListener mPlayListener;

    public StarsView(Context context) {
        super(context);
        init(context);
    }

    public StarsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StarsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void init(Context ctx) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.layout_stars_anmi, null);
        if (view != null) {
            start_1 = (ImageView) view.findViewById(R.id.star_1);
            start_2 = (ImageView) view.findViewById(R.id.star_2);
            start_3 = (ImageView) view.findViewById(R.id.star_3);
            start_4 = (ImageView) view.findViewById(R.id.star_4);
            start_5 = (ImageView) view.findViewById(R.id.star_5);
            start_6 = (ImageView) view.findViewById(R.id.star_6);
            tv_text = (TextView) view.findViewById(R.id.tv_text);
            addAnimation();
            addView(view);
        }
    }

    void addAnimation() {
        set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(start_1, ScaleX, 0, 0, 0, 1, 0, 1, 0),
                ObjectAnimator.ofFloat(start_1, ScaleY, 0, 0, 0, 1, 0, 1, 0),
                ObjectAnimator.ofFloat(start_2, ScaleX, 0, 0, 2, 0, 1, 0),
                ObjectAnimator.ofFloat(start_2, ScaleY, 0, 0, 2, 0, 1, 0),
                ObjectAnimator.ofFloat(start_3, ScaleX, 0, 0, 0, 1, 0, 1, 0),
                ObjectAnimator.ofFloat(start_3, ScaleY, 0, 0, 0, 1, 0, 1, 0),
                ObjectAnimator.ofFloat(start_4, ScaleX, 0, 0, 0, 2, 0, 1, 0),
                ObjectAnimator.ofFloat(start_4, ScaleY, 0, 0, 0, 2, 0, 1, 0),
                ObjectAnimator.ofFloat(start_5, ScaleX, 0, 0, 1, 0, 1, 0),
                ObjectAnimator.ofFloat(start_5, ScaleY, 0, 0, 1, 0, 1, 0),
                ObjectAnimator.ofFloat(start_6, ScaleX, 0, 0, 0, 2, 0, 1, 0),
                ObjectAnimator.ofFloat(start_6, ScaleY, 0, 0, 0, 2, 0, 1, 0),
                ObjectAnimator.ofFloat(tv_text, Alpha, 0, 1, 1, 1, 0)
        );
        set.setDuration(2000);
        set.addListener(animatorListener);
    }

    Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (mPlayListener != null) {
                mPlayListener.playOver();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    public interface PlayListener {
        void playOver();
    }

    public void Play() {
        set.start();
    }

    public void Play(PlayListener listener) {
        mPlayListener = listener;
        Play();
    }
}
