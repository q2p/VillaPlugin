package q2p.quickclick;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import q2p.quickclick.client.ClientInfo;
import q2p.quickclick.client.ClientPool;

public class AdminManager {
	public static boolean checkCommand(Command command, CommandSender sender, String[] args) {
		if(!command.getName().equals("admin"))
			return false;

		if(!(sender instanceof ConsoleCommandSender))
			return false;

		if(args.length != 2) {
			sender.sendMessage("Usage: /admin <add|remove> <player name>");
			return false;
		}

		final boolean makeAdmin;
		switch(args[0]) {
			case "add":
				makeAdmin = true;
				break;
			case "remove":
				makeAdmin = false;
				break;
			default:
				sender.sendMessage("Usage: /admin <add|remove> <player name>");
				return true;
		}

		String playerName = args[1].toLowerCase();
		ClientInfo clientInfo = ClientPool.online.get(playerName);

		if(clientInfo == null) {
			ClientInfo.getOffline(playerName).setAdmin(makeAdmin);
		} else {
			clientInfo.setAdmin(makeAdmin);
			playerName = clientInfo.name();
			clientInfo.getPlayer().sendRawMessage("You are admin now.");
		}

		if(makeAdmin)
			ClientPool.sendMessageForEachLoggedAdmin("Player " + playerName + " is an admin now.");
		else
			ClientPool.sendMessageForEachLoggedAdmin("Player " + playerName + " is not an admin anymore.");

		return true;
	}
}