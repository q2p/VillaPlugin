package skinsrestorer.shared.utils.updater.update.spiget;

public abstract interface UpdateCallback
{
  public abstract void updateAvailable(String paramString1, String paramString2, boolean paramBoolean);
  
  public abstract void upToDate();
}
