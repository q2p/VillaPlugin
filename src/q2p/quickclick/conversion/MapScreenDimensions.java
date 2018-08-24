package q2p.quickclick.conversion;

public final class MapScreenDimensions {
	private static final short mapSize = 128;

	public final Resolution mapsAmount;

	public final Resolution renderSize;

	public MapScreenDimensions(final int mapsWidth, final int mapsHeight) {
		this.mapsAmount = new Resolution(mapsWidth, mapsHeight);
		this.renderSize = new Resolution(mapsWidth*mapSize, mapsHeight*mapSize);
	}
}