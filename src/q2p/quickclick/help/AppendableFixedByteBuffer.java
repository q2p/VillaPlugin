package q2p.quickclick.help;

public final class AppendableFixedByteBuffer implements BytesContainer {
	private final byte[] values;
	public byte[] getBytes() {
		return values;
	}
	public int getCapacity() {
		return values.length;
	}

	private int length = 0;
	public int getLength() {
		return length;
	}
	public boolean isFilled() {
		return values.length == length;
	}

	public AppendableFixedByteBuffer(final int capacity) {
		assert capacity > 0;

		values = new byte[capacity];
	}

	public void append(final byte character) {
		assert length + 1 <= values.length;

		values[length++] = character;
	}

	public void reset() {
		length = 0;
	}
}