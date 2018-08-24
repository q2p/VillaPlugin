package q2p.quickclick.base.shorters;

import q2p.quickclick.base.*;

public final class CompactNumber {
	public static final char[] symbols = Assist.alpha.toCharArray();
	
	private static final byte base = (byte)symbols.length;
	
	private static final byte[] decodeMap = new byte['z'+1];
	
	private static final char[] maxCompact;
	
	static {
		// Decode Map
		for(byte i = 'z'; i != -1; i--)
			decodeMap[i] = -1;
		
		for(byte i = (byte)(base-1); i != -1; i--)
			decodeMap[symbols[i]] = i;
		
		// Max Value
		long binary = Long.MAX_VALUE;
		
		final StringBuilder builder = new StringBuilder();
		do {
			builder.append(symbols[(int) (binary % base)]);
			binary /= base;
		} while(binary != 0);
		
		maxCompact = builder.reverse().toString().toCharArray();
	}
	
	public static final int longestCompact = maxCompact.length;
	
	public static Long decode(final byte[] compact, final int offset, final int length) {
		long binary = 0;
		
		if(length > longestCompact || length == 0)
			return null;
		
		long power = 1;
		
		int end = offset + length;
		do {
			final byte c = compact[--end];
			if(c > 'z')
				return null;
			final byte magnitude = decodeMap[c];
			if(magnitude == -1)
				return null;
			
			try {
				binary = Math.addExact(binary, magnitude * power);
				power = Math.multiplyExact(base, power);
			} catch(final ArithmeticException e) {
				return null;
			}
		} while(end != offset);
		
		return binary;
	}
	
	public static Long decode(final CharSequence compact, final int offset, final int length) {
		long binary = 0;
		
		if(length > longestCompact || length == 0)
			return null;
		
		long power = 1;
		
		int end = offset + length;
		do {
			final char c = compact.charAt(--end);
			if(c > 'z')
				return null;
			final byte magnitude = decodeMap[c];
			if(magnitude == -1)
				return null;
			
			try {
				binary = Math.addExact(binary, magnitude * power);
				power = Math.multiplyExact(base, power);
			} catch(final ArithmeticException e) {
				return null;
			}
		} while(end != offset);
		
		return binary;
	}
	
	public static String encode(long binary) {
		if(binary < 0)
			return null;
		
		final StringBuilder builder = new StringBuilder(longestCompact);
		do {
			builder.append(symbols[(int) (binary % base)]);
			binary /= base;
		} while(binary != 0);
		
		return builder.reverse().toString();
	}
}