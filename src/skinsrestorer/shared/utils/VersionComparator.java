package skinsrestorer.shared.utils;

import java.io.PrintStream;






























public abstract class VersionComparator
{
  public static final VersionComparator EQUAL = new VersionComparator()
  {
    public boolean isNewer(String currentVersion, String checkVersion) {
      return !currentVersion.equals(checkVersion);
    }
  };
  



  public static final VersionComparator SEM_VER = new VersionComparator()
  {
    public boolean isNewer(String currentVersion, String checkVersion) {
      currentVersion = currentVersion.replace(".", "");
      checkVersion = checkVersion.replace(".", "");
      try
      {
        int current = Integer.parseInt(currentVersion);
        int check = Integer.parseInt(checkVersion);
        
        return check > current;
      } catch (NumberFormatException e) {
        System.err.println("[SpigetUpdate] Invalid SemVer versions specified [" + currentVersion + "] [" + checkVersion + "]");
      }
      return false;
    }
  };
  



  public static final VersionComparator SEM_VER_SNAPSHOT = new VersionComparator()
  {
    public boolean isNewer(String currentVersion, String checkVersion) {
      currentVersion = currentVersion.replace("-SNAPSHOT", "");
      checkVersion = checkVersion.replace("-SNAPSHOT", "");
      
      return SEM_VER.isNewer(currentVersion, checkVersion);
    }
  };
  
  public VersionComparator() {}
  
  public abstract boolean isNewer(String paramString1, String paramString2);
}
