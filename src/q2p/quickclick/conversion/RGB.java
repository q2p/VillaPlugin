package q2p.quickclick.conversion;

import q2p.quickclick.base.shorters.HEX;

public final class RGB {
	public final short r;
	public final short g;
	public final short b;
	public final String hex;

	public RGB(final int r, final int g, final int b) {
		assert r >= 0 && r <= 255;
		assert g >= 0 && g <= 255;
		assert b >= 0 && b <= 255;

		this.r = (short) r;
		this.g = (short) g;
		this.b = (short) b;

		char[] ch = new char[6];
		HEX.encode(new byte[] {(byte) r, (byte) g, (byte) b},0, 3,  ch, 0);
		hex = new String(ch);
	}
}
