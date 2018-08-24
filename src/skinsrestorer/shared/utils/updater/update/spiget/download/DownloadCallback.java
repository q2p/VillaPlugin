package skinsrestorer.shared.utils.updater.update.spiget.download;

public abstract interface DownloadCallback
{
  public abstract void finished();
  
  public abstract void error(Exception paramException);
}
