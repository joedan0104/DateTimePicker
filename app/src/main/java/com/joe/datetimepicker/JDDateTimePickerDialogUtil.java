package com.joe.datetimepicker;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 日期时间选择控件 使用方法： private EditText inputDate;//需要设置的日期时间文本编辑框 private String
 * initDateTime="2012年9月3日 14:44",//初始日期时间值 在点击事件中使用：
 * inputDate.setOnClickListener(new OnClickListener() {
 * 
 */
public class JDDateTimePickerDialogUtil implements JDDatePicker.OnDateChangedListener,
		JDTimePicker.OnTimeChangedListener {
	/**
	 * The callback interface used to indicate the time has been adjusted.
	 */
	public interface OnDateTimeChangedListener {

		/**
		 * @param datePicker The date view associated with this listener.
		 * @param timePicker The time view associated with this listener.
		 * @param calendar The current selection calendar.
		 */
		void onDateTimeChanged(JDDatePicker datePicker, JDTimePicker timePicker,
							   Calendar calendar);
	}



	private JDDatePicker datePicker;
	private JDTimePicker timePicker;
	private TextView selectionDateTime;
	private AlertDialog ad;
	private Calendar currentCalendar;
	private String dateTime;
	private String initDateTime;
	private Activity activity;
	private OnDateTimeChangedListener onDateTimeChangedListener;

	/**
	 * 日期时间弹出选择框构造函数
	 *
	 * @param activity
	 *            ：调用的父activity
	 * @param calendar
	 *            初始日期时间值，作为弹出窗口的标题和日期时间初始值
	 */
	public JDDateTimePickerDialogUtil(Activity activity, Calendar calendar) {
		this.activity = activity;
		this.currentCalendar = calendar;
		if(null == calendar) {
			this.currentCalendar = Calendar.getInstance();
		}
	}

	private void init(JDDatePicker datePicker, JDTimePicker timePicker) {
		initDateTime = currentCalendar.get(Calendar.YEAR) + "年"
				+ currentCalendar.get(Calendar.MONTH) + "月"
				+ currentCalendar.get(Calendar.DAY_OF_MONTH) + "日 "
				+ currentCalendar.get(Calendar.HOUR_OF_DAY) + ":"
				+ currentCalendar.get(Calendar.MINUTE) + ":"
				+ currentCalendar.get(Calendar.SECOND);

		datePicker.init(currentCalendar.get(Calendar.YEAR),
				currentCalendar.get(Calendar.MONTH),
				currentCalendar.get(Calendar.DAY_OF_MONTH), this);
		timePicker.setCurrentHour(currentCalendar.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(currentCalendar.get(Calendar.MINUTE));
		timePicker.setCurrentSecond(currentCalendar.get(Calendar.SECOND));
		int dividerColor = activity.getResources().getColor(R.color.numberpicker_line);
		int textColor = activity.getResources().getColor(R.color.numberpicker_text_line);
		datePicker.setDividerColor(dividerColor);
		datePicker.setSelectionTextColor(textColor);
		datePicker.setSelectionTextSize(50);
		timePicker.setDividerColor(dividerColor);
		timePicker.setSelectionTextColor(textColor);
		timePicker.setSelectionTextSize(50);
	}

	/**
	 * 设置日期时间选择监听
	 *
	 * @param onDateTimeChangedListener : 日期时间选择结果监听器
	 */
	public void setOnDateTimeChangedListener(OnDateTimeChangedListener onDateTimeChangedListener) {
		this.onDateTimeChangedListener = onDateTimeChangedListener;
	}

	/**
	 * 弹出日期时间选择框方法
	 * 
	 * @return
	 */
	public AlertDialog show() {
		LinearLayout dateTimeLayout = (LinearLayout) activity
				.getLayoutInflater().inflate(R.layout.layout_date_time_picker, null);
		datePicker = (JDDatePicker) dateTimeLayout.findViewById(R.id.date_picker);
		timePicker = (JDTimePicker) dateTimeLayout.findViewById(R.id.time_picker);
		selectionDateTime = (TextView) dateTimeLayout.findViewById(R.id.date_time_text);
		selectionDateTime.setText(initDateTime);
		init(datePicker, timePicker);
		datePicker.setOnDateChangedListener(this);
		timePicker.setOnTimeChangedListener(this);
		dateTimeLayout.findViewById(R.id.date_time_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ad.dismiss();
			}
		});
		dateTimeLayout.findViewById(R.id.date_time_ok).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null != onDateTimeChangedListener) {
					//选择完成发送回调通知
					onDateTimeChangedListener.onDateTimeChanged(datePicker, timePicker,
							currentCalendar);
				}
				ad.dismiss();
			}
		});

		ad = new AlertDialog.Builder(activity)
				.setView(dateTimeLayout)
				.show();

		onDateChanged(null, 0, 0, 0);
		return ad;
	}


	@Override
	public void onTimeChanged(JDTimePicker view, int hourOfDay, int minute, int second) {
		onDateChanged(null, 0, 0, 0);
	}

	@Override
	public void onDateChanged(JDDatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		currentCalendar.set(datePicker.getYear(), datePicker.getMonth(),
				datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
				timePicker.getCurrentMinute(), timePicker.getCurrentSecond());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

		dateTime = sdf.format(currentCalendar.getTime());
		selectionDateTime.setText(dateTime);
	}

	/**
	 * 实现将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒,并赋值给calendar
	 * 
	 * @param initDateTime
	 *            初始日期时间值 字符串型
	 * @return Calendar
	 */
	private Calendar getCalendarByInintData(String initDateTime) {
		Calendar calendar = Calendar.getInstance();

		// 将初始日期时间2012年07月02日 16:45:30 拆分成年 月 日 时 分 秒
		String date = spliteString(initDateTime, "日", "index", "front"); // 日期
		String time = spliteString(initDateTime, "日", "index", "back"); // 时间

		String yearStr = spliteString(date, "年", "index", "front"); // 年份
		String monthAndDay = spliteString(date, "年", "index", "back"); // 月日

		String monthStr = spliteString(monthAndDay, "月", "index", "front"); // 月
		String dayStr = spliteString(monthAndDay, "月", "index", "back"); // 日

		String hourStr = spliteString(time, ":", "index", "front"); // 时
		String msStr = time.substring(time.indexOf(":") + 1);
		String minuteStr = spliteString(msStr, ":", "index", "front"); // 分
		String secondStr = spliteString(msStr, ":", "index", "back"); // 分

		int currentYear = Integer.valueOf(yearStr.trim()).intValue();
		int currentMonth = Integer.valueOf(monthStr.trim()).intValue() - 1;
		int currentDay = Integer.valueOf(dayStr.trim()).intValue();
		int currentHour = Integer.valueOf(hourStr.trim()).intValue();
		int currentMinute = Integer.valueOf(minuteStr.trim()).intValue();
		int currentSecond = Integer.valueOf(secondStr.trim()).intValue();

		calendar.set(currentYear, currentMonth, currentDay, currentHour,
				currentMinute, currentSecond);
		return calendar;
	}

	/**
	 * 截取子串
	 * 
	 * @param srcStr
	 *            源串
	 * @param pattern
	 *            匹配模式
	 * @param indexOrLast
	 * @param frontOrBack
	 * @return
	 */
	public static String spliteString(String srcStr, String pattern,
			String indexOrLast, String frontOrBack) {
		String result = "";
		int loc = -1;
		if (indexOrLast.equalsIgnoreCase("index")) {
			loc = srcStr.indexOf(pattern); // 取得字符串第一次出现的位置
		} else {
			loc = srcStr.lastIndexOf(pattern); // 最后一个匹配串的位置
		}
		if (frontOrBack.equalsIgnoreCase("front")) {
			if (loc != -1)
				result = srcStr.substring(0, loc); // 截取子串
		} else {
			if (loc != -1)
				result = srcStr.substring(loc + 1, srcStr.length()); // 截取子串
		}
		return result;
	}

}
