package com.putao.camera.editor.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;

import com.putao.camera.bean.WaterMarkIconInfo;
import com.putao.camera.bean.WaterText;
import com.putao.camera.util.Loger;
import com.putao.camera.util.StringHelper;

/**
 * Created by ji dong dong on 15/1/15.
 */
public class TextWaterMarkView extends WaterMarkView {

    private ArrayList<WaterText> textList;
    private TextOnClickListener mTextOnClickListener;
    private Bitmap btm_drawable;
    private ArrayList<RectF> rectFList;
    private WaterMarkIconInfo markIconInfo;

    public TextWaterMarkView(Context context, Bitmap watermark) {
        super(context, watermark);
        btm_drawable = watermark;
        textList = new ArrayList<WaterText>();
        rectFList = new ArrayList<RectF>();
    }

    public TextWaterMarkView(Context context, Bitmap watermark, ArrayList<WaterText> waterTextList, WaterMarkIconInfo iconInfo) {
        super(context, watermark);
        btm_drawable = watermark;
        textList = waterTextList;
        markIconInfo = iconInfo;
        initTextList();
    }

    public TextWaterMarkView(Context context, Bitmap watermark, ArrayList<WaterText> waterTextList, WaterMarkIconInfo iconInfo, boolean isCanRemove) {
        super(context, watermark, isCanRemove);
        btm_drawable = watermark;
        textList = waterTextList;
        markIconInfo = iconInfo;
        initTextList();
    }

    private void initTextList() {

        if (textList != null) {
            rectFList = new ArrayList<RectF>();
            for (int i = 0; i < textList.size(); i++) {
                rectFList.add(new RectF(textList.get(i).left, textList.get(i).top, textList.get(i).right, textList.get(i).bottom));
            }
            reDraw();
        }
    }

    public void setTextOnclickListener(TextOnClickListener listener) {
        mTextOnClickListener = listener;
        setOnRemoveWaterListener(listener);
    }

    @Override
    void WaterMarkClicked(float x, float y) {
        if (mTextOnClickListener != null) {
            for (int i = 0; i < rectFList.size(); i++) {
                if (rectFList.get(i).contains(x, y)) {
                    if (!textList.get(i).eventType.equals(WaterTextEventType.TYPE_SELECT_NONE)) {
                        mTextOnClickListener.onclicked(markIconInfo, i);
                    }
                    break;
                } else {
                    continue;
                }
            }
        } else {
            Loger.i("TextOnClickListener is null");
        }
    }

    public void AddText(WaterText text) {
        if (textList != null) {
            textList.add(text);
            reDraw();
        }
    }

    /**
     * @return
     */
    public WaterMarkIconInfo getMarkIconInfo() {
        return this.markIconInfo;
    }

    /**
     * set water text by index
     *
     * @param index
     * @param newText
     */
    public void setWaterText(int index, String newText) {
        if (textList == null)
            return;
        textList.get(index).text = newText;
        reDraw();
    }


    /**
     * set water text by index
     *
     * @param eventType
     * @param newText
     */
    public void setWaterTextByType(String eventType, String newText) {
        if (textList == null)
            return;
        for (int i = 0; i < textList.size(); i++) {
            if (textList.get(i).eventType.equals(eventType)) {
                textList.get(i).text = newText;
                break;
            }
        }
        reDraw();
    }


    /**
     * set water text by index
     *
     * @param index
     */
    public String getWaterText(int index) {
        if (textList == null)
            return "";
        return textList.get(index).text;
    }


    /**
     * set water text by eventType
     *
     * @param eventType
     */
    public String getWaterTextByType(String eventType) {
        if (textList == null)
            return "";
        String txt = "";
        for (int i = 0; i < textList.size(); i++) {
            if (textList.get(i).eventType.equals(eventType)) {
                txt = textList.get(i).text;
                break;
            }
        }
        return txt;
    }


    /**
     * redraw the water bitmap
     */
    public void reDraw() {
        Bitmap new_bit = Bitmap.createBitmap(btm_drawable.getWidth(), btm_drawable.getHeight(), btm_drawable.getConfig());
        Canvas canvas = new Canvas(new_bit);
        canvas.save();
        canvas.drawBitmap(btm_drawable, 0, 0, null);
        for (int i = 0; i < textList.size(); i++) {
            WaterText textobj = textList.get(i);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setTextSize(textobj.textSize);
            paint.setColor(Color.parseColor(textobj.textColor));
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTypeface(Typeface.DEFAULT);
            float text_width = paint.measureText(textobj.text);

            if (markIconInfo.type.equals(WaterType.TYPE_TEXTEDIT)) {
                Paint.FontMetrics fm = paint.getFontMetrics();
                int text_height = (int) (Math.ceil(fm.descent - fm.top) + 2);// 获得字体高度
                float text_panel_witdh = Math.abs(textobj.right - textobj.left);
                float text_panel_height = Math.abs(textobj.top - textobj.bottom);
                ArrayList<String> textlines = new ArrayList<String>();
                String str_tmp = "";
                for (int j = 0; j < textobj.text.length(); j++) {
                    str_tmp += textobj.text.charAt(j);
                    if (paint.measureText(str_tmp) < text_panel_witdh) {
                        continue;
                    }
                    if ((textlines.size() + 1) * text_height > text_panel_height) {
                        str_tmp = str_tmp.substring(0, str_tmp.length() - 3) + "...";
                        textlines.add(str_tmp);
                        str_tmp = "";
                        break;
                    }
                    textlines.add(str_tmp);
                    str_tmp = "";
                }
                if (!StringHelper.isEmpty(str_tmp)) {
                    textlines.add(str_tmp);
                }
                float textlines_height = text_height * textlines.size();
                float line_y = textobj.top + ((text_panel_height - textlines_height) / 2) + textlines_height;
                for (int l = textlines.size() - 1; l > -1; l--) {
                    float line_text_width = paint.measureText(textlines.get(l));
                    canvas.drawText(textlines.get(l), textobj.left + (text_panel_witdh - line_text_width) / 2, line_y, paint);
                    line_y -= text_height;
                }
            } else {
                float draw_text_left = textobj.left;
                if (textobj.textAlign.equals(WaterTextAlign.CENTER)) {
                    draw_text_left = textobj.left + ((textobj.right - textobj.left) - text_width) / 2;
                } else if (textobj.textAlign.equals(WaterTextAlign.RIGHT)) {
                    draw_text_left = textobj.right - text_width;
                }
                canvas.drawText(textobj.text, draw_text_left, textobj.bottom, paint);
            }
//            paint.reset();
//            paint.setAntiAlias(true);
//            paint.setColor(Color.RED);
//            paint.setStyle(Paint.Style.STROKE);
//            paint.setStrokeWidth(2);
//            canvas.drawRect(rectFList.get(i), paint);
        }
        canvas.restore();
        setWaterMarkBitmap(new_bit);
        invalidate();
    }


    /**
     *
     */
    public interface TextOnClickListener extends OnRemoveWaterListener {

        /**
         * @param iconInfo  view id
         * @param textIndex textlist index
         */
        void onclicked(WaterMarkIconInfo iconInfo, int textIndex);
    }

    /**
     * return text collection
     *
     * @return
     */
    public ArrayList<WaterText> getTextList() {
        return textList;
    }


    public class WaterTextAlign {
        public final static String LEFT = "LEFT";
        public final static String RIGHT = "RIGHT";
        public final static String CENTER = "CENTER";
    }

    /**
     * 代表水印点击后的事件类型，选择日期或者城市
     */
    public class WaterTextEventType {
        //不响应事件
        public final static String TYPE_SELECT_NONE = "NONE";
        //选择日期
        public final static String TYPE_SELECT_DATE = "SELECT_DATE";
        //选择家所在城市
        public final static String TYPE_SELECT_HOME_CITY = "SELECT_HOME_CITY";
        //选择当前所在城市
        public final static String TYPE_SELECT_CURRENT_CITY = "SELECT_CURRENT_CITY";
        //选择节日名称
        public final static String TYPE_SELECT_FESTIVAL_NAME = "SELECT_FESTIVAL_NAME";
        //选择节日日期
        public final static String TYPE_SELECT_FESTIVAL_DATE = "SELECT_FESTIVAL_DATE";
        //编辑文字
        public final static String TYPE_EDIT_TEXT = "EDIT_TEXT";
    }
}
