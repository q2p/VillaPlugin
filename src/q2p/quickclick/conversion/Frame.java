package q2p.quickclick.conversion;

public final class Frame {
	public final Resolution resolution;
	public final RGB backgroundColor;
	public final byte[] rgbData;

	public Frame(final Resolution resolution, final RGB backgroundColor, final byte[] rgbData) {
		this.resolution = resolution;
		this.backgroundColor = backgroundColor;
		this.rgbData = rgbData;
	}
}