package skinsrestorer.bukkit;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.World;

public class MCoreAPI
{
  public MCoreAPI() {}
  
  private static MultiverseCore mcore = null;
  
  public static boolean check() {
    if (mcore != null)
      return true;
    return false;
  }
  
  public static int dimension(World world)
  {
    if ((getWorldScale(world) == 1.0D) || (getWorldScale(world) == 14.0D))
      return 0;
    if ((getWorldScale(world) == 8.0D) || (getWorldScale(world) == 13.0D))
      return -1;
    if ((getWorldScale(world) == 16.0D) || (getWorldScale(world) == 12.0D))
      return 1;
    return 0;
  }
  
  public static double getWorldScale(World world) {
    return mcore.getMVWorldManager().getMVWorld(world).getScaling();
  }
  
  public static void init() {
    org.bukkit.plugin.Plugin plugin = org.bukkit.Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
    if ((plugin instanceof MultiverseCore)) {
      mcore = (MultiverseCore)plugin;
    }
  }
}
