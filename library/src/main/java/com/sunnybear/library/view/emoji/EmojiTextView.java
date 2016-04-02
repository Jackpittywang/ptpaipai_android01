package com.sunnybear.library.view.emoji;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sunnybear.library.BasicApplication;
import com.sunnybear.library.util.StringUtils;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 支持自定义表情的TextView
 * Created by guchenkai on 2015/11/24.
 */
public class EmojiTextView extends TextView {
    private Html.ImageGetter mImageGetter;
    private Map<String, String> emojis;

    public EmojiTextView(Context context) {
        this(context, null, 0);
    }

    public EmojiTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmojiTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        emojis = BasicApplication.getEmojis();
        init(context);
    }

    private void init(final Context context) {
        mImageGetter = new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                if (StringUtils.isEmpty(source))
                    throw new IllegalArgumentException("没有对应的表情");
                Bitmap bitmap = BitmapFactory.decodeFile(source);
//                Drawable emoji = getResources().getDrawable(Integer.parseInt(source));
                Drawable emoji = new BitmapDrawable(bitmap);
                emoji.setBounds(0, 0, (int) getTextSize(), (int) getTextSize());
                return emoji;
            }
        };
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (!isEmoji(text)) {
            super.setText(text, type);
            return;
        }
        text = parseEmoji(text.toString());
        CharSequence sequence = Html.fromHtml(text.toString(), mImageGetter, null);
        super.setText(sequence);
    }

    /**
     * 解析表情
     *
     * @param text 文本
     * @return String
     */
    private String parseEmoji(String text) {
        Set<String> keys = emojis.keySet();
        for (String key : keys) {
            text = text.replace(key, "<img src='" + emojis.get(key) + "'/>");
        }
        return text.replaceAll("\\[", "").replaceAll("\\]", "");
    }

    /**
     * 判断文本中是否带有表情
     *
     * @param text 文本
     * @return boolean
     */
    private boolean isEmoji(CharSequence text) {
        if(StringUtils.isEmpty(text+""))
            return false;
        return Pattern
                .compile(".*?\\[(.*?)\\].*?",
                        Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
                .matcher(text).find();
    }
}
