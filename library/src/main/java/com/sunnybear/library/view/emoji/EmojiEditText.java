package com.sunnybear.library.view.emoji;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.widget.EditText;

import com.sunnybear.library.util.StringUtils;

/**
 * Emoji表情文本输入
 * Created by guchenkai on 2015/11/24.
 */
public class EmojiEditText extends EditText {
    private Editable mEditable;
    private int start;
    private int end;

    public EmojiEditText(Context context) {
        super(context);
    }

    public EmojiEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmojiEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void delete() {
        int index = getSelectionStart();
        mEditable = getText();
        start = 0;
        end = 0;
        if (StringUtils.equals("]", String.valueOf(mEditable.charAt(index - 1)))) {
            end = index - 1;
            for (int i = end; i >= 0; i--) {
                if (StringUtils.equals("[", String.valueOf(mEditable.charAt(i)))) {
                    start = i;
                    break;
                }
            }
            mEditable.delete(start, index);
            return;
        }
        mEditable.delete(index - 1, index);
    }
}
