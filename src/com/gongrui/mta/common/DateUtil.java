package com.gongrui.mta.common;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	/**
	 * 默认日期格式
	 */
	private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";// Timestamp
																			// format
																			// must
																			// be
																			// yyyy-mm-dd
																			// hh:mm:ss[.fffffffff]
	/**
	 * 默认构�?函数
	 */
	private DateUtil() {
	}

	public static String longtime2str(long time, String format) {
		Date date = new Date(time);
		if (format == null) {
			format = DEFAULT_FORMAT;
		}
		return date2Str(date, format);
	}

	public static long str2longtime(String time, String srcformat) throws ParseException {

		SimpleDateFormat df1 = new SimpleDateFormat(srcformat);
		java.util.Date date1 = df1.parse(time);
		SimpleDateFormat df2 = new SimpleDateFormat(DEFAULT_FORMAT);

		String time2 = df2.format(date1);
		Date date = str2Date(time2, DEFAULT_FORMAT);
		return date.getTime();
	}

	/**
	 * 字符串转换成日期 如果转换格式为空，则利用默认格式进行转换操作
	 * 
	 * @param str
	 *            字符�?
	 * @param format
	 *            日期格式
	 * @return 日期
	 * @throws java.text.ParseException
	 */
	public static Date str2Date(String str, String format) {
		if (null == str || "".equals(str)) {
			return null;
		}
		// 如果没有指定字符串转换的格式，则用默认格式进行转�?
		if (null == format || "".equals(format)) {
			format = DEFAULT_FORMAT;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = sdf.parse(str);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 日期转换为字符串
	 * 
	 * @param date
	 *            日期
	 * @param format
	 *            日期格式
	 * @return 字符�?
	 */
	public static String date2Str(Date date, String format) {
		if (null == date) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/**
	 * 时间戳转换为字符�?
	 * 
	 * @param time
	 * @return
	 */
	public static String timestamp2Str(Timestamp time) {
		Date date = null;
		if (null != time) {
			date = new Date(time.getTime());
		}
		return date2Str(date, DEFAULT_FORMAT);
	}

	/**
	 * 字符串转换时间戳
	 * 
	 * @param str
	 * @return
	 */
	public static Timestamp str2Timestamp(String str) {
		Date date = str2Date(str, DEFAULT_FORMAT);
		return new Timestamp(date.getTime());
	}

	public static void main(String[] args) throws Exception {

		String tm = "2011-01-01 10:00";
		Timestamp tstamp = str2Timestamp(tm);
		System.out.println(tstamp);
	}
}