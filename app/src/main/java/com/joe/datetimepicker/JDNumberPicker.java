package com.joe.datetimepicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.joe.datetimepicker.R;

import java.lang.reflect.Field;

/**
 * 自定义NumberPicker
 *
 * Created By Joe 2017/6/15
 */
public class JDNumberPicker extends NumberPicker {
    /**
     * The default unscaled height of the selection divider.
     */
    private static final int UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT = 2;

    /**
     * The default unscaled distance between the selection dividers.
     */
    private static final int UNSCALED_DEFAULT_SELECTION_DIVIDERS_DISTANCE = 48;
    /**
     * The default unscaled of the selection textSize
     */
    private static final int UNSCALED_DEFAULT_SELECTION_TEXTSIZE = 30;
    /**
     * The height of the selection divider.
     */
    private int mSelectionDividerHeight = UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT;
    /**
     * The distance between the two selection dividers.
     */
    private int mSelectionDividersDistance = UNSCALED_DEFAULT_SELECTION_DIVIDERS_DISTANCE;

    /**
     *
     * @param context
     */
    private int mSelectionDividersColor = Color.GRAY;
    /**
     *
     * @param context
     */
    private int mSelectionTextColor = Color.WHITE;

    /**
     * 文字大小
     */
    private int mSelectionTextSize = UNSCALED_DEFAULT_SELECTION_TEXTSIZE;

    public JDNumberPicker(Context context) {
        super(context, null);
    }

    public JDNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        //视图初始化
        initView(context, attrs);
    }

    public JDNumberPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //视图初始化
        initView(context, attrs);
    }

    /**
     * 视图初始化
     */
    private void initView(Context context, AttributeSet attrs) {
        if(null != attrs) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.JDNumberPicker, 0, 0);
            try{
                if (a.hasValue(R.styleable.JDNumberPicker_jdSelectionDividerHeight)) {
                    mSelectionDividerHeight = a.getDimensionPixelSize(R.styleable.JDNumberPicker_jdSelectionDividerHeight, UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT);
                    setDividerHeight(mSelectionDividerHeight);
                }
                if (a.hasValue(R.styleable.JDNumberPicker_jdSelectionDividersDistance)) {
                    mSelectionDividersDistance = a.getDimensionPixelSize(R.styleable.JDNumberPicker_jdSelectionDividersDistance, UNSCALED_DEFAULT_SELECTION_DIVIDERS_DISTANCE);
                    setDividersDistance(mSelectionDividersDistance);
                }
                if (a.hasValue(R.styleable.JDNumberPicker_jdSelectDividersColor)) {
                    mSelectionDividersColor = a.getColor(R.styleable.JDNumberPicker_jdSelectDividersColor, Color.GRAY);
                    setDividerColor(mSelectionDividersColor);
                }
                if (a.hasValue(R.styleable.JDNumberPicker_jdSelectTextColor)) {
                    mSelectionTextColor = a.getColor(R.styleable.JDNumberPicker_jdSelectTextColor, Color.WHITE);
                    setSelectionTextColor(mSelectionTextColor);
                }
                if (a.hasValue(R.styleable.JDNumberPicker_jdSelectTextSize)) {
                    mSelectionTextSize = a.getDimensionPixelSize(R.styleable.JDNumberPicker_jdSelectTextSize, UNSCALED_DEFAULT_SELECTION_TEXTSIZE);
                    setSelectionTextSize(mSelectionTextSize);
                }
            } finally {
                a.recycle();
            }
        }
    }

    /**
     * 设置选中项分割线之间距离
     *
     * @param distance : 选中项分割线之间距离
     */
    public void setDividersDistance(int distance) {
        int dividerD = Math.max(distance, UNSCALED_DEFAULT_SELECTION_DIVIDERS_DISTANCE);
        try {
            Field pf = NumberPicker.class.getDeclaredField("mSelectionDividersDistance");
            if(null != pf) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值
                    pf.set(this, dividerD);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置选中项Text颜色
     *
     * @param color : 颜色值
     */
    public void setSelectionTextColor(int color) {
        try {
            //初始的Text字体颜色
            Field fInputText = NumberPicker.class.getDeclaredField("mInputText");
            if(null != fInputText) {
                fInputText.setAccessible(true);
                Object value = fInputText.get(this);
                if(value instanceof EditText) {
                    //设置字体颜色
                    ((EditText) value).setTextColor(color);
                }
            }

            //滚动后的字体颜色
            Field fPaint = NumberPicker.class.getDeclaredField("mSelectorWheelPaint");
            if(null != fPaint) {
                fPaint.setAccessible(true);
                Object value = fPaint.get(this);
                if(value instanceof Paint) {
                    //设置字体颜色
                    ((Paint) value).setColor(color);
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置选中项分割线高度
     *
     * @param height : 选中项分割线高度
     */
    public void setDividerHeight(int height) {
        int dividerH = Math.max(height, UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT);
        try {
            Field pf = NumberPicker.class.getDeclaredField("mSelectionDividerHeight");
            if(null != pf) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值
                    pf.set(this, dividerH);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置分割线颜色
     *
     * @param color : 颜色值
     */
    public void setDividerColor(int color) {
        try {
            Field pf = NumberPicker.class.getDeclaredField("mSelectionDivider");
            if(null != pf) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值
                    pf.set(this, new ColorDrawable(color));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置选项文字大小
     *
     * @param textSize : 文字大小
     */
    public void setSelectionTextSize(int textSize) {
        textSize = Math.max(textSize, UNSCALED_DEFAULT_SELECTION_TEXTSIZE);
        try {
            try {
                Field pf = NumberPicker.class.getDeclaredField("mTextSize");
                if(null != pf) {
                    pf.setAccessible(true);
                    try {
                        //设置分割线的颜色值
                        pf.set(this, textSize);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }

            //初始的Text字体颜色
            Field fInputText = NumberPicker.class.getDeclaredField("mInputText");
            if(null != fInputText) {
                fInputText.setAccessible(true);
                Object value = fInputText.get(this);
                if(value instanceof EditText) {
                    //设置字体颜色
                    ((EditText) value).setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                    ((EditText) value).getPaint().setTextSize(textSize);
                }
            }

            //滚动后的字体颜色
            Field fPaint = NumberPicker.class.getDeclaredField("mSelectorWheelPaint");
            if(null != fPaint) {
                fPaint.setAccessible(true);
                Object value = fPaint.get(this);
                if(value instanceof Paint) {
                    //设置字体颜色
                    ((Paint) value).setTextSize(textSize);
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * @hide
     */
    public static Formatter getTwoDigitFormatter(NumberPicker numberPicker) {
        Formatter formatter = null;
        try {
            Field pf = NumberPicker.class.getDeclaredField("sTwoDigitFormatter");
            if(null != pf) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值
                    Object obj = pf.get(numberPicker);
                    if(null != obj && obj instanceof Formatter) {
                        formatter = (Formatter) obj;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return formatter;
    }
}
