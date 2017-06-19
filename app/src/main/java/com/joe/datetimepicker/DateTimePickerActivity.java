package com.joe.datetimepicker;

import android.app.Activity;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.joe.datetimepicker.JDDateTimePickerDialogUtil.OnDateTimeChangedListener;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Calendar;

/**
 * 时间拾取器界面
 * 
 * @author wwj_748
 * 
 */
public class DateTimePickerActivity extends Activity implements JDDatePicker.OnDateChangedListener {
	/**
	 * Called when the activity is first created.
	 */
	private EditText startDateTime;
	private EditText endDateTime;
	private JDNumberPicker yearPicker;
	private JDDatePicker datePicker;
//    private OnDateTimeChangedListener onDateTimeChangedListener = new OnDateTimeChangedListener() {
//
//        @Override
//        public void onDateTimeChanged(JDDatePicker datePicker, JDTimePicker timePicker, int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
//            Log.d("DateTimePickerActivity", "onTimeChanged date: " + year + "-" + (month + 1) + "-" + dayOfMonth + " " + hourOfDay + ":" + minute + ":" + second);
//        }
//    };

	private static String DATE_TIME_FORMAT = "yyyy年MM月dd日 HH:mm:ss";
	private String initStartDateTime = "2013年9月3日 14:44:22"; // 初始化开始时间
	private String initEndDateTime = "2014年8月23日 17:44:22"; // 初始化结束时间

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 两个输入框
		startDateTime = (EditText) findViewById(R.id.inputDate);
		endDateTime = (EditText) findViewById(R.id.inputDate2);
		yearPicker = (JDNumberPicker) findViewById(R.id.yearPicker);
		yearPicker.setMinValue(1900);
		yearPicker.setMaxValue(2100);
		yearPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		yearPicker.setDividerColor(getResources().getColor(R.color.numberpicker_line));
		yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				Log.d("zrh", "onValueChange oldVal:[" + oldVal + "] newVal:[" + newVal + "]");
			}
		});

		Calendar calendar = Calendar.getInstance();
		datePicker = (JDDatePicker) findViewById(R.id.datepicker);
		datePicker.setOnDateChangedListener(this);

		startDateTime.setText(initStartDateTime);
		endDateTime.setText(initEndDateTime);

		startDateTime.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Calendar calendar = null;
				try {
					calendar = TimeUtil.getCalenderByDateString(startDateTime.getText().toString(), DATE_TIME_FORMAT);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if(null == calendar) {
					calendar = Calendar.getInstance();
				}
				JDDateTimePickerDialogUtil dateTimePicKDialog = new JDDateTimePickerDialogUtil(
						DateTimePickerActivity.this, calendar);
                dateTimePicKDialog.setOnDateTimeChangedListener(new OnDateTimeChangedListener() {

					@Override
					public void onDateTimeChanged(JDDatePicker datePicker, JDTimePicker timePicker, Calendar calendar) {
						startDateTime.setText(TimeUtil.format(DATE_TIME_FORMAT, calendar));
					}
				});
				dateTimePicKDialog.show();

			}
		});

		endDateTime.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Calendar calendar = null;
				try {
					calendar = TimeUtil.getCalenderByDateString(endDateTime.getText().toString(), DATE_TIME_FORMAT);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if(null == calendar) {
					calendar = Calendar.getInstance();
				}
                JDDateTimePickerDialogUtil dateTimePicKDialog = new JDDateTimePickerDialogUtil(
						DateTimePickerActivity.this, calendar);
                dateTimePicKDialog.setOnDateTimeChangedListener(new OnDateTimeChangedListener() {
					@Override
					public void onDateTimeChanged(JDDatePicker datePicker, JDTimePicker timePicker, Calendar calendar) {
						endDateTime.setText(TimeUtil.format(DATE_TIME_FORMAT, calendar));
					}
				});
				dateTimePicKDialog.show();
			}
		});
	}

	/**
	 * 自定义滚动框分隔线颜色
	 */
	private void setNumberPickerDividerColor(NumberPicker number, int color) {
		try {
			Field pf = NumberPicker.class.getDeclaredField("mSelectionDivider");
			if(null != pf) {
				pf.setAccessible(true);
				try {
					//设置分割线的颜色值
					pf.set(number, new ColorDrawable(color));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	private void setDatePickerShowSpinner(DatePicker datePicker) {
		try {
			//初始的Text字体颜色
			Field fCalendarView = DatePicker.class.getDeclaredField("mCalendarView");
			if (null != fCalendarView) {
				fCalendarView.setAccessible(true);
				Object value = fCalendarView.get(datePicker);
				if (value instanceof CalendarView) {
					((CalendarView) value).setVisibility(View.GONE);
				}
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 自定义滚动框Text字体颜色
	 */
	private void setNumberPickerTextColor(NumberPicker number, int color) {
		try {
			//初始的Text字体颜色
			Field fInputText = NumberPicker.class.getDeclaredField("mInputText");
			if(null != fInputText) {
				fInputText.setAccessible(true);
				Object value = fInputText.get(number);
				if(value instanceof EditText) {
					//设置字体颜色
					((EditText) value).setTextColor(color);
				}
			}

			//滚动后的字体颜色
			Field fPaint = NumberPicker.class.getDeclaredField("mSelectorWheelPaint");
			if(null != fPaint) {
				fPaint.setAccessible(true);
				Object value = fPaint.get(number);
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

	@Override
	public void onDateChanged(JDDatePicker view, int year, int monthOfYear, int dayOfMonth) {
		Log.d("DateTimePickerActivity", "date: " + year + "-" + monthOfYear + "-" + dayOfMonth);
	}
}
