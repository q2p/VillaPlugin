package skinsrestorer.bungee;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.connection.LoginResult.Property;
import skinsrestorer.shared.utils.ReflectionUtil;

public class SkinApplier
{
  private static Class<?> LoginResult;
  
  public SkinApplier() {}
  
  public static void applySkin(ProxiedPlayer p)
  {
    SkinsRestorer.getInstance().getExecutor().submit(new Runnable()
    {
      public void run()
      {
        try {
          LoginResult.Property textures = (LoginResult.Property)skinsrestorer.shared.storage.SkinStorage.getOrCreateSkinForPlayer(val$p.getName());
          
          InitialHandler handler = (InitialHandler)val$p.getPendingConnection();
          
          if (handler.isOnlineMode()) {
            SkinApplier.sendUpdateRequest(val$p, textures);
            return;
          }
          LoginResult profile = null;
          
          try
          {
            profile = (LoginResult)ReflectionUtil.invokeConstructor(SkinApplier.LoginResult, new Class[] { String.class, String.class, [Lnet.md_5.bungee.connection.LoginResult.Property.class }, new Object[] {val$p
            
              .getUniqueId().toString(), val$p.getName(), { textures } });
          }
          catch (Exception e) {
            profile = (LoginResult)ReflectionUtil.invokeConstructor(SkinApplier.LoginResult, new Class[] { String.class, [Lnet.md_5.bungee.connection.LoginResult.Property.class }, new Object[] {val$p
              .getUniqueId().toString(), { textures } });
          }
          
          LoginResult.Property[] present = profile.getProperties();
          LoginResult.Property[] newprops = new LoginResult.Property[present.length + 1];
          System.arraycopy(present, 0, newprops, 0, present.length);
          newprops[present.length] = textures;
          profile.getProperties()[0].setName(newprops[0].getName());
          profile.getProperties()[0].setValue(newprops[0].getValue());
          profile.getProperties()[0].setSignature(newprops[0].getSignature());
          ReflectionUtil.setObject(InitialHandler.class, handler, "loginProfile", profile);
          
          if (SkinsRestorer.getInstance().isMultiBungee()) {
            SkinApplier.sendUpdateRequest(val$p, textures);
          } else {
            SkinApplier.sendUpdateRequest(val$p, null);
          }
        }
        catch (Exception localException1) {}
      }
    });
  }
  
  public static void applySkin(String pname)
  {
    ProxiedPlayer p = ProxyServer.getInstance().getPlayer(pname);
    if (p != null)
      applySkin(p);
  }
  
  public static void init() {
    try {
      LoginResult = ReflectionUtil.getBungeeClass("connection", "LoginResult");
    }
    catch (Exception localException) {}
  }
  
  private static void sendUpdateRequest(ProxiedPlayer p, LoginResult.Property textures)
  {
    ByteArrayOutputStream b = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(b);
    try {
      out.writeUTF("SkinUpdate");
      
      if (textures != null) {
        out.writeUTF(textures.getName());
        out.writeUTF(textures.getValue());
        out.writeUTF(textures.getSignature());
      }
      
      p.getServer().sendData("SkinsRestorer", b.toByteArray());
    }
    catch (Exception localException) {}
  }
}
