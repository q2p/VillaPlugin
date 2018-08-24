package q2p.quickclick.conversion;

public interface ColorPallete {
	void idToRGB(final byte id, final short[] output);
	void idToRGB(final byte id, final byte[] output);
	byte RGBtoId(final int r, final int g, final int b);
}