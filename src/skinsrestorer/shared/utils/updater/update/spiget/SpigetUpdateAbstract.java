package skinsrestorer.shared.utils.updater.update.spiget;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import skinsrestorer.shared.utils.updater.update.spiget.comparator.VersionComparator;






























public abstract class SpigetUpdateAbstract
{
  public static final String RESOURCE_INFO = "http://api.spiget.org/v2/resources/%s?ut=%s";
  public static final String RESOURCE_VERSION = "http://api.spiget.org/v2/resources/%s/versions/latest?ut=%s";
  protected final int resourceId;
  protected final String currentVersion;
  protected final Logger log;
  protected String userAgent = "SpigetResourceUpdater";
  protected VersionComparator versionComparator = VersionComparator.EQUAL;
  protected ResourceInfo latestResourceInfo;
  
  public SpigetUpdateAbstract(int resourceId, String currentVersion, Logger log)
  {
    this.resourceId = resourceId;
    this.currentVersion = currentVersion;
    this.log = log;
  }
  
  public SpigetUpdateAbstract setUserAgent(String userAgent) {
    this.userAgent = userAgent;
    return this;
  }
  
  public String getUserAgent() {
    return userAgent;
  }
  
  public SpigetUpdateAbstract setVersionComparator(VersionComparator comparator) {
    versionComparator = comparator;
    return this;
  }
  
  public ResourceInfo getLatestResourceInfo() {
    return latestResourceInfo;
  }
  
  protected abstract void dispatch(Runnable paramRunnable);
  
  public boolean isVersionNewer(String oldVersion, String newVersion) {
    return versionComparator.isNewer(oldVersion, newVersion);
  }
  
  public void checkForUpdate(final UpdateCallback callback) {
    dispatch(new Runnable()
    {
      public void run() {
        try {
          HttpURLConnection connection = (HttpURLConnection)new URL(String.format("http://api.spiget.org/v2/resources/%s?ut=%s", new Object[] { Integer.valueOf(resourceId), Long.valueOf(System.currentTimeMillis()) })).openConnection();
          connection.setRequestProperty("User-Agent", getUserAgent());
          JsonObject jsonObject = new JsonParser().parse(new InputStreamReader(connection.getInputStream())).getAsJsonObject();
          latestResourceInfo = ((ResourceInfo)new Gson().fromJson(jsonObject, ResourceInfo.class));
          
          connection = (HttpURLConnection)new URL(String.format("http://api.spiget.org/v2/resources/%s/versions/latest?ut=%s", new Object[] { Integer.valueOf(resourceId), Long.valueOf(System.currentTimeMillis()) })).openConnection();
          connection.setRequestProperty("User-Agent", getUserAgent());
          jsonObject = new JsonParser().parse(new InputStreamReader(connection.getInputStream())).getAsJsonObject();
          latestResourceInfo.latestVersion = ((ResourceVersion)new Gson().fromJson(jsonObject, ResourceVersion.class));
          
          if (isVersionNewer(currentVersion, latestResourceInfo.latestVersion.name)) {
            callback.updateAvailable(latestResourceInfo.latestVersion.name, "https://spigotmc.org/" + latestResourceInfo.file.url, !latestResourceInfo.external);
          } else {
            callback.upToDate();
          }
        } catch (Exception e) {
          log.log(Level.WARNING, "Failed to get resource info from spiget.org", e);
        }
      }
    });
  }
}
