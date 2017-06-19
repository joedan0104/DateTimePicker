package com.joe.datetimepicker;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

/**
 * 时间选择器
 *
 * Created by Joe on 2017/6/16.
 */
public class JDTimePicker extends LinearLayout {
    /**
     * The callback interface used to indicate the time has been adjusted.
     */
    public interface OnTimeChangedListener {

        /**
         * @param view The view associated with this listener.
         * @param hourOfDay The current hour.
         * @param minute The current minute.
         * @param second The current second
         */
        void onTimeChanged(JDTimePicker view, int hourOfDay, int minute, int second);
    }

    /**
     * Used to save / restore state of time picker
     */
    private static class SavedState extends BaseSavedState {

        private final int mHour;

        private final int mMinute;
        private final int mSecond;

        private SavedState(Parcelable superState, int hour, int minute, int second) {
            super(superState);
            mHour = hour;
            mMinute = minute;
            mSecond = second;
        }

        private SavedState(Parcel in) {
            super(in);
            mHour = in.readInt();
            mMinute = in.readInt();
            mSecond = in.readInt();
        }

        public int getHour() {
            return mHour;
        }

        public int getMinute() {
            return mMinute;
        }

        public int getSecond() {
            return mSecond;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mHour);
            dest.writeInt(mMinute);
            dest.writeInt(mSecond);
        }

        @SuppressWarnings({"unused", "hiding"})
        public static final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    private static final int HOURS_IN_HALF_DAY = 12;

    private Context mContext;
    /**
     * 小时选择
     */
    private JDNumberPicker mHourSpinner;
    /**
     * 分钟选择
     */
    private JDNumberPicker mMinuteSpinner;
    /**
     * 秒钟选择
     */
    private JDNumberPicker mSecondSpinner;
    /**
     * 时钟和分钟分割线
     */
    private TextView mDivider;
    /**
     * 分钟和秒钟间分隔符
     */
    private TextView mMinuteSplit;
    /**
     * 临时用日历
     */
    private Calendar mTempCalendar;

    // callbacks
    private OnTimeChangedListener mOnTimeChangedListener;
    /**
     * 当前时区
     */
    private Locale mCurrentLocale;
    private char mHourFormat = 'K';
    private boolean mHourWithTwoDigit;

    /**
     * 是否显示秒钟
     */
    private boolean isShowSecondView = true;
    /**
     * 显示24小时时钟(默认显示24小时时钟)
     */
    private boolean mIs24HourView = true;
    private boolean mIsAm;

    /**
     * 构造函数
     *
     * @param context
     */
    public JDTimePicker(Context context) {
        this(context, null);
    }

    public JDTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        //视图初始化
        initView(context, attrs);
    }

    /**
     * 控件初始化
     *
     * @param context
     * @param attrs
     */
    private void initView(Context context, AttributeSet attrs) {
        // initialization based on locale
        setCurrentLocale(Locale.getDefault());

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_time_picker, this,true);
        mHourSpinner = (JDNumberPicker) findViewById(R.id.hourPicker);
        mMinuteSpinner = (JDNumberPicker) findViewById(R.id.minutePicker);
        mSecondSpinner = (JDNumberPicker) findViewById(R.id.secondPicker);
        //不显示输入法
        mHourSpinner.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mMinuteSpinner.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mSecondSpinner.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mDivider = (TextView) findViewById(R.id.hour_split);
        mMinuteSplit = (TextView) findViewById(R.id.minute_split);
        //设置秒钟时钟显隐
        setShowSecondView(isShowSecondView);

        mHourSpinner.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker spinner, int oldVal, int newVal) {
                if (!is24HourView()) {
                    if ((oldVal == HOURS_IN_HALF_DAY - 1 && newVal == HOURS_IN_HALF_DAY)
                            || (oldVal == HOURS_IN_HALF_DAY && newVal == HOURS_IN_HALF_DAY - 1)) {
                        mIsAm = !mIsAm;
                    }
                }
                onTimeChanged();
            }
        });

        // minute
        mMinuteSpinner.setMinValue(0);
        mMinuteSpinner.setMaxValue(59);
        mMinuteSpinner.setOnLongPressUpdateInterval(100);
        mMinuteSpinner.setFormatter(JDNumberPicker.getTwoDigitFormatter(mMinuteSpinner));
        mMinuteSpinner.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker spinner, int oldVal, int newVal) {
                int minValue = mMinuteSpinner.getMinValue();
                int maxValue = mMinuteSpinner.getMaxValue();
                if (oldVal == maxValue && newVal == minValue) {
                    int newHour = mHourSpinner.getValue() + 1;
                    if (!is24HourView() && newHour == HOURS_IN_HALF_DAY) {
                        mIsAm = !mIsAm;
                    }
                    mHourSpinner.setValue(newHour);
                } else if (oldVal == minValue && newVal == maxValue) {
                    int newHour = mHourSpinner.getValue() - 1;
                    if (!is24HourView() && newHour == HOURS_IN_HALF_DAY - 1) {
                        mIsAm = !mIsAm;
                    }
                    mHourSpinner.setValue(newHour);
                }
                onTimeChanged();
            }
        });

        // second
        mSecondSpinner.setMinValue(0);
        mSecondSpinner.setMaxValue(59);
        mSecondSpinner.setOnLongPressUpdateInterval(100);
        mSecondSpinner.setFormatter(JDNumberPicker.getTwoDigitFormatter(mSecondSpinner));
        mSecondSpinner.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker spinner, int oldVal, int newVal) {
                int minValue = mSecondSpinner.getMinValue();
                int maxValue = mSecondSpinner.getMaxValue();
                if (oldVal == maxValue && newVal == minValue) {
                    int newHour = mMinuteSpinner.getValue() + 1;
                    mMinuteSpinner.setValue(newHour);
                } else if (oldVal == minValue && newVal == maxValue) {
                    int newHour = mMinuteSpinner.getValue() - 1;
                    mMinuteSpinner.setValue(newHour);
                }
                onTimeChanged();
            }
        });

        // update controls to initial state
        updateHourControl();
        updateMinuteControl();

        // set to current time
        setCurrentHour(mTempCalendar.get(Calendar.HOUR_OF_DAY));
        setCurrentMinute(mTempCalendar.get(Calendar.MINUTE));
        setCurrentSecond(mTempCalendar.get(Calendar.SECOND));
    }

    /**
     * 设置是否显示秒钟计时
     */
    public void setShowSecondView(boolean showSecondView) {
        isShowSecondView = showSecondView;
        if(isShowSecondView) {
            mMinuteSplit.setVisibility(View.VISIBLE);
            mSecondSpinner.setVisibility(View.VISIBLE);
        } else {
            mMinuteSplit.setVisibility(View.GONE);
            mSecondSpinner.setVisibility(View.GONE);
        }
    }

    /**
     * @return true if this is in 24 hour view else false.
     */
    public boolean is24HourView() {
        return mIs24HourView;
    }

    /**
     * Set the callback that indicates the time has been adjusted by the user.
     *
     * @param onTimeChangedListener the callback, should not be null.
     */
    public void setOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener) {
        mOnTimeChangedListener = onTimeChangedListener;
    }

    /**
     * 设置选中项Text颜色
     *
     * @param color : 颜色值
     */
    public void setSelectionTextColor(int color) {
        mHourSpinner.setSelectionTextColor(color);
        mMinuteSpinner.setSelectionTextColor(color);
        mSecondSpinner.setSelectionTextColor(color);
    }

    /**
     * 设置选中项分割线高度
     *
     * @param height : 选中项分割线高度
     */
    public void setDividerHeight(int height) {
        mHourSpinner.setDividerHeight(height);
        mMinuteSpinner.setDividerHeight(height);
        mSecondSpinner.setDividerHeight(height);
    }

    /**
     * 设置分割线颜色
     *
     * @param color : 颜色值
     */
    public void setDividerColor(int color) {
        mHourSpinner.setDividerColor(color);
        mMinuteSpinner.setDividerColor(color);
        mSecondSpinner.setDividerColor(color);
    }

    /**
     * 设置选项文字大小
     *
     * @param textSize : 文字大小
     */
    public void setSelectionTextSize(int textSize) {
        mHourSpinner.setSelectionTextSize(textSize);
        mMinuteSpinner.setSelectionTextSize(textSize);
        mSecondSpinner.setSelectionTextSize(textSize);
    }

    /**
     * 设置选中项分割线之间距离
     *
     * @param distance : 选中项分割线之间距离
     */
    public void setDividersDistance(int distance) {
        mHourSpinner.setDividersDistance(distance);
        mMinuteSpinner.setDividersDistance(distance);
        mSecondSpinner.setDividersDistance(distance);
    }

    /**
     * @return The current second. (0~59)
     */
    public Integer getCurrentSecond() {
        return mSecondSpinner.getValue();
    }

    /**
     * Set the current minute (0-59).
     */
    public void setCurrentSecond(Integer currentSecond) {
        if (currentSecond == getCurrentSecond()) {
            return;
        }
        mSecondSpinner.setValue(currentSecond);
        onTimeChanged();
    }

    /**
     * @return The current minute.(0~59)
     */
    public Integer getCurrentMinute() {
        return mMinuteSpinner.getValue();
    }

    /**
     * Set the current minute (0-59).
     */
    public void setCurrentMinute(Integer currentMinute) {
        if (currentMinute == getCurrentMinute()) {
            return;
        }
        mMinuteSpinner.setValue(currentMinute);
        onTimeChanged();
    }

    /**
     * @return The current hour in the range (0-23).
     */
    public Integer getCurrentHour() {
        int currentHour = mHourSpinner.getValue();
        if (is24HourView()) {
            return currentHour;
        } else if (mIsAm) {
            return currentHour % HOURS_IN_HALF_DAY;
        } else {
            return (currentHour % HOURS_IN_HALF_DAY) + HOURS_IN_HALF_DAY;
        }
    }

    /**
     * Set the current hour.
     */
    public void setCurrentHour(Integer currentHour) {
        setCurrentHour(currentHour, true);
    }

    private void setCurrentHour(Integer currentHour, boolean notifyTimeChanged) {
        // why was Integer used in the first place?
        if (currentHour == null || currentHour == getCurrentHour()) {
            return;
        }
        if (!is24HourView()) {
            // convert [0,23] ordinal to wall clock display
            if (currentHour >= HOURS_IN_HALF_DAY) {
                mIsAm = false;
                if (currentHour > HOURS_IN_HALF_DAY) {
                    currentHour = currentHour - HOURS_IN_HALF_DAY;
                }
            } else {
                mIsAm = true;
                if (currentHour == 0) {
                    currentHour = HOURS_IN_HALF_DAY;
                }
            }
        }
        mHourSpinner.setValue(currentHour);
        if (notifyTimeChanged) {
            onTimeChanged();
        }
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        onPopulateAccessibilityEvent(event);
        return true;
    }

    @Override
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        super.onPopulateAccessibilityEvent(event);

        int flags = DateUtils.FORMAT_SHOW_TIME;
        if (mIs24HourView) {
            flags |= DateUtils.FORMAT_24HOUR;
        } else {
            flags |= DateUtils.FORMAT_12HOUR;
        }
        mTempCalendar.set(Calendar.HOUR_OF_DAY, getCurrentHour());
        mTempCalendar.set(Calendar.MINUTE, getCurrentMinute());
        String selectedDateUtterance = DateUtils.formatDateTime(mContext,
                mTempCalendar.getTimeInMillis(), flags);
        event.getText().add(selectedDateUtterance);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(JDTimePicker.class.getName());
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setCurrentLocale(newConfig.locale);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, getCurrentHour(), getCurrentMinute(), getCurrentSecond());
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setCurrentHour(ss.getHour());
        setCurrentMinute(ss.getMinute());
        setCurrentSecond(ss.getSecond());
    }

    /**
     * Sets the current locale.
     *
     * @param locale The current locale.
     */
    private void setCurrentLocale(Locale locale) {
        if (locale.equals(mCurrentLocale)) {
            return;
        }
        mCurrentLocale = locale;
        mTempCalendar = Calendar.getInstance(locale);
    }

    private void onTimeChanged() {
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
        if (mOnTimeChangedListener != null) {
            mOnTimeChangedListener.onTimeChanged(this, getCurrentHour(), getCurrentMinute(), getCurrentSecond());
        }
    }

    private void getHourFormatData() {
        Locale defaultLocale = Locale.getDefault();
        String bestDateTimePattern = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bestDateTimePattern = DateFormat.getBestDateTimePattern(defaultLocale,
                    (mIs24HourView) ? "Hm" : "hm");
        }
        int lengthPattern = bestDateTimePattern.length();
        mHourWithTwoDigit = false;
        char hourFormat = '\0';
        // Check if the returned pattern is single or double 'H', 'h', 'K', 'k'. We also save
        // the hour format that we found.
        for (int i = 0; i < lengthPattern; i++) {
            final char c = bestDateTimePattern.charAt(i);
            if (c == 'H' || c == 'h' || c == 'K' || c == 'k') {
                mHourFormat = c;
                if (i + 1 < lengthPattern && c == bestDateTimePattern.charAt(i + 1)) {
                    mHourWithTwoDigit = true;
                }
                break;
            }
        }
    }

    private void updateHourControl() {
        if (is24HourView()) {
            // 'k' means 1-24 hour
            if (mHourFormat == 'k') {
                mHourSpinner.setMinValue(1);
                mHourSpinner.setMaxValue(24);
            } else {
                mHourSpinner.setMinValue(0);
                mHourSpinner.setMaxValue(23);
            }
        } else {
            // 'K' means 0-11 hour
            if (mHourFormat == 'K') {
                mHourSpinner.setMinValue(0);
                mHourSpinner.setMaxValue(11);
            } else {
                mHourSpinner.setMinValue(1);
                mHourSpinner.setMaxValue(12);
            }
        }
        mHourSpinner.setFormatter(mHourWithTwoDigit ? JDNumberPicker.getTwoDigitFormatter(mHourSpinner) : null);
    }

    private void updateMinuteControl() {
//        if (is24HourView()) {
//            mMinuteSpinnerInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
//        } else {
//            mMinuteSpinnerInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);
//        }
    }
}
