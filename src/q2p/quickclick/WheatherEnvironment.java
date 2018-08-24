package q2p.quickclick;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public final class WheatherEnvironment {
	public static void tick() {
		WorldManager.getWorld().setFullTime(6000);
	}

	//TODO: нужно?
	public static int worldTimeFromRealTime() {
		final Calendar defaultCalendar = getDefaultCalendar();
		return
			defaultCalendar.get(Calendar.HOUR_OF_DAY) * 1000 +
			defaultCalendar.get(Calendar.MINUTE     ) * 1000 / 60 +
			defaultCalendar.get(Calendar.SECOND     ) * 1000 / (60 * 60);
	}
	public static Calendar getDefaultCalendar(){
		return Calendar.getInstance(TimeZone.getTimeZone("GMT"), new Locale("en"));
	}
}