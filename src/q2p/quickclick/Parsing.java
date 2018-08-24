package q2p.quickclick;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Parsing {
	public static void forEachItem(final String string, final char separator, final Consumer<String> action) {
		int idx1 = 0;
		int idx2;
		while(true) {
			idx2 = string.indexOf(separator, idx1);
			if(idx2 == -1) {
				idx2 = string.length();
				action.accept(string.substring(idx1, idx2));
				return;
			} else {
				action.accept(string.substring(idx1, idx2));
				idx1 = idx2+1;
			}
		}
	}
	public static LinkedList<String> splitLinesAndTrim(final String string) {
		final LinkedList<String> ret = new LinkedList<>();
		forEachItem(string, '\n', (String line) -> ret.add(line.trim()));
		return ret;
	}
	public static <T> boolean fillContainer(final String string, final char separator, final T[] output, final Function<String, T> conversion) {
		int idx1 = 0;
		int idx2;
		int i = 0;
		while(true) {
			idx2 = string.indexOf(separator, idx1);
			if (idx2 == -1)
				idx2 = string.length();

			T out = conversion.apply(string.substring(idx1, idx2));
			if(out == null)
				return false;

			output[i++] = out;

			if(idx2 == string.length())
				return i == output.length;

			if(i == output.length)
				return false;

			idx1 = idx2+1;
		}
	}
}