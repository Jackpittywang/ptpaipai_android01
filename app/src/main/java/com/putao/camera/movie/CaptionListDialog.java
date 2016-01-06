package com.putao.camera.movie;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.putao.camera.R;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.movie.adapter.MovieCaptionListAdapter;
import com.putao.camera.movie.model.MovieCaption;
import com.putao.camera.movie.model.MovieCaptionConfig;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.DisplayHelper;

public class CaptionListDialog extends Dialog {

    Context mContext;
    private ListView list_caption_default;
    private LinearLayout subtitle_button_more;
    private ImageView point_before_text;

    public CaptionListDialog(Context context) {
        super(context);
        mContext = context;
    }

    public CaptionListDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // this.setContentView(R.layout.dialog_movie_caption_list);
        this.setContentView(getLayoutInflater().inflate(R.layout.dialog_movie_caption_list, null), new LayoutParams(DisplayHelper.getScreenWidth(),
                DisplayHelper.getScreenHeight() * 3 / 4));

        point_before_text = (ImageView) findViewById(R.id.point_before_text);
        point_before_text.setImageBitmap(BitmapHelper.getCircleBitmap(Color.WHITE, 20));

        list_caption_default = (ListView) findViewById(R.id.list_caption_default);

        subtitle_button_more = (LinearLayout) findViewById(R.id.subtitle_button_more);
        subtitle_button_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDismiss();
            }
        });

        initCaptionList();
    }

    protected void dialogDismiss() {
        dismiss();
    }

    void initCaptionList() {
        try {
            final MovieCaptionConfig movieCaptionConfig = MovieCaption.newInstance().getMovieCaptionConfig();
            if (movieCaptionConfig != null) {
                MovieCaptionListAdapter mMovieCaptionListAdapter = new MovieCaptionListAdapter(mContext, movieCaptionConfig.movieLines);
                list_caption_default.setAdapter(mMovieCaptionListAdapter);
                mMovieCaptionListAdapter.notifyDataSetChanged();
                list_caption_default.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MovieCaptionConfig.MovieCaptionItem item = movieCaptionConfig.movieLines.get(position);
                        Bundle bundle = new Bundle();
                        bundle.putString("cn_line", item.cn_line);
                        bundle.putString("en_line", item.en_line);
                        EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.DIALOG_CAPTIONS_FINISH_EVENT, bundle));

                        dialogDismiss();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
