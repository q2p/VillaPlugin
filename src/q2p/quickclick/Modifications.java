package q2p.quickclick;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Modifications {
	private static final boolean modificationMode = false;

	public static boolean isInModificationMode() {
		return modificationMode;
	}

	public static boolean checkCommand(Command command, CommandSender sender, String[] args) {
		if(!command.getName().equals("modify"))
			return false;

		return true;
	}
}
