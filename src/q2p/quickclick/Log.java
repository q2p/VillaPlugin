package q2p.quickclick;

import org.bukkit.ChatColor;

import java.util.logging.Logger;

public final class Log {
	private Log() {}
	private static Logger logger;
	
	public static void consoleInfo(final String message) {
		logger.info(message);
	}
	public static void consoleWarn(final String message) {
		logger.warning(message);
	}
	public static void consoleAdminWarn(final String message) {
		// adminInfo(message);
		consoleWarn(message);
	}
	
	public static void initLog(final Logger logger) {
		Log.logger = logger;
	}

	public static String formatThrowable(final Throwable throwable) {
		// TODO:
		return ChatColor.RED + throwable.getMessage();
	}
}