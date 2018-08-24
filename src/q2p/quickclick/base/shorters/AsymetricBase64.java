package q2p.quickclick.base.shorters;

import q2p.quickclick.base.*;

public final class AsymetricBase64 {
	private static final byte[] symbols = Coding.toUTF("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_");

	public static int encode(final byte[] plain, int plainOffset, final int plainLength, final byte[] out, int outOffset) {
		assert out   != null && outOffset   + Assist.perfectPositiveCeil(plainLength, 3) * 4 < out.length;
		assert plain != null && plainOffset + plainLength                                    < plain.length;

		final int modulus = plainLength % 3;
		final int dataLength = plainLength - modulus;

		final int plainEnd = plainOffset + dataLength;
		while(plainOffset != plainEnd) {
			final byte b1 = plain[plainOffset++];
			final byte b2 = plain[plainOffset++];
			final byte b3 = plain[plainOffset++];

			out[outOffset++] = symbols[0x3F & (          (0xFF & b1) >>> 2)];
			out[outOffset++] = symbols[0x3F & (b1 << 4 | (0xFF & b2) >>> 4)];
			out[outOffset++] = symbols[0x3F & (b2 << 2 | (0xFF & b3) >>> 6)];
			out[outOffset++] = symbols[0x3F & (b3                         )];
		}

		final int b1, b2;

		switch(modulus) {
			case 1:
				b1 = plain[plainOffset] & 0xFF;

				out[outOffset    ] = symbols[0x3F & ((0xFF & b1) >>> 2)];
				out[outOffset + 1] = symbols[0x3F & (        b1  <<  4)];
				out[outOffset + 2] = '=';
				out[outOffset + 3] = '=';
				break;
			case 2:
				b1 = plain[plainOffset    ] & 0xFF;
				b2 = plain[plainOffset + 1] & 0xFF;

				out[outOffset    ] = symbols[0x3F & (          (0xFF & b1) >>> 2)];
				out[outOffset + 1] = symbols[0x3F & (b1 << 4 | (0xFF & b2) >>> 4)];
				out[outOffset + 2] = symbols[0x3F & (b2 << 2                    )];
				out[outOffset + 3] = '=';
				break;
		}

		return dataLength / 3 * 4 + (modulus == 0 ? 0 : 4);
	}
}