package q2p.quickclick;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

public class WorldManager {
	private static World mainWorld;
	public static World getWorld() {
		return mainWorld;
	}

	private static final float spawnX, spawnY, spawnZ;
	private static final Location spawnLocation;
	public static Location getSpawnLocation() {
		return spawnLocation;
	}
	private static final Location loginLocation;
	public static Location getLoginLocation() {
		return loginLocation;
	}

	private static final float spawnYaw, spawnPitch;

	private static final float minX, minY, minZ;
	private static final float maxX, maxY, maxZ;

	static {
		List<World> worlds = QuickClick.getPluginInstance().getServer().getWorlds();
		if(worlds.size() != 1)
			Abort.message("Для работы плагина на сервере должн быть только один мир.");
		mainWorld = worlds.get(0);

		final String safeBoxRules = MainSerializer.loadAsString("entrancebox.txt", 64);
		final String[] lines = new String[2];
		final Float[] min = new Float[3];
		final Float[] max = new Float[3];
		if(
			!Parsing.fillContainer(safeBoxRules, '\n', lines, (String line) -> line) ||
			!Parsing.fillContainer(lines[0], ' ', min, Float::parseFloat) ||
			!Parsing.fillContainer(lines[1], ' ', max, Float::parseFloat) ||

			min[0] >= max[0] || min[1] >= max[1] || min[2] >= max[2]
		) Abort.message("Ошибка в форматировании entrancebox.txt");

		minX = min[0];
		minY = min[1];
		minZ = min[2];
		maxX = max[0];
		maxY = max[1];
		maxZ = max[2];

		final String spawnLocationString = MainSerializer.loadAsString("spawn.txt", 64);
		final Float[] spawnPos = new Float[3];
		final Float[] spawnRot = new Float[2];
		if(
			!Parsing.fillContainer(spawnLocationString, '\n', lines, (String line) -> line) ||
			!Parsing.fillContainer(lines[0], ' ', spawnPos, Float::parseFloat) ||
			!Parsing.fillContainer(lines[1], ' ', spawnRot, Float::parseFloat)
		) Abort.message("Ошибка в форматировании spawn.txt");

		spawnX = spawnPos[0];
		spawnY = spawnPos[1];
		spawnZ = spawnPos[2];

		spawnYaw = spawnRot[0];
		spawnPitch = spawnRot[1];

		spawnLocation = new Location(mainWorld, spawnX, spawnY, spawnZ, spawnYaw, spawnPitch);
		loginLocation = new Location(mainWorld, 0, 2048, 0, 0, 90);
	}
	
	//TODO:
	/*public static void generateRespawnBox() {
		for(int x = -3; x <= -1; x++) {
			for(int y = 0; y <= 3; y++) {
				for(int z = 0; z <= 2; z++) {
					HubStatus.world.getBlockAt(x, y, z).setType(Material.GLOWSTONE);
				}
			}
		}
		for(int y = 1; y <= 2; y++) HubStatus.world.getBlockAt(-2, y, 1).setType(Material.AIR);
	}
	public static Location getAuthBoxLocation() {
		return new Location(HubStatus.world, -1.5, 1, 1.5);
	}*/

	public static boolean outOfBoundaries(final Location location) {
		final double x = location.getX();
		final double y = location.getY();
		final double z = location.getZ();

		return x < minX || x > maxX || y < minY || y > maxY || z < minZ || z > maxZ;
	}
}