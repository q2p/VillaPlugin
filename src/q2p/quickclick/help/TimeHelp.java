package q2p.quickclick.help;

import java.text.*;
import java.util.*;

public final class TimeHelp {
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
}