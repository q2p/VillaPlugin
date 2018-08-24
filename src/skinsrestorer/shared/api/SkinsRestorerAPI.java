package skinsrestorer.shared.api;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import skinsrestorer.bukkit.SkinsRestorer;
import skinsrestorer.bukkit.skinfactory.SkinFactory;
import skinsrestorer.bungee.SkinApplier;
import skinsrestorer.shared.storage.SkinStorage;
import skinsrestorer.shared.utils.MojangAPI.SkinRequestException;

public class SkinsRestorerAPI
{
  public SkinsRestorerAPI() {}
  
  public static void applySkin(Object player, Object props)
  {
    try
    {
      SkinsRestorer.getInstance().getFactory().applySkin((Player)player, props);
    }
    catch (Throwable t)
    {
      SkinApplier.applySkin((net.md_5.bungee.api.connection.ProxiedPlayer)player);
    }
  }
  







  public static String getSkinName(String playerName)
  {
    return SkinStorage.getPlayerSkin(playerName);
  }
  





  public static Object getSkin(String skinName)
  {
    try
    {
      return SkinStorage.getOrCreateSkinForPlayer(skinName);
    } catch (MojangAPI.SkinRequestException e) {
      e.printStackTrace();
    }
    return null;
  }
  






  public static boolean hasSkin(String playerName)
  {
    return SkinStorage.getPlayerSkin(playerName) != null;
  }
  





  public static void removeSkin(String playername)
  {
    SkinStorage.removePlayerSkin(playername);
  }
  








  public static void setSkin(String playerName, String skinName)
  {
    try
    {
      skinsrestorer.shared.utils.MojangAPI.getUUID(skinName);
      SkinStorage.setPlayerSkin(playerName, skinName);
      SkinStorage.setSkinData(skinName, SkinStorage.getOrCreateSkinForPlayer(skinName));
    } catch (Throwable t) {
      Player p = null;
      try
      {
        p = (Player)com.google.common.collect.Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
      } catch (Exception e) {
        p = (Player)Bukkit.getOnlinePlayers().iterator().next();
      }
      
      if (p != null) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try
        {
          out.writeUTF("SkinsRestorer");
          out.writeUTF(playerName);
          out.writeUTF(skinName);
          
          p.sendPluginMessage(SkinsRestorer.getInstance(), "BungeeCord", b
            .toByteArray());
          
          out.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}
