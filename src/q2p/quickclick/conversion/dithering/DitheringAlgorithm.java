package q2p.quickclick.conversion.dithering;

import q2p.quickclick.conversion.ColorPallete;

public abstract class DitheringAlgorithm {
	public static final DitheringAlgorithm disabled = new DitheringDisabledAlgorithm();
	private static final DitheringAlgorithm[] ditheringAlgorithms = {
		disabled,
		new ErrorDiffusionDithering("sides_dithering", 2, 3, new int[] {
			0, 0, 1,
			0, 1, 0
		}),
		new ErrorDiffusionDithering("corner_dithering", 2, 3, new int[] {
			0, 0, 1,
			0, 1, 1
		}),
		new ErrorDiffusionDithering("floyd_steinberg_dithering", 16, 3, new int[] {
			0, 0, 7,
			3, 5, 1
		}),
		new ErrorDiffusionDithering("jarvis_judice_ninke_dithering", 48, 5, new int[] {
			0, 0, 0, 7, 5,
			3, 5, 7, 5, 3,
			1, 3, 5, 3, 1
		}),
		new ErrorDiffusionDithering("sierra_2", 16, 5, new int[] {
			0, 0, 0, 4, 3,
			1, 2, 3, 2, 1
		}),
		new ErrorDiffusionDithering("sierra_2_4a", 4, 3, new int[] {
			0, 0, 2,
			1, 1, 0
		}),
		new ErrorDiffusionDithering("sierra_3", 32, 5, new int[] {
			0, 0, 0, 5, 3,
			2, 4, 5, 4, 2,
			0, 2, 3, 2, 0
		}),
		new ErrorDiffusionDithering("stucki", 42, 5, new int[] {
			0, 0, 0, 8, 4,
			2, 4, 8, 4, 2,
			1, 2, 4, 2, 1
		}),
		new ErrorDiffusionDithering("burkes", 32, 5, new int[] {
			0, 0, 0, 8, 4,
			2, 4, 8, 4, 2
		}),
		new ErrorDiffusionDithering("stevenson-arce", 200, 7, new int[] {
			 0,  0,  0,  0,  0, 32,  0,
			12,  0, 26,  0, 30,  0, 16,
			 0, 12,  0, 26,  0, 12,  0,
			 5,  0, 12,  0, 12,  0,  5
		}),
		new ErrorDiffusionDithering("atkinson", 8, 5, new int[] {
			0, 0, 0, 1, 1,
			0, 1, 1, 1, 0,
			0, 0, 1, 0, 0
		})
	};
	public static final DitheringAlgorithm defaultDithering = getByName("burkes");

	private static byte uidCounter = 0;
	public final byte uid = uidCounter++;
	public final String name;

	DitheringAlgorithm(final String name) {
		this.name = name;
	}

	public static DitheringAlgorithm getByName(String name) {
		name = name.toLowerCase();
		for(final DitheringAlgorithm algorithm : ditheringAlgorithms)
			if(algorithm.name.toLowerCase().equals(name))
				return algorithm;

		return null;
	}

	public abstract void dither(final int sizeX, final int sizeY, final byte[] inputRGB, final byte[] outputIds, final ColorPallete colorPallete);
}