package q2p.quickclick.client;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import q2p.quickclick.Assist;
import q2p.quickclick.Log;
import q2p.quickclick.QuickClick;

import java.util.TreeMap;

public final class ClientPool {
	private static final String BANNED_MSG = "You were banned on QuickClick :(";
	public static final TreeMap<String, ClientInfo> online = new TreeMap<>();
	
	public static void initialize() {
		for(final Player player : QuickClick.getPluginInstance().getServer().getOnlinePlayers()) {
			// TODO: кикнуть если не допустимый ник
			Authorization.onJoin(player);
		}
	}
	
	public static void deinitialize() {
		online.clear();
	}

	/** @return Является ли игрок главным админом или сервером */
	public static boolean isLoggedAdminOrConsole(final CommandSender sender) {
		if(sender instanceof Player) {
			ClientInfo clientInfo =ClientInfo.getFromPlayer((Player)sender);
			return clientInfo.isLoggedIn() && clientInfo.isAdmin();
		}

		return sender instanceof ConsoleCommandSender;
	}
	// Разрешить игроку подключится?
	public static void acceptPlayer(final PlayerLoginEvent event) {
		final String name = event.getPlayer().getName();
		if(!Assist.isValidName(name))
			event.disallow(Result.KICK_OTHER, "Your name contains forbidden characters.");
		else if(ClientPool.online.containsKey(name.toLowerCase()))
			event.disallow(Result.KICK_OTHER, "Player with your name is already on the server.");
		else
			event.allow();
	}
	// Отключение игрока
	public static void onExit(final PlayerQuitEvent event) {
		event.setQuitMessage(null);
		ClientInfo cli = ClientInfo.getFromPlayer(event.getPlayer());
		online.remove(cli.lowerCaseName());
	}

	public static void sendMessageForEachLoggedAdmin(final String english, final String russian) {
		Log.consoleInfo(russian);
		online.forEach((s, clientInfo) -> {
			if(clientInfo.isAdmin() && clientInfo.isLoggedIn())
				clientInfo.getPlayer().sendRawMessage(clientInfo.preferesRussian() ? russian : english);
		});
	}

	public static void sendMessageForEachLoggedAdmin(final String message) {
		sendMessageForEachLoggedAdmin(message, message);
	}
}