package q2p.quickclick.help;

public final class NumberParser {
	public static Long parse(final String string, final boolean allowNegativeSign, final boolean allowPositiveSign, final long min, final long max) {
		if(string == null || string.length() == 0)
			return null;

		final char c = string.charAt(0);

		if(c == '-' && !allowNegativeSign || c == '+' && !allowPositiveSign)
			return null;

		final long temp;
		try {
			temp = Long.parseLong(string);
		} catch(final NumberFormatException e) {
			return null;
		}

		if(temp < min || temp > max)
			return null;

		return temp;
	}
}