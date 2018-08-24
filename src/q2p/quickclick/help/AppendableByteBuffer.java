package q2p.quickclick.help;

public final class AppendableByteBuffer implements BytesContainer {
	private int startingLength;
	public int getStartingLength() {
		return startingLength;
	}
	public void setStartingLength(final int startingLength) {
		assert startingLength > 0;

		this.startingLength = startingLength;
	}
	private int keepLength;
	public int getKeepLength() {
		return keepLength;
	}
	public void setKeepLength(final int keepLength) {
		assert keepLength > 0;

		this.keepLength = keepLength;
	}
	private int maxCapacity;
	public int getMaxCapacity() {
		return maxCapacity;
	}
	public void setMaxCapacity(final int maxCapacity) {
		assert maxCapacity > 0;

		this.maxCapacity = maxCapacity;
	}

	private byte[] values;
	public byte[] getBytes() {
		return values;
	}
	private int length = 0;
	public int getLength() {
		return length;
	}

	public AppendableByteBuffer(final int startingLength, final int keepLength, final int maxCapacity) {
		assert 0 < startingLength && startingLength <= keepLength && keepLength <= maxCapacity;

		this.startingLength = startingLength;
		this.keepLength = keepLength;
		this.maxCapacity = maxCapacity;
		values = new byte[startingLength];
	}

	public void append(final byte character) {
		assert values != null;

		assert startingLength <= keepLength && keepLength <= maxCapacity;

		assert length + 1 <= maxCapacity;

		if(length + 1 == values.length && values.length != maxCapacity) {
			final byte[] temp = new byte[Math.min(2 * values.length, maxCapacity)];
			System.arraycopy(values, 0, temp, 0, length);
			values = temp;
		}

		values[length++] = character;
	}

	public void reset() {
		assert values != null;

		assert startingLength <= keepLength && keepLength <= maxCapacity;

		if(values.length > keepLength)
			values = new byte[startingLength];

		length = 0;
	}
}