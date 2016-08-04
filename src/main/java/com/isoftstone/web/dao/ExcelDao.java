package com.isoftstone.web.dao;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ExcelDao {
	/***
	 * 将date类型的日期转化为string类型
	 * 
	 * @param date
	 *            传入的Date类型的日期
	 * @return 返回的String类型的日期
	 */
	public String dateToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String str = sdf.format(date);
		return str;
	}

	/***
	 * 将string类型的日期转化为date类型
	 * 
	 * @param str
	 *            传入的String类型的日期
	 * @return 返回的Date类型的日期
	 */
	public Date StringToDate(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 小写的mm表示的是分钟
		Date date = new Date();
		try {
			date = sdf.parse(str);
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return date;
	}

	/***
	 * 获取给定日期所在周的第一天(Sunday)的日期和最后一天(Saturday)的日期
	 * 
	 * @param calendar
	 * 			传入的给定日期
	 * @return Date数组，[0]为第一天的日期，[1]最后一天的日期
	 */
	public String[] getWeekStartAndEndDate(String calenstr) {
		Calendar calendar = DateToCal(StringToDate(calenstr));
		String[] dates = new String[2];
		Date firstDateOfWeek, lastDateOfWeek;
		// 得到当天是这周的第几天
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		// 减去dayOfWeek,得到第一天的日期，因为Calendar用０－６代表一周七天，所以要减一
		calendar.add(Calendar.DAY_OF_WEEK, -(dayOfWeek - 1));
		firstDateOfWeek = calendar.getTime();
		// 每周7天，加６，得到最后一天的日子
		calendar.add(Calendar.DAY_OF_WEEK, 6);
		lastDateOfWeek = calendar.getTime();

		dates[0] = dateToString(firstDateOfWeek);
		dates[1] = dateToString(lastDateOfWeek);
		return dates;
	}

	/**
	 * 获取给定时间所在月的第一天的日期和最后一天的日期
	 * 
	 * @param calendar
	 * 			传入的给定日期
	 * @return Date数组，[0]为第一天的日期，[1]最后一天的日期
	 */
	public String[] getMonthStartAndEndDate(String calenstr) {
		Calendar calendar = DateToCal(StringToDate(calenstr));
		String[] dates = new String[2];
		Date firstDateOfMonth, lastDateOfMonth;
		// 得到当天是这月的第几天
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		// 减去dayOfMonth,得到第一天的日期，因为Calendar用０代表每月的第一天，所以要减一
		calendar.add(Calendar.DAY_OF_MONTH, -(dayOfMonth - 1));
		firstDateOfMonth = calendar.getTime();
		// calendar.getActualMaximum(Calendar.DAY_OF_MONTH)得到这个月有几天
		calendar.add(Calendar.DAY_OF_MONTH,
				calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - 1);
		lastDateOfMonth = calendar.getTime();

		dates[0] = dateToString(firstDateOfMonth);
		dates[1] = dateToString(lastDateOfMonth);
		return dates;
	}

	/***
	 * 将日历类型的日期转换为date类型
	 * 
	 * @param calendar
	 * 		传入的日历类型的日期
	 * @return 返回的date类型的日期
	 */
	public Date CalToDate(Calendar calendar) {
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		return date;
	}

	/***
	 * 将date类型的日期转换为日历类型
	 * 
	 * @param date
	 * 		传入的date类型的日期
	 * @return 返回的日历类型的日期
	 */
	public Calendar DateToCal(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}
}
