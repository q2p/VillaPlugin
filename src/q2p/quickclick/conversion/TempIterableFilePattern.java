package q2p.quickclick.conversion;

import java.nio.file.Path;

public final class TempIterableFilePattern {
	public final Path folder;
	public final String prefix;
	public final String suffix;
	public final String ffmpegPattern;
	public TempIterableFilePattern(final Path folder, final String prefix, final String suffix) {
		this.folder = folder;
		this.prefix = prefix;
		this.suffix = suffix;
		ffmpegPattern = folder.toAbsolutePath().toString().replace('\\', '/')+'/'+prefix+"%03d"+suffix;
	}
}