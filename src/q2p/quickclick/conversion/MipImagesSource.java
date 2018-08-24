package q2p.quickclick.conversion;

public final class MipImagesSource extends MipsSource {
	public final String path;

	public MipImagesSource(String path) {
		this.path = path.replace('\\', '/');
	}

	public int getLength() {
		return 1;
	}
}