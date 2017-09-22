package com.legend.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public abstract class DateTimeUtils extends DateUtils
{
	private final static SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");

	private final static Calendar calendar = Calendar.getInstance();

	/**
	 * yyyy-MM-dd
	 */
	public final static String DATE_FORMAT = "yyyy-MM-dd";

	/**
	 * HH:mm:ss
	 */
	public final static String TIME_FORMAT = "HH:mm:ss";

	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * yyyy-MM-dd HH:mm:ss.S
	 */
	public final static String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.S";

	protected DateTimeUtils()
	{
	}

	public static String date2String(final Date date, final String dateFormat)
	{
		return DateTimeUtils.format(date, dateFormat);
	}

	public static Date string2Date(final String dateString, final String dateForm)
	{
		return DateTimeUtils.parse(dateString, dateForm, Date.class);
	}
	
	public static <T extends Date> T string2Date(final String dateString, final String dateFormat,
			final Class<T> targetResultType)
	{
		return DateTimeUtils.parse(dateString, dateFormat, targetResultType);
	}

	/**
	 * 日期加法.
	 * 
	 * @param date
	 *            初始日期
	 * @param amount
	 *            带单位的日期. 后缀d/m/y分别表示日/月/年，例如10m表示10个月
	 * @return
	 */
	public static Date add(Date date, String addend)
	{
		int amount = Integer.valueOf(addend.substring(0, addend.length() - 1)).intValue();
		char unit = addend.charAt(addend.length() - 1);
		switch (unit) {
		case 'y':
			return addYears(date, amount);
		case 'm':
			return addMonths(date, amount);
		case 'd':
			return addDays(date, amount);
		}
		throw new IllegalArgumentException("参数格式错误：" + addend);
	}

	/**
	 * 获得一个月的最后一天 时间格式：yyyyMM
	 * 
	 * @param month
	 * @return
	 * @throws ParseException
	 * @throws Exception
	 */
	public static String getLastDayByMonth(String month) throws ParseException
	{

		calendar.setTime(SDF.parse(getFirstDayOnMonth(month)));
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date lastDay = calendar.getTime();
		return SDF.format(lastDay);
	}

	/**
	 * 获得一个月的第一天 时间格式：yyyyMM
	 * 
	 * @param month
	 * @return
	 * @throws ParseException
	 * @throws Exception
	 */
	public static String getFirstDayByMonth(String month) throws ParseException
	{
		return getFirstDayOnMonth(month);
	}

	/**
	 * 获得该月有几天 时间格式：yyyyMM
	 * 
	 * @param month
	 * @return
	 * @throws ParseException
	 */
	public static int getDaysByMonth(final String month) throws ParseException
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(SDF.parse(getFirstDayOnMonth(month)));
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 使用默认样式（yyyyMMdd）将字符串转换成的日期
	 * 
	 * @param date
	 *            时间字符串
	 * @return
	 * @throws ParseException
	 */
	public static Date convertStringToDate(String date) throws ParseException
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(SDF.parse(date));
		return calendar.getTime();
	}

	public static Date parseDate(String dateString, String dateFormat)
	{
		return parse(dateString, dateFormat, Date.class);
	}

	public static Date parse(String dateString, String dateFormat)
	{
		return parse(dateString, dateFormat, Date.class);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Date> T parse(String dateString, String dateFormat, Class<T> targetResultType)
	{
		if (StringUtils.isEmpty(dateString))
		{
			return null;
		}
		DateFormat df = new SimpleDateFormat(dateFormat);
		try
		{
			long time = df.parse(dateString).getTime();
			Date t = targetResultType.getConstructor(long.class).newInstance(time);
			return (T) t;
		} catch (ParseException e)
		{
			String errorInfo = "cannot use dateformat:" + dateFormat + " parse datestring:" + dateString;
			throw new IllegalArgumentException(errorInfo, e);
		} catch (Exception e)
		{
			throw new IllegalArgumentException("error targetResultType:" + targetResultType.getName(), e);
		}
	}

	public static String format(Date date, String dateFormat)
	{
		if (date == null)
		{
			return null;
		}
		DateFormat df = new SimpleDateFormat(dateFormat);
		return df.format(date);
	}

	/**
	 * 获得某一天是星期几 使用自定义的时间格式
	 * 
	 * @param dateStr
	 *            时间字符串
	 * @param pattern
	 *            时间样式
	 * @return
	 * @throws Exception
	 */
	public static int getWeekByDay(String dateStr, String pattern) throws Exception
	{
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(dateStr));

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int yearCons = (14 - month) / 12;
		int yearConstant = year - yearCons;
		int monthConstant = month + 12 * yearCons - 2;
		return (day + yearConstant + yearConstant / 4 - yearConstant / 100 + yearConstant / 400 + 31 * monthConstant / 12) % 7;

	}

	/**
	 * 获得某一天是星期几 使用默认的时间格式：yyyyMMdd
	 * 
	 * @param dateStr
	 *            时间字符串
	 * @return
	 * @throws Exception
	 */
	public static int getWeekByDay(String dateStr) throws Exception
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(SDF.parse(dateStr));

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int yearCons = (14 - month) / 12;
		int yearConstant = year - yearCons;
		int monthConstant = month + 12 * yearCons - 2;
		return (day + yearConstant + yearConstant / 4 - yearConstant / 100 + yearConstant / 400 + 31 * monthConstant / 12) % 7;
	}

	/**
	 * 获得一个月的第一天
	 * 
	 * @param month
	 * @return
	 */
	private static String getFirstDayOnMonth(String month)
	{
		return month + "01";
	}

	/**
	 * 返回给定日期所在周的周一的日期
	 * 
	 * @param date
	 * @return
	 */
	public static Date getMonday(Date date)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return cal.getTime();
	}

	public static Date getDate(Date date,TimeZone originTimeZone,TimeZone targetTimeZone)
	{
		return new Date(date.getTime()-originTimeZone.getRawOffset()+targetTimeZone.getRawOffset());
	}
	
	public static Date getDate(Date date,String originTimeZone,String targetTimeZone)
	{
		TimeZone o = TimeZone.getTimeZone(originTimeZone);
		TimeZone t = TimeZone.getTimeZone(targetTimeZone);
		return new Date(date.getTime()-o.getRawOffset()+t.getRawOffset());
	}
	
	public static Date getNow()
	{
		return new Date();
	}
	
	/*public static void main(String[] args)
	{
		System.out.println(DateTimeUtils.date2String(new Date(), DateTimeUtils.DATE_TIME_FORMAT));
		System.out.println(DateTimeUtils.string2Date("2015-05-28 21:25:58", DateTimeUtils.DATE_TIME_FORMAT, Date.class));
		Date d = new Date();
		System.out.println(d.getTime());
		TimeZone tz_CN = TimeZone.getTimeZone("Asia/Shanghai");
		TimeZone tz_US = TimeZone.getTimeZone("America/New_York");
		long cnmlls = d.getTime() - tz_CN.getRawOffset() + tz_US.getRawOffset();
		
		Date d_US = new Date(cnmlls);
		System.out.println(d_US);
		
		Date dn = new Date(1483090575777l);
		
		System.out.println("dn:"+dn);
		//Fri Dec 30 17:36:15 CST 2016
		
	}*/
}
