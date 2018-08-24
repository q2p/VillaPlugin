package q2p.quickclick.base.time;

import java.util.*;

public final class Time {
	public static final short second = 1000;
	public static final int   minute =   60  * second;
	public static final int   hour   =   60  * minute;
	public static final int   day    =   24  * hour;
	public static final int   week   =    7  * day;
	public static final long  month  =   30L * day;
	public static final long  year   =  365L * day;

	public static boolean limit(final long timeLimit) {
		return System.currentTimeMillis() > timeLimit;
	}

	private static final TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");

	public static Calendar currentGMTCalendar() {
		return Calendar.getInstance(gmtTimeZone, Locale.US);
	}
}