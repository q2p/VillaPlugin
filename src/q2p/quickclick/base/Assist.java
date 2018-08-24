package q2p.quickclick.base;

import java.util.*;

public final class Assist {
	/** @return {@code length >= 0 && offset >= 0 && array != null && offset + length <= array.length} */
	public static boolean checkArrayBounds(final byte[] array, final int offset, final int length) {
		return length >= 0 && offset >= 0 && array != null && offset + length <= array.length;
	}
	
	public static final int maxNetworkPortValue = 65535;

	public static final String alpha = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	public static final byte alphaLength = (byte)alpha.length();

	private static final Random random = new Random();
	public static synchronized int random(final int bound) {
		assert bound >= 1;
		return random.nextInt(bound);
	}

	public static int perfectPositiveCeil(final int number, final int divisor) {
		assert number >= 0 && divisor > 0;
		return number / divisor + (number % divisor == 0 ? 0 : 1);
	}

	public static long perfectPositiveCeil(final long number, final long divisor) {
		assert number >= 0 && divisor > 0;
		return number / divisor + (number % divisor == 0 ? 0 : 1);
	}

	public static long perfectCeil(final double value) {
		return (long)(Math.ceil(value) + 0.2);
	}

	public static long limit(final long min, final long value, final long max) {
		if(value < min)
			return min;

		if(value > max)
			return max;

		return value;
	}
	public static int limit(final int min, final int value, final int max) {
		if(value < min)
			return min;

		if(value > max)
			return max;

		return value;
	}

	public static int perfectRoundToInt(final double value) {
		return (int)(Math.round(value)+0.45);
	}
}