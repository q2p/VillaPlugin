package q2p.quickclick.conversion;

public final class ColorsConverter {
	public static void extract(final int sizeX, final int sizeY, final byte[] inputIds, final byte[] outputRGB, final ColorPallete colorPallete) {
		final int limit = sizeX*sizeY;
		byte[] buffer = new byte[3];
		int rgbOffset = 3*limit-1;
		int id = limit-1;
		while(id != -1) {
			colorPallete.idToRGB(inputIds[id--], buffer);
			outputRGB[rgbOffset--] = buffer[2];
			outputRGB[rgbOffset--] = buffer[1];
			outputRGB[rgbOffset--] = buffer[0];
		}
	}
}
