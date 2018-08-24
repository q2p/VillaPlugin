package q2p.quickclick;

import org.bukkit.entity.Player;

import java.util.TreeSet;

public class BannedCommands {
	private static final TreeSet<String> bannedCommands = new TreeSet<>();

	public static boolean onCommandPreProcess(final String message, final Player player) {
		if(player == null)
			return false;

		if(message.startsWith("/")) {
			int idx = message.indexOf(" ");
			if(idx == -1)
				idx = message.length();

			String title = message.substring(1, idx).toLowerCase();
			return bannedCommands.contains(title);
		}
		return false;
	}

	public static void initialize() {
		Parsing.forEachItem(
			MainSerializer.loadAsString("blockedCommands.txt", 64*1024),
			'\n', (String commandLine) -> bannedCommands.add(commandLine.trim())
		);
	}

	public static void deinitialize() {
		bannedCommands.clear();
	}
}
