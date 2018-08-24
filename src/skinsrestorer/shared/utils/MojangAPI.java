package skinsrestorer.shared.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import skinsrestorer.shared.storage.Locale;

public class MojangAPI
{
  private static final String uuidurl = "https://api.mojang.com/users/profiles/minecraft/";
  private static final String skinurl = "https://sessionserver.mojang.com/session/minecraft/profile/";
  private static MojangAPI mojangapi = new MojangAPI();
  

  public MojangAPI() {}
  

  public static Object getSkinProperty(String uuid)
    throws MojangAPI.SkinRequestException
  {
    try
    {
      String output = readURL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
      
      String sigbeg = "\",\"signature\":\"";
      String mid = "[{\"name\":\"textures\",\"value\":\"";
      String valend = "\"}]";
      
      String signature = "";String value = "";
      
      value = getStringBetween(output, mid, sigbeg);
      signature = getStringBetween(output, sigbeg, valend);
      
      return skinsrestorer.shared.storage.SkinStorage.createProperty("textures", value, signature);
    } catch (Exception e) {
      System.out.println("[SkinsRestorer] Switching to proxy to get skin property."); }
    return getSkinPropertyProxy(uuid);
  }
  
  public static Object getSkinPropertyProxy(String uuid) throws MojangAPI.SkinRequestException
  {
    try
    {
      String output = readURLProxy("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
      
      String sigbeg = "\",\"signature\":\"";
      String mid = "[{\"name\":\"textures\",\"value\":\"";
      String valend = "\"}]";
      
      String signature = "";String value = "";
      
      value = getStringBetween(output, mid, sigbeg);
      signature = getStringBetween(output, sigbeg, valend);
      
      return skinsrestorer.shared.storage.SkinStorage.createProperty("textures", value, signature);
    } catch (Exception e) {
      System.out.println("[SkinsRestorer] Failed to get proxy."); }
    return Boolean.valueOf(false);
  }
  
  public static String getStringBetween(String base, String begin, String end)
  {
    try {
      Pattern patbeg = Pattern.compile(Pattern.quote(begin));
      Pattern patend = Pattern.compile(Pattern.quote(end));
      
      int resbeg = 0;
      int resend = base.length() - 1;
      
      Matcher matbeg = patbeg.matcher(base);
      
      while (matbeg.find()) {
        resbeg = matbeg.end();
      }
      Matcher matend = patend.matcher(base);
      
      while (matend.find()) {
        resend = matend.start();
      }
      return base.substring(resbeg, resend);
    } catch (Exception e) {}
    return base;
  }
  




  public static String getUUID(String name)
    throws MojangAPI.SkinRequestException
  {
    try
    {
      String output = readURL("https://api.mojang.com/users/profiles/minecraft/" + name);
      
      if (output.isEmpty())
        throw new SkinRequestException(Locale.NOT_PREMIUM);
      if (output.contains("\"error\"")) {
        return getUUIDProxy(name);
      }
      return output.substring(7, 39);
    } catch (IOException e) {
      System.out.println("[SkinsRestorer] Switching to proxy to get skin property."); }
    return getUUIDProxy(name);
  }
  
  public static String getUUIDProxy(String name) throws MojangAPI.SkinRequestException
  {
    try
    {
      String output = readURLProxy("https://api.mojang.com/users/profiles/minecraft/" + name);
      
      if (output.isEmpty())
        throw new SkinRequestException(Locale.NOT_PREMIUM);
      if (output.contains("\"error\"")) {
        throw new SkinRequestException(Locale.ALT_API_FAILED);
      }
      return output.substring(7, 39);
    } catch (IOException e) {
      throw new SkinRequestException(e.getMessage());
    }
  }
  
  public static MojangAPI get() {
    return mojangapi;
  }
  
  public static int rand(int High) {
    Random r = new Random();
    return r.nextInt(High - 1) + 1;
  }
  
  private static String readURL(String url)
    throws MalformedURLException, IOException, MojangAPI.SkinRequestException
  {
    HttpURLConnection con = (HttpURLConnection)new URL(url).openConnection();
    
    con.setRequestMethod("GET");
    con.setRequestProperty("User-Agent", "SkinsRestorer");
    con.setConnectTimeout(5000);
    con.setReadTimeout(5000);
    con.setDoOutput(true);
    

    StringBuilder output = new StringBuilder();
    BufferedReader in = new BufferedReader(new java.io.InputStreamReader(con.getInputStream()));
    String line;
    while ((line = in.readLine()) != null) {
      output.append(line);
    }
    in.close();
    return output.toString();
  }
  
  private static String readURLProxy(String url) throws MalformedURLException, IOException, MojangAPI.SkinRequestException {
    HttpURLConnection con = null;
    String ip = null;
    int port = 0;
    String proxyStr = null;
    List<String> list = ProxyManager.getList();
    proxyStr = (String)list.get(rand(list.size() - 1));
    String[] realProxy = proxyStr.split(":");
    ip = realProxy[0];
    port = Integer.valueOf(realProxy[1]).intValue();
    java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new java.net.InetSocketAddress(ip, port));
    con = (HttpURLConnection)new URL(url).openConnection(proxy);
    
    con.setRequestMethod("GET");
    con.setRequestProperty("User-Agent", "SkinsRestorer");
    con.setConnectTimeout(5000);
    con.setReadTimeout(5000);
    con.setDoOutput(true);
    

    StringBuilder output = new StringBuilder();
    BufferedReader in = new BufferedReader(new java.io.InputStreamReader(con.getInputStream()));
    String line;
    while ((line = in.readLine()) != null) {
      output.append(line);
    }
    in.close();
    
    return output.toString();
  }
  
  public static class SkinRequestException extends Exception {
    private String reason;
    
    public SkinRequestException(String reason) {
      this.reason = reason;
    }
    
    public String getReason() { return reason; }
  }
}
