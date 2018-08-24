package q2p.quickclick.client;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import q2p.quickclick.*;

import java.nio.charset.StandardCharsets;

public class Authorization {
	public static final String LOG_ACT = ChatColor.RED + "You must log in or register to perform this action.";
	private static final byte minPasswordSize = 6;
	private static final byte maxPasswordSize = 32;
	static final String SIZE_LIMIT_STR = "must be at least "+minPasswordSize+" bytes long and at most "+maxPasswordSize+" bytes long.";
	static final String INVALID_LETTERS_STR = "can contain only lower and upper case latin letters and digits.";
	
	public static boolean checkCommand(Command command, CommandSender sender, String[] arguments) {
		if(command.getName().equals("register")) {
			if(!(sender instanceof Player))
				return true;
			Player p = (Player) sender;
			ClientInfo cli = ClientInfo.getFromPlayer(p);

			if(arguments.length != 2) {
				if(cli.isRegistered())
					p.sendRawMessage(ChatColor.GREEN + "You are already registered.");
				else
					p.sendRawMessage(ChatColor.RED + "To register you must type /register <password> <password>");

				return true;
			}

			if(!arguments[0].equals(arguments[1])) {
				if(cli.isRegistered())
					p.sendRawMessage(ChatColor.GREEN + "You are already registered.");
				else
					p.sendRawMessage(ChatColor.RED + "Passwords you've entered don't match.");

				return true;
			}

			final byte[] password = arguments[0].getBytes(StandardCharsets.UTF_8);

			if(cli.isRegistered()) {
				switch (cli.tryToLogin(password)) {
					case SUCCESS:
						p.sendRawMessage(ChatColor.GREEN + "You've successfully logged in.");
						break;
					case WRONG_PASSWORD:
						p.sendRawMessage(ChatColor.GREEN + "You are already registered.");
						break;
					case OUT_OF_ATTEMPTS:
						p.kickPlayer("You've failed to log in too many times");
						break;
				}

				return true;
			}

			if(password.length < minPasswordSize || password.length > maxPasswordSize) {
				p.sendRawMessage(ChatColor.RED + "Password " + SIZE_LIMIT_STR);
				return true;
			}

			cli.register(password);
			p.sendRawMessage(ChatColor.GREEN + "You've registered successfully!");
			onLogin(cli);
			return true;
		}
		if(command.getName().equals("login")) {
			if(!(sender instanceof Player))
				return true;
			Player p = (Player) sender;
			ClientInfo cli = ClientInfo.getFromPlayer(p);

			if(cli.isLoggedIn()) {
				sender.sendMessage(ChatColor.GREEN + "You are already logged in.");
				return true;
			}

			if(!cli.isRegistered()) {
				sender.sendMessage(ChatColor.RED + "You are not registered.");
				return true;
			}

			if(arguments.length != 1) {
				sender.sendMessage(ChatColor.RED + "Usage: /login <password>");
				return true;
			}

			final byte[] password = arguments[0].getBytes(StandardCharsets.UTF_8);

			switch(cli.tryToLogin(password)) {
				case SUCCESS:
					p.sendRawMessage(ChatColor.GREEN + "You've successfully logged in.");
					onLogin(cli);
					return true;
				case WRONG_PASSWORD:
					p.sendRawMessage(ChatColor.GREEN + "Wrong password, please try again.");
					return true;
				case OUT_OF_ATTEMPTS:
					p.kickPlayer("You've failed to log in too many times");
					return true;
			}
		}
		if(command.getName().equals("changepassword")) {
			if(!(sender instanceof Player))
				return true;
			Player p = (Player) sender;
			ClientInfo cli = ClientInfo.getFromPlayer(p);

			if(!cli.isLoggedIn())
				return true;

			if(arguments.length != 3) {
				sender.sendMessage(ChatColor.RED + "Usage /changepassword <old password> <new password> <new password>");
				return false;
			}

			if(!arguments[1].equals(arguments[2])) {
				sender.sendMessage(ChatColor.RED + "New passwords don't match.");
				return false;
			}

			final byte[] oldPassword = arguments[0].getBytes(StandardCharsets.UTF_8);
			final byte[] newPassword = arguments[1].getBytes(StandardCharsets.UTF_8);

			if(newPassword.length < minPasswordSize || newPassword.length > maxPasswordSize) {
				p.sendRawMessage(ChatColor.RED + "New password " + SIZE_LIMIT_STR);
				return true;
			}


			switch(cli.tryToChangePassword(oldPassword, newPassword)) {
				case SUCCESS:
					p.sendRawMessage(ChatColor.GREEN + "You've successfully changed your password.");
					break;
				case WRONG_PASSWORD:
					p.sendRawMessage(ChatColor.GREEN + "You've entered wrong old password.");
					break;
				case OUT_OF_ATTEMPTS:
					p.kickPlayer("You've failed to enter right password too many times");
					break;
			}

			return true;
		}
		if(command.getName().equals("clearpassword")) {
			if(!ClientPool.isLoggedAdminOrConsole(sender))
				return false;

			if(arguments.length != 1)
				sender.sendMessage(ChatColor.RED + "Usage: /clearpassword <player name>");

			// TODO:
			return true;
		}
		return false;
	}
	
	static void onLogin(ClientInfo cli) {
		cli.getPlayer().teleport(WorldManager.getSpawnLocation());
		cli.getPlayer().sendRawMessage(ChatColor.GREEN + "Welcome back "+cli.name()+"!");
		TitleSender.resetTitle(cli.getPlayer());
		// TODO: weather
	}

	public static void onJoin(final Player player) {
		ClientInfo cli = ClientInfo.getOnline(player);

		player.setHealthScale(40);
		player.setMaxHealth(40);
		player.setHealth(20);
		player.setGameMode(GameMode.ADVENTURE);
		player.setCanPickupItems(false);
		player.getInventory().clear();
		player.setAllowFlight(true);
		player.setFlying(true);
		player.teleport(WorldManager.getSpawnLocation());

		player.setPlayerTime(4000, false); // TODO: держать время в одном состоянии и послелогина ставить глобальное
		player.setPlayerWeather(WeatherType.CLEAR);

		Log.consoleInfo(player.getName() +  " подключается к серверу.");
		ClientPool.online.forEach((s, clientInfo) -> {
			if(clientInfo != cli) {
				clientInfo.getPlayer().sendRawMessage(ChatColor.GOLD + player.getName() +
					(clientInfo.preferesRussian() ? " подключается к серверу." : " connects to the server.")
				);
			}
		});

		if(cli.isRegistered()) {
			TitleSender.sendTitle(player, "Welcum back =3", false, ChatColor.LIGHT_PURPLE, 0, Integer.MAX_VALUE, 0);
			TitleSender.sendTitle(player, "/login <your password>", true, ChatColor.GOLD, 0, Integer.MAX_VALUE, 0);
		} else {
			TitleSender.sendTitle(player, "Welcum to my humble server =3", false, ChatColor.LIGHT_PURPLE, 0, Integer.MAX_VALUE, 0);
			TitleSender.sendTitle(player, "/register <your password> <your password>", true, ChatColor.GOLD, 0, Integer.MAX_VALUE, 0);
		}
	}
}
