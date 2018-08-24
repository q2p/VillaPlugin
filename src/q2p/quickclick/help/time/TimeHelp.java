package q2p.quickclick.help.time;

import java.text.*;
import java.util.*;

public final class TimeHelp {
	/***
	 * Синхронизирует выполнение на {@code format}'е
	 * @return Время в миллисекундах представляемое строкой или {@code null}, если не удалось обработать строку.
	 */
	public static Long parse(final SimpleDateFormat format, final String time) {
		synchronized(format) {
			try {
				return format.parse(time).getTime();
			} catch (final Exception e) {
				return null;
			}
		}
	}

	private static final SimpleDateFormat dateFormater = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
  private static final SimpleDateFormat[] dateFormaters = {
  	dateFormater,
  	new SimpleDateFormat("EEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US),
  	new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy", Locale.US)
  };
	
  public static String toHttpTime(final long milliseconds) {
		synchronized(dateFormater) {
	    return dateFormater.format(milliseconds);
		}
	}
	public static Long toMillieconds(final String timeString) {
		for(final SimpleDateFormat formater : dateFormaters) {
			final Long ret = parse(formater, timeString);
			if(ret != null)
				return ret;
		}
		return null;
	}
	public static String toCustomTime(long time) {
  	if(time < 1000)
  		return "0s";
  	
		final StringBuilder sb = new StringBuilder();
		
		if(time >= 60*60*1000) {
			sb.append(time/(60*60*1000));
			sb.append("h ");
			time %= 60;
		}
		if(time >= 60*1000) {
			sb.append(time/(60*1000));
			sb.append("m ");
			time %= 60;
		}
		if(time >= 1000) {
			sb.append(time/1000);
			sb.append("s ");
		}
		
		sb.setLength(sb.length()-1);
		
		return sb.toString();
	}
}