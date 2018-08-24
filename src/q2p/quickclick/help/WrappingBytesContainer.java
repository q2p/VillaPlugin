package q2p.quickclick.help;

public class WrappingBytesContainer implements BytesContainer {
	public final byte[] bytes;
	public final int length;

	public WrappingBytesContainer(final byte[] bytes) {
		this.bytes = bytes;
		length = bytes.length;
	}

	public WrappingBytesContainer(final byte[] bytes, final int length) {
		this.bytes = bytes;
		this.length = length;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public int getLength() {
		return length;
	}
}
