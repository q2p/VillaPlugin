package q2p.quickclick.base.shorters;

public final class HEX {
	public static final byte encodedMultiplier = 2;

	private static final char[] hexLowCase  = "0123456789abcdef".toCharArray();
	private static final char[] hexHighCase = "0123456789ABCDEF".toCharArray();

	private static final byte[] decodeMap = new byte['f'+1];

	static {
		for(byte i = 'f'; i != -1; i--)
			decodeMap[i] = -1;

		for(byte i = 0; i != 16; i++) {
			decodeMap[hexLowCase [i]] = i;
			decodeMap[hexHighCase[i]] = i;
		}
	}

	public static void encode(final byte[] plain, int plainOffset, final int plainLength, final char[] out, int outOffset) {
		for(final int plainEnd = plainOffset + plainLength; plainOffset != plainEnd;) {
			final byte b = plain[plainOffset++];

			out[outOffset++] = hexLowCase[(0xFF & b) >>> 4];
			out[outOffset++] = hexLowCase[b & 0xF];
		}
	}

	public static void encode(final byte[] plain, int plainOffset, final int plainLength, final StringBuilder out) {
		for(final int plainEnd = plainOffset + plainLength; plainOffset != plainEnd;) {
			final byte b = plain[plainOffset++];

			out.append(hexLowCase[(0xFF & b) >>> 4]);
			out.append(hexLowCase[b & 0xF]);
		}
	}

	public static boolean decode(final char[] encoded, int encodedOffset, final int encodedLength, final byte[] out, int outOffset) {
		if(encodedLength % encodedMultiplier != 0)
			return false;

		final int encodedEnd = encodedOffset + encodedLength;

		while(encodedOffset != encodedEnd) {
			final byte p1 = decode(encoded[encodedOffset++]);
			final byte p2 = decode(encoded[encodedOffset++]);
			if(p1 == -1 || p2 == -1)
				return false;

			out[outOffset++] = (byte)(p1 << 4 | p2);
		}

		return true;
	}

	public static boolean decode(final CharSequence encoded, int encodedOffset, final int encodedLength, final byte[] out, int outOffset) {
		if(encodedLength % encodedMultiplier != 0)
			return false;

		final int encodedEnd = encodedOffset + encodedLength;

		while(encodedOffset != encodedEnd) {
			final byte p1 = decode(encoded.charAt(encodedOffset++));
			final byte p2 = decode(encoded.charAt(encodedOffset++));
			if(p1 == -1 || p2 == -1)
				return false;

			out[outOffset++] = (byte)(p1 << 4 | p2);
		}

		return true;
	}

	public static void decodeUnsafe(final char[] encoded, int encodedOffset, final int encodedLength, final byte[] out, int outOffset) {
		final int encodedEnd = encodedOffset + encodedLength;

		while(encodedOffset != encodedEnd)
			out[outOffset++] = (byte)(decodeMap[encoded[encodedOffset++]] << 4 | decodeMap[encoded[encodedOffset++]]);
	}

	public static void decodeUnsafe(final CharSequence encoded, int encodedOffset, final int encodedLength, final byte[] out, int outOffset) {
		final int encodedEnd = encodedOffset + encodedLength;

		while(encodedOffset != encodedEnd)
			out[outOffset++] = (byte)(decodeMap[encoded.charAt(encodedOffset++)] << 4 | decodeMap[encoded.charAt(encodedOffset++)]);
	}

	/** @return {@code -1} если символ не явяется HEX числом, иначе значение числа. */
	public static byte decode(final byte character) {
		return character > 'f' ? -1 : decodeMap[character];
	}
	/** @return {@code -1} если символ не явяется HEX числом, иначе значение числа. */
	public static byte decode(final char character) {
		return character > 'f' ? -1 : decodeMap[character];
	}
}