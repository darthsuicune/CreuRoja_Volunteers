package net.creuroja.android.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {
	public static Date parse(String time) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
		return format.parse(time);
	}
}
