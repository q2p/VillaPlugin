package q2p.quickclick.conversion;


import org.bukkit.command.CommandSender;
import q2p.quickclick.Log;
import q2p.quickclick.MainSerializer;
import q2p.quickclick.help.DisplayableException;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class InputSource {
	public final String name;
	public final Resolution resolution;
	private final HasAlpha hasAlpha;
	public final HasAlpha hasAlpha() {
		return hasAlpha;
	}
	private final int approximateDuration;
	private FrameRate frameRate;
	public FrameRate getFrameRate() {
		return frameRate;
	}
	private final LinkedList<Period> periods = new LinkedList<>();

	private Path rawSourcePath = null;
	public Path getRawSourcePath() {
		return rawSourcePath;
	}

	private final Path[] imageSequence;
	public Path[] getImageSequence() {
		return imageSequence;
	}

	public boolean isImageSequence() {
		return imageSequence != null;
	}

	InputSource(String name, final Path sourcePath) throws DisplayableException {
		this.name = name;
		imageSequence = null;
		rawSourcePath = sourcePath;
		FFProbeResult result = FFProbe.execute(sourcePath, true);
		if(result.hasFailed())
			throw new DisplayableException("// TODO: ", null);

		this.resolution = result.getResolution();
		this.frameRate = result.getFrameRate();
		this.approximateDuration = result.getDuration();
		this.hasAlpha = result.hasAlpha() ? HasAlpha.Transparent : HasAlpha.Opaque;
	}

	InputSource(String name, final Path[] imageSequence) throws DisplayableException {
		this.name = name;
		this.imageSequence = imageSequence;
		approximateDuration = -1;
		frameRate = FrameRate.defaultFrameRate;
		hasAlpha = HasAlpha.DependsOnImageSource;
		FFProbeResult result = FFProbe.execute(imageSequence[0], false);
		if(result.hasFailed())
			throw new DisplayableException("Не удалось загрузить изображение по адресу: // TODO: ", null);

		this.resolution = result.getResolution();
	}

	public static void importSingleFile(final WorkStation workStation, final String name, final String path, final CommandSender sender) throws DisplayableException {
		Path sourcePath = MainSerializer.resourcesPath().resolve("input").toAbsolutePath().resolve(path).toAbsolutePath();

		if(Files.isDirectory(sourcePath)) {
			sender.sendMessage("Указан путь к директории // TODO:");
			return;
		}

		workStation.addSourceIfAbsent(name, sourcePath);
	}

	public static void importFolderRecursively(final WorkStation workStation, final String path,  final CommandSender sender) throws DisplayableException {
		Path sourcePath = MainSerializer.resourcesPath().resolve("input").toAbsolutePath().resolve(path).toAbsolutePath();

		LinkedList<Path> checkFolders = new LinkedList<>();
		if(!Files.isDirectory(sourcePath)) {
			sender.sendMessage("Указан путь не к директории // TODO:");
			return;
		}

		checkFolders.addLast(sourcePath);

		while(!checkFolders.isEmpty()) {
			try(final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(checkFolders.removeFirst())) {
				for(final Path element : directoryStream) {
					if(Files.isDirectory(element))
						checkFolders.addLast(element);
					else
						workStation.addSourceIfAbsent(element.getFileName().toString(), element.toAbsolutePath());
				}
			} catch (final Throwable t) {
				sender.sendMessage("Не удалось загрузить файлы из // TODO:\n" + Log.formatThrowable(t));
			}
		}
	}

	public static void importImageSequence(final WorkStation workStation, String fullPath, final int from, final int to, final CommandSender sender) throws DisplayableException {
		fullPath = fullPath.replace('\\', '/');
		int folderEndIdx = fullPath.lastIndexOf('/');
		final Path folderPath;
		final String filePrefix;
		if(folderEndIdx == -1) {
			folderPath = inputDirectory.resolve(fullPath);
			filePrefix = "";
		} else {
			folderPath = inputDirectory.resolve(fullPath.substring(0, folderEndIdx));
			filePrefix = fullPath.substring(folderEndIdx+1);
		}

		Path[] framesPaths = getSequence(folderPath, filePrefix, "", 0, to);

		workStation.addImageSequenceIfAbsent(fullPath, folderPath, framesPaths, sender);
	}

	public static Path[] getSequence(final Path folder, final String prefix, final String suffix, final int from, final int to) throws DisplayableException {
		Path[] framesPaths = null;
		if(to != -1)
			framesPaths = new Path[to-from+1];

		final TreeMap<Integer, Path> ret = new TreeMap<>();
		try(final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folder)) {
			for(final Path path : directoryStream) {
				final String fileName = path.getFileName().toString();

				if(!fileName.startsWith(prefix) || !fileName.endsWith(suffix))
					continue;

				int end = prefix.length();
				while(end != fileName.length() && "0123456789".indexOf(fileName.charAt(end)) != -1)
					end++;

				if(end == prefix.length())
					continue;

				int id;
				try {
					id = Integer.parseUnsignedInt(fileName.substring(prefix.length(), end));
				} catch (final NumberFormatException e) {
					continue;
				}

				if(id < from || (to != -1 && id > to))
					continue;

				id -= from;

				if(to == -1) {
					ret.putIfAbsent(id, path.toAbsolutePath());
				} else {
					if(framesPaths[id] != null)
						framesPaths[id] = path.toAbsolutePath();
				}
			}
		} catch (final Throwable ignore) {}

		if(to == -1) {
			final Path[] nret = new Path[ret.size()];
			ret.forEach((id, path) -> {
				try {
					nret[id] = path;
				} catch (final ArrayIndexOutOfBoundsException ignore) {}
			});
			framesPaths = nret;
		}

		for(final Path path : framesPaths)
			if(path == null)
				throw new DisplayableException("// TODO: not all paths are found", null);

		return framesPaths;
	}

	private static final Path inputDirectory = MainSerializer.resourcesPath().resolve("input").toAbsolutePath();

	public void forEachPeriod(final Consumer<Period> periodConsumer) {
		if(periods.isEmpty()) {
			periodConsumer.accept(Period.wholeThing());
		} else {
			for(final Period period : periods)
				periodConsumer.accept(period);
		}
	}
}