package q2p.quickclick.base.operatingsystem;

import java.nio.file.*;

public final class SafePath {
	public static final String safeSymbols = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_";

	private static final boolean[] nameTable = new boolean['z'+1];
	static {
		setDistance(nameTable, '0', '9');
		setDistance(nameTable, 'A', 'Z');
		setDistance(nameTable, 'a', 'z');
		nameTable['_'] = true;

		assert isNameSafe(safeSymbols);
	}
	private static final boolean[] driveLetters = new boolean['z'+1];
	static {
		setDistance(driveLetters, 'A', 'Z');
	}
	private static void setDistance(final boolean[] table, int from, final int to) {
		assert from >= 0 && from <= to && to < table.length;
		while(from <= to) {
			assert !table[from];
			table[from] = true;
			from++;
		}
	}

	private static boolean isNotAllowed(final boolean[] table, final char ch) {
		assert table != null;
		return ch > 'z' || !table[ch];
	}
	public static boolean isNameSafe(final CharSequence path) {
		assert path != null;
		final int length = path.length();
		if(length == 0)
			return false;

		for(int i = 0; i != length; i++)
			if(isNotAllowed(nameTable, path.charAt(i)))
				return false;

		return true;
	}
	public static boolean isAbsolutePathSafe(final CharSequence path) {
		assert path != null;
		final int length = path.length();
		int offset;
		if(OperatingSystem.getHostingOS() == OperatingSystem.Windows) {
			if(length < 3 || isNotAllowed(driveLetters, path.charAt(0)) || path.charAt(1) != ':' || path.charAt(2) != '/')
				return false;

			offset = 3;
		} else {
			if(length == 0 || path.charAt(0) != '/')
				return false;

			offset = 1;
		}

		boolean prevSlash = false;
		while(offset != length) {
			final char c = path.charAt(offset++);

			if(c == '/') {
				if(prevSlash)
					return false;
				prevSlash = true;
				continue;
			} else {
				prevSlash = false;
			}

			if(isNotAllowed(nameTable, c))
				return false;
		}

		return true;
	}

	/** @return Абсолютный путь в виде строки или {@code null}, если путь не является безопасным. */
	public static String toSafeAbsolutePath(final Path path) {
		assert path != null;

		try {
			String pathString = path.normalize().toString();

			if(OperatingSystem.getHostingOS() == OperatingSystem.Windows)
				pathString = pathString.replace('\\', '/');

			if(!isAbsolutePathSafe(pathString))
				return null;

			return pathString;
		} catch(final Throwable t) {
			return null;
		}
	}
}