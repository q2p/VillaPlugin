package q2p.quickclick;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.List;

public final class Assist {
	private static final char[] allowedSymbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-.,;".toCharArray();
	private static final boolean[] allowedTable = new boolean[256];
	private static int minName = 3;
	private static int maxName = 32;

	static {
		for(char c : allowedSymbols)
			allowedTable[c] = true;
	}
	public static boolean isValidSymbols(final String string) {
		for(int i = string.length() - 1; i != -1; i--) {
			char c = string.charAt(i);
			if(c >= allowedTable.length || !allowedTable[c]) {
				return false;
			}
		}
		return true;
	}
	public static boolean isValidName(final String name) {
		return name.length() >= minName && name.length() <= maxName && isValidSymbols(name);
	}
	public static Location rayTrace(Location start, Vector ray, double startX, double startY, double startZ, double endX, double endY, double endZ) {
		double dirfracx = 1.0 / ray.getX();
		double dirfracy = 1.0 / ray.getY();
		double dirfracz = 1.0 / ray.getZ();
		
		double t1 = (startX - start.getX())*dirfracx;
		double t2 = (endX - start.getX())*dirfracx;
		double t3 = (startY - start.getY())*dirfracy;
		double t4 = (endY - start.getY())*dirfracy;
		double t5 = (startZ - start.getZ())*dirfracz;
		double t6 = (endZ - start.getZ())*dirfracz;
		
		double tMin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
		double tMax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));
		
		if(tMax >= 0 && tMin <= tMax)
			return start.add(ray.multiply(tMin));
		return null;
	}
	public static float distance(Location location1, Location location2) {
		float dx = (float)(location1.getX() - location2.getX());
	  float dy = (float)(location1.getY() - location2.getY());
	  float dz = (float)(location1.getZ() - location2.getZ());

	  return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
	public static boolean aabbXaabb(double x1, double y1, double z1, double x2, double y2, double z2, double xSize1, double ySize1, double zSize1, double xSize2, double ySize2, double zSize2) {
		return (Math.abs(x1 - x2) * 2 < (xSize1 + xSize2)) && (Math.abs(y1 - y2) * 2 < (ySize1 + ySize2)) && (Math.abs(z1 - z2) * 2 < (zSize1 + zSize2));
	}

	public static void addToList(final List<String> list, final String ... elements) {
		for(String element : elements)
			list.add(element);
	}
}