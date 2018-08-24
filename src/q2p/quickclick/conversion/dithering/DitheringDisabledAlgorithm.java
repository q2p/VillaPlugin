package q2p.quickclick.conversion.dithering;

import q2p.quickclick.conversion.ColorPallete;

final class DitheringDisabledAlgorithm extends DitheringAlgorithm {
	DitheringDisabledAlgorithm() {
		super("none");
	}

	public void dither(final int sizeX, final int sizeY, final byte[] inputRGB, final byte[] outputIds, final ColorPallete colorPallete) {
		int offsetIds = 0;
		int offsetRGB = 0;
		while(offsetRGB != inputRGB.length)
			outputIds[offsetIds++] = colorPallete.RGBtoId(0xFF & inputRGB[offsetRGB++], 0xFF & inputRGB[offsetRGB++], 0xFF & inputRGB[offsetRGB++]);
	}
}