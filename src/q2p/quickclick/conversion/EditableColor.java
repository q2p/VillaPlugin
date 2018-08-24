package q2p.quickclick.conversion;

import java.awt.*;
import java.awt.color.ColorSpace;

public class EditableColor extends Color {
	private EditableColor() {
		super(0, 0, 0);
		setR(0);
		setG(0);
		setB(0);
		calcValue();
	}
	public static final EditableColor instance = new EditableColor();

	private static int value = 0xFF000000;
	private static int r = 0, g = 0, b = 0;

	public static void setR(final int value) {
		EditableColor.r = value;
	}

	public static void setG(final int value) {
		EditableColor.g = value;
	}

	public static void setB(final int value) {
		EditableColor.b = value;
	}

	private static void calcValue() {
		value =
		0xFF000000 |
		r << 16 |
		g <<  8 |
		b
		;
	}

	public int getRed() {
		return r;
	}
	public int getGreen() {
		return g;
	}
	public int getBlue() {
		return b;

	}
	public int getAlpha() {
		return 0xFF;
	}

	public int getRGB() {
		return value;
	}


	public Color brighter() {
		return new Color(r, g, b).brighter();
	}
	public Color darker() {
		return new Color(r, g, b).darker();
	}

	public int hashCode() {
		return value;
	}

	public float[] getRGBComponents(float[] compArray) {
		return new Color(value).getRGBComponents(compArray);
	}

	public float[] getRGBColorComponents(float[] compArray) {
		return new Color(value).getRGBColorComponents(compArray);
	}

	public float[] getComponents(float[] compArray) {
		return new Color(value).getComponents(compArray);
	}

	public float[] getColorComponents(float[] compArray) {
		return new Color(value).getColorComponents(compArray);
	}

	public float[] getComponents(ColorSpace cspace, float[] compArray) {
		return new Color(value).getComponents(cspace, compArray);
	}

	public float[] getColorComponents(ColorSpace cspace, float[] compArray) {
		return new Color(value).getColorComponents(cspace, compArray);
	}

	private static final ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
	public ColorSpace getColorSpace() {
		return cs;
	}

	public int getTransparency() {
		return Transparency.OPAQUE;
	}
}
