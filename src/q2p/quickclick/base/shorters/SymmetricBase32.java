package q2p.quickclick.base.shorters;

public final class SymmetricBase32 {
	/*
		+--first octet--+-second octet--+--third octet--+-fourth octet--+--fifth octet--+
  	|7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|
  	+---------+-----+---+---------+-+---------------+-+---------+---+-----+---------+
  	|4 3 2 1 0|4 3 2 1 0|4 3 2 1 0|4 3 2 1 0|4 3 2 1 0|4 3 2 1 0|4 3 2 1 0|4 3 2 1 0|
  	+-1.index-+-2.index-+-3.index-+-4.index-+-5.index-+-6.index-+-7.index-+-8.index-+
  	
  	+-1.index-+-2.index-+-3.index-+-4.index-+-5.index-+-6.index-+-7.index-+-8.index-+
  	|4 3 2 1 0|4 3 2 1 0|4 3 2 1 0|4 3 2 1 0|4 3 2 1 0|4 3 2 1 0|4 3 2 1 0|4 3 2 1 0|
  	+---------+-----+---+---------+-+---------------+-+---------+---+-----+---------+
  	|7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|
		+--first octet--+-second octet--+--third octet--+-fourth octet--+--fifth octet--+
	*/
	
	public static final byte plainMultiplier = 5;
	public static final byte encodedMultiplier = 8;
	
	private static final char[] symbols = "ybndrfg8ejkmcpqxot1uwisza345h769".toCharArray();
	
	private static final byte[] decodeMap = new byte['z'+1];
	
	static {
		for(byte i = 'z'; i != -1; i--)
			decodeMap[i] = -1;
		
		for(byte i = 0; i != 32; i++)
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
			final byte b4 = plain[plainOffset++];
			final byte b5 = plain[plainOffset++];
			
			out[outOffset++] = symbols[0x1F & (          (0xFF & b1) >>> 3)];
			out[outOffset++] = symbols[0x1F & (b1 << 2 | (0xFF & b2) >>> 6)];
			out[outOffset++] = symbols[0x1F & (          (0xFF & b2) >>> 1)];
			out[outOffset++] = symbols[0x1F & (b2 << 4 | (0xFF & b3) >>> 4)];
			out[outOffset++] = symbols[0x1F & (b3 << 1 | (0xFF & b4) >>> 7)];
			out[outOffset++] = symbols[0x1F & (          (0xFF & b4) >>> 2)];
			out[outOffset++] = symbols[0x1F & (b4 << 3 | (0xFF & b5) >>> 5)];
			out[outOffset++] = symbols[0x1F & (b5                         )];
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
			final byte b4 = plain[plainOffset++];
			final byte b5 = plain[plainOffset++];
			
			builder.append(symbols[0x1F & (          (0xFF & b1) >>> 3)]);
			builder.append(symbols[0x1F & (b1 << 2 | (0xFF & b2) >>> 6)]);
			builder.append(symbols[0x1F & (          (0xFF & b2) >>> 1)]);
			builder.append(symbols[0x1F & (b2 << 4 | (0xFF & b3) >>> 4)]);
			builder.append(symbols[0x1F & (b3 << 1 | (0xFF & b4) >>> 7)]);
			builder.append(symbols[0x1F & (          (0xFF & b4) >>> 2)]);
			builder.append(symbols[0x1F & (b4 << 3 | (0xFF & b5) >>> 5)]);
			builder.append(symbols[0x1F & (b5                         )]);
		}
		
		return true;
	}
	
	public static boolean decode(final char[] encoded, int encodedOffset, final int encodedLength, final byte[] out, int outOffset) {
		if(encodedLength % encodedMultiplier != 0)
			return false;
		
		final int encodedEnd = encodedOffset + encodedLength;
		
		while(encodedOffset != encodedEnd) {
			final char c1 = encoded[encodedOffset++], c2 = encoded[encodedOffset++];
			final char c3 = encoded[encodedOffset++], c4 = encoded[encodedOffset++];
			final char c5 = encoded[encodedOffset++], c6 = encoded[encodedOffset++];
			final char c7 = encoded[encodedOffset++], c8 = encoded[encodedOffset++];
			if(
				c1 > 'z' || c2 > 'z' || c3 > 'z' || c4 > 'z' ||
					c5 > 'z' || c6 > 'z' || c7 > 'z' || c8 > 'z'
				) return false;
			final byte p1 = decodeMap[c1], p2 = decodeMap[c2];
			final byte p3 = decodeMap[c3], p4 = decodeMap[c4];
			final byte p5 = decodeMap[c5], p6 = decodeMap[c6];
			final byte p7 = decodeMap[c7], p8 = decodeMap[c8];
			if(
				p1 == -1 || p2 == -1 || p3 == -1 || p4 == -1 ||
				p5 == -1 || p6 == -1 || p7 == -1 || p8 == -1
			) return false;
			
			out[outOffset++] = (byte)(p1 << 3 |           (0x1F & p2) >>> 2);
			out[outOffset++] = (byte)(p2 << 6 | p3 << 1 | (0x1F & p4) >>> 4);
			out[outOffset++] = (byte)(p4 << 4 |           (0x1F & p5) >>> 1);
			out[outOffset++] = (byte)(p5 << 7 | p6 << 2 | (0x1F & p7) >>> 3);
			out[outOffset++] = (byte)(p7 << 5 | p8                         );
		}
		
		return true;
	}
}