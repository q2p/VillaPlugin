package skinsrestorer.shared.storage;

import java.util.concurrent.ConcurrentHashMap;

public class CooldownStorage implements Runnable {
  public CooldownStorage() {}
  
  private static final ConcurrentHashMap<String, Long> cooldown = new ConcurrentHashMap();
  
  public static boolean hasCooldown(String name) {
    Long expire = (Long)cooldown.get(name);
    if (expire != null)
      return expire.longValue() > System.currentTimeMillis();
    return false;
  }
  
  public static void resetCooldown(String name) {
    cooldown.remove(name);
  }
  
  public static int getCooldown(String name) {
    int int1 = Integer.valueOf(String.format("%d", new Object[] {
      Long.valueOf(java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(((Long)cooldown.get(name)).longValue())) })).intValue();
    int int2 = Integer.valueOf(String.format("%d", new Object[] {
      Long.valueOf(java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())) })).intValue();
    return int1 - int2;
  }
  
  public static void setCooldown(String name, int cooldowntime, java.util.concurrent.TimeUnit timeunit)
  {
    cooldown.put(name, Long.valueOf(System.currentTimeMillis() + timeunit.toMillis(cooldowntime)));
  }
  
  public void run()
  {
    long current = System.currentTimeMillis();
    java.util.Iterator<Long> iterator = cooldown.values().iterator();
    while (iterator.hasNext()) {
      if (((Long)iterator.next()).longValue() <= current) {
        iterator.remove();
      }
    }
  }
}
