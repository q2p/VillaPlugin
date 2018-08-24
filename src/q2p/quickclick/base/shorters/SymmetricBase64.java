package q2p.quickclick.base.shorters;

public final class SymmetricBase64 {
/*
	+--first octet--+-second octet--+--third octet--+
  	|7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|
  	+-----------+---+-------+-------+---+-----------+
  	|5 4 3 2 1 0|5 4 3 2 1 0|5 4 3 2 1 0|5 4 3 2 1 0|
  	+--1.index--+--2.index--+--3.index--+--4.index--+
  	
  	+--1.index--+--2.index--+--3.index--+--4.index--+
  	|5 4 3 2 1 0|5 4 3 2 1 0|5 4 3 2 1 0|5 4 3 2 1 0|
  	+-----------+---+-------+-------+---+-----------+
  	|7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|
	+--first octet--+-second octet--+--third octet--+
*/
	
	public static final byte plainMultiplier = 3;
	public static final byte encodedMultiplier = 4;
	
	private static final char[] symbols = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_".toCharArray();
	
	private static final byte[] decodeMap = new byte['z'+1];
	
	static {
		for(byte i = 'z'; i != -1; i--)
			decodeMap[i] = -1;
		
		for(byte i = 0; i != 64; i++)
			decodeMap[symbols[i]] = i;
	}
	
	public static boolean encode(final byte[] plain, int plainOffset, final int plainLength, final char[] out, int outOffset) {
		if(plainLength % plainMultiplier != 0)
			return false;
		
		final int plainEnd = plainOffset + plainLength;
		
		while(plainOffset != plainEnd) {
			final byte b1 = plain[plainOffset++];
			final byte b2 = plain[plainOffset++];
			final byte b3 = plain[plainOffset++];
			
			out[outOffset++] = symbols[0x3F & (          (0xFF & b1) >>> 2)];
			out[outOffset++] = symbols[0x3F & (b1 << 4 | (0xFF & b2) >>> 4)];
			out[outOffset++] = symbols[0x3F & (b2 << 2 | (0xFF & b3) >>> 6)];
			out[outOffset++] = symbols[0x3F & (b3                         )];
		}
		
		return true;
	}
	
	public static boolean encode(final byte[] plain, int plainOffset, final int plainLength, final StringBuilder builder) {
		if(plainLength % plainMultiplier != 0)
			return false;
		
		final int plainEnd = plainOffset + plainLength;
		
		while(plainOffset != plainEnd) {
			final byte b1 = plain[plainOffset++];
			final byte b2 = plain[plainOffset++];
			final byte b3 = plain[plainOffset++];
			
			builder.append(symbols[0x3F & (          (0xFF & b1) >>> 2)]);
			builder.append(symbols[0x3F & (b1 << 4 | (0xFF & b2) >>> 4)]);
			builder.append(symbols[0x3F & (b2 << 2 | (0xFF & b3) >>> 6)]);
			builder.append(symbols[0x3F & (b3                         )]);
		}
		
		return true;
	}
	
	public static boolean decode(final char[] encoded, int encodedOffset, final int encodedLength, final byte[] out, int outOffset) {
		if(encodedLength % encodedMultiplier != 0)
			return false;
		
		final int encodedEnd = encodedOffset + encodedLength;
		
		while(encodedOffset != encodedEnd) {
			final char c1 = encoded[encodedOffset++];
			final char c2 = encoded[encodedOffset++];
			final char c3 = encoded[encodedOffset++];
			final char c4 = encoded[encodedOffset++];
			if(c1 > 'z' || c2 > 'z' || c3 > 'z' || c4 > 'z')
				return false;
			final byte p1 = decodeMap[c1];
			final byte p2 = decodeMap[c2];
			final byte p3 = decodeMap[c3];
			final byte p4 = decodeMap[c4];
			if(p1 == -1 || p2 == -1 || p3 == -1 || p4 == -1)
				return false;
			
			out[outOffset++] = (byte)(p1 << 2 | (0x3F & p2) >>> 4);
			out[outOffset++] = (byte)(p2 << 4 | (0x3F & p3) >>> 2);
			out[outOffset++] = (byte)(p3 << 6 |         p4       );
		}
		
		return true;
	}
}