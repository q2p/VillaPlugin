package q2p.quickclick;

import q2p.quickclick.base.shorters.CompactNumber;
import q2p.quickclick.conversion.CUID;
import q2p.quickclick.conversion.FFMpeg;
import q2p.quickclick.conversion.TempIterableFilePattern;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.Random;

	public final class MainSerializer {
		private static final Path villaResPath;
		public static Path resourcesPath() {
			return villaResPath;
		}
		private static final Path tempPath;
		public static Path getTempPath() {
			return tempPath;
		}

		static {
			String pathString = null;
			try {
				pathString = new String(loadBytes(Paths.get("pathToVillaRes.txt").toAbsolutePath(), 1024).array(), StandardCharsets.UTF_8);
			} catch(final Throwable throwable) {
				Abort.message("Не удалось прочитать pathToVillaRes.txt");
			}
			assert pathString != null;
			villaResPath = Paths.get(pathString).toAbsolutePath();
			tempPath = villaResPath.resolve(loadAsString("tempPath.txt", 1024).trim()).normalize().toAbsolutePath();
		}
		public static ByteBuffer loadBytes(final Path path, final int maxSize) {
			try {
				assert path != null && maxSize >= 0;
				try(FileChannel fc = FileChannel.open(path, StandardOpenOption.READ)) {
					long size = fc.size();
					if(size > maxSize)
						throw new ArrayIndexOutOfBoundsException();
					final ByteBuffer bb = ByteBuffer.allocate((int) size);
					while(bb.hasRemaining())
						fc.read(bb);
					bb.clear();
					return bb;
				}
			} catch(final Throwable t) {
				Abort.message("Не удалось прочтать файл: "+path.toString(), t);
				return ByteBuffer.allocate(0);
			}
		}
		public static ByteBuffer loadBytes(final String subPath, final int maxSize) {
			return loadBytes(villaResPath.resolve(subPath), maxSize);
		}
		public static String loadAsString(final String subPath, final int maxSize) {
			return new String(loadBytes(subPath, maxSize).array(), StandardCharsets.UTF_8);
		}

		public static void saveBytes(final String subPath, final ByteBuffer bytes) {
			assert subPath != null;
			final Path path = villaResPath.resolve(subPath);
			try(FileChannel fc = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
				while(bytes.hasRemaining())
					fc.write(bytes);
			} catch(final Throwable t) {
				Abort.message("Не удалось прочтать файл: "+path.toString(), t);
			}
		}

		private static long num = 0;
		private static synchronized long getTempNumber() {
			return num++;
		}
		public static Path getRandomTempFile(final String suffix) {
			// TODO: корявая имплементация
			return tempPath.resolve(CompactNumber.encode(getTempNumber())+'_'+suffix).toAbsolutePath();
		}
		public static TempIterableFilePattern getRandomTempIterable(final String suffix) {
			// TODO: корявая имплементация
			return new TempIterableFilePattern(tempPath, CompactNumber.encode(getTempNumber())+'_', suffix);
		}
}