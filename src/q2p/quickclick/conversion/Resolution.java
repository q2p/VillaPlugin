package q2p.quickclick.conversion;

public class Resolution implements Comparable<Resolution> {
	private final int width;
	private final int height;
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	private final int space;

	public Resolution(final int width, final int height) {
		assert width > 0;
		assert height> 0;

		this.width = width;
		this.height = height;
		this.space = width*height;
	}

	/** @inheritDoc */
	public int compareTo(final Resolution to) {
		return space - to.space;
	}

	/** Сравнивает площадь двух разрешений */
	public boolean same(final Resolution other) {
		return other.width == width && other.height == height;
	}

	public double getZoomOf(final Resolution original) {
		if(original.width > original.height)
			return (double) width  / (double) original.width;
		else
			return (double) height / (double) original.height;
	}

	public int space() {
		return space;
	}

	/** @return отрицательное число если then шире, 0 если одинаковые, положительное число если then уже */
	public int compareAspects(final Resolution then) {
		return width * then.height - then.width * height;
	}

	public static ResizeTransformation getTransformation(final Resolution from, final Resolution to) {
		if(from.width == to.width) {
			if(from.height == to.height) {
				return ResizeTransformation.None;
			} else if(from.height > to.height) {
				return ResizeTransformation.Downscaling;
			} else {
				return ResizeTransformation.Upscaling;
			}
		} else if(from.width > to.width) {
			return ResizeTransformation.Downscaling;
		} else {
			return ResizeTransformation.Upscaling;
		}
	}

	public enum ResizeTransformation {
		Downscaling, None, Upscaling
	}
}