package q2p.quickclick.conversion;

public class RawImageArrayRGB {
	public final int sizeX, sizeY;
	public final byte[] data;

	public RawImageArrayRGB(final int sizeX, final int sizeY) {
		assert sizeX > 0 && sizeY > 0;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.data = new byte[sizeX * sizeY * 3];
	}
}