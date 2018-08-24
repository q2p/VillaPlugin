package q2p.quickclick;

import org.bukkit.ChatColor;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

public class OutsideInfo {
	private static final ArrayList<CachedServerIcon> icons = new ArrayList<>();
	private static final TreeMap<String, CachedServerIcon> iconsTree = new TreeMap<>();

	private static final int maxPlayersCount = 34346969;
	private static final String motd =
		""+
		ChatColor.DARK_GRAY +
		ChatColor.BOLD +
		"Привет, " +
		ChatColor.LIGHT_PURPLE +
		ChatColor.BOLD +
		"Лапуся" +
		ChatColor.DARK_PURPLE +
		ChatColor.BOLD +
		" ♥" +
		ChatColor.DARK_GRAY +
		ChatColor.BOLD +
		"\n" +
		ChatColor.LIGHT_PURPLE +
		ChatColor.BOLD +
		"Чмок" +
		ChatColor.DARK_GRAY +
		ChatColor.BOLD +
		" в " +
		ChatColor.LIGHT_PURPLE +
		ChatColor.BOLD +
		ChatColor.UNDERLINE +
		"писю" +
		ChatColor.DARK_PURPLE +
		ChatColor.BOLD +
		" :3";

	public static void initialize() {
		try {
			Files.list(MainSerializer.resourcesPath().resolve("server-icons/")).forEach(path -> {
				if(!Files.isDirectory(path)) {
					final String fileName = path.getFileName().toString();
					if(fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
						try {
							CachedServerIcon icon = QuickClick.getPluginInstance().getServer().loadServerIcon(path.toAbsolutePath().toFile());
							icons.add(icon);
							iconsTree.put(fileName, icon);
						} catch(final Throwable t) {
							Log.consoleWarn("Не удалось загрузить файл: server-icons/"+fileName);
						}
					}
				}
			});
		} catch(final IOException e) {
			Log.consoleWarn("Не удалось загрузить файлы из: server-icons/");
		}
		icons.trimToSize();
	}

	public static void deinitialize() {
		icons.clear();
	}

	public static void onPing(ServerListPingEvent event) {
		event.setMaxPlayers(maxPlayersCount);
		event.setServerIcon(icons.get(new Random().nextInt(OutsideInfo.icons.size())));
		event.setMotd(motd);
	}
}
