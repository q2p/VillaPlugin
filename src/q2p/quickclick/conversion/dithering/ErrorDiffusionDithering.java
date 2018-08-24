package q2p.quickclick.conversion.dithering;

import q2p.quickclick.base.Assist;
import q2p.quickclick.conversion.ColorPallete;

final class ErrorDiffusionDithering extends DitheringAlgorithm {
	private final double[][] errorMap;
	private final boolean[][] errorMapUsed;
	private final int mapLeftOffset;
	private final int mapBottomOffset;

	ErrorDiffusionDithering(final String name, final int magnitude, final int mapSizeX, final int[] errorMap) {
		super(name);

		this.errorMap = new double[errorMap.length/mapSizeX][mapSizeX];
		this.errorMapUsed = new boolean[this.errorMap.length][mapSizeX];
		this.mapLeftOffset = mapSizeX / 2;
		this.mapBottomOffset = this.errorMap.length - 1;

		for(int y = 0; y != this.errorMap.length; y++) {
			for(int x = 0; x != mapSizeX; x++) {
				final int value = errorMap[y * mapSizeX + x];
				if(value != 0) {
					errorMapUsed[y][x] = true;
					this.errorMap[y][x] = (double) value / (double) magnitude;
				}
			}
		}
	}

	public void dither(final int sizeX, final int sizeY, final byte[] inputRGB, final byte[] outputIds, final ColorPallete colorPallete) {
		assert sizeX > 0 && sizeY > 0;

		final double[] pixels = new double[3 * sizeX * sizeY];
		for(int i = pixels.length-1; i != -1; i--)
			pixels[i] = 0xFF & inputRGB[i];

		final short[] rgbBuffer = new short[3];

		for(int y = 0; y != sizeY; y++) {
			final int eyMax = Assist.limit(0, sizeY - y - 1, mapBottomOffset) + 1;
			for(int x = 0; x != sizeX; x++) {
				final int exMin = mapLeftOffset - Math.min(mapLeftOffset, x);
				final int exMax = mapLeftOffset + Assist.limit(0, sizeX - x - 1, mapLeftOffset) + 1;

				final int idOffset = y * sizeX + x;
				final int rgbOffset = 3 * idOffset;
				final int oldColorR = Assist.limit(0, Assist.perfectRoundToInt(pixels[rgbOffset  ]), 255);
				final int oldColorG = Assist.limit(0, Assist.perfectRoundToInt(pixels[rgbOffset+1]), 255);
				final int oldColorB = Assist.limit(0, Assist.perfectRoundToInt(pixels[rgbOffset+2]), 255);
				final byte newColorId = colorPallete.RGBtoId(oldColorR, oldColorG, oldColorB);
				outputIds[idOffset] = newColorId;

				colorPallete.idToRGB(newColorId, rgbBuffer);

				final double errorR = oldColorR - rgbBuffer[0];
				final double errorG = oldColorG - rgbBuffer[1];
				final double errorB = oldColorB - rgbBuffer[2];

				for(int ey = 0; ey != eyMax; ey++) {
					final int dy = y + ey;
					for(int ex = exMin; ex != exMax; ex++) {
						if(errorMapUsed[ey][ex]) {
							// 3 * (y * sizeX + x)
							final int off = 3*(dy * sizeX + x + ex - mapLeftOffset);
							pixels[off  ] = pixels[off  ] + errorR * errorMap[ey][ex];
							pixels[off+1] = pixels[off+1] + errorG * errorMap[ey][ex];
							pixels[off+2] = pixels[off+2] + errorB * errorMap[ey][ex];
						}
					}
				}
			}
		}
	}
}
