package skinsrestorer.shared.utils.updater.update.spiget.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import skinsrestorer.shared.utils.updater.update.spiget.ResourceInfo;


























public class UpdateDownloader
{
  public static final String RESOURCE_DOWNLOAD = "http://api.spiget.org/v2/resources/%s/download";
  
  public UpdateDownloader() {}
  
  public static Runnable downloadAsync(ResourceInfo info, final File file, final String userAgent, final DownloadCallback callback)
  {
    new Runnable()
    {
      public void run() {
        try {
          UpdateDownloader.download(val$info, file, userAgent);
          callback.finished();
        } catch (Exception e) {
          callback.error(e);
        }
      }
    };
  }
  
  public static void download(ResourceInfo info, File file) {
    download(info, file);
  }
  
  public static void download(ResourceInfo info, File file, String userAgent) {
    if (external) { throw new IllegalArgumentException("Cannot download external resource #" + id);
    }
    try
    {
      HttpURLConnection connection = (HttpURLConnection)new URL(String.format("http://api.spiget.org/v2/resources/%s/download", new Object[] { Integer.valueOf(id) })).openConnection();
      connection.setRequestProperty("User-Agent", userAgent);
      if (connection.getResponseCode() != 200) {
        throw new RuntimeException("Download returned status #" + connection.getResponseCode());
      }
      channel = Channels.newChannel(connection.getInputStream());
    } catch (IOException e) { ReadableByteChannel channel;
      throw new RuntimeException("Download failed", e);
    }
    try { ReadableByteChannel channel;
      FileOutputStream output = new FileOutputStream(file);
      output.getChannel().transferFrom(channel, 0L, Long.MAX_VALUE);
      output.flush();
      output.close();
    } catch (IOException e) {
      throw new RuntimeException("Could not save file", e);
    }
  }
}
