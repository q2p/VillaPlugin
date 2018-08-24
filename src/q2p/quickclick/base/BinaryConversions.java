package q2p.quickclick.base;

public final class BinaryConversions {
	public static long getLong(final byte[] byteArray, final int offset) {
		return (
			((0xffL & byteArray[offset    ]) << 56) |
			((0xffL & byteArray[offset + 1]) << 48) |
			((0xffL & byteArray[offset + 2]) << 40) |
			((0xffL & byteArray[offset + 3]) << 32) |
			((0xffL & byteArray[offset + 4]) << 24) |
			((0xffL & byteArray[offset + 5]) << 16) |
			((0xffL & byteArray[offset + 6]) <<  8) |
			((0xffL & byteArray[offset + 7])      )
		);
	}
	public static long getLong(final byte[] byteArray) {
		return (
			((0xffL & byteArray[0]) << 56) |
			((0xffL & byteArray[1]) << 48) |
			((0xffL & byteArray[2]) << 40) |
			((0xffL & byteArray[3]) << 32) |
			((0xffL & byteArray[4]) << 24) |
			((0xffL & byteArray[5]) << 16) |
			((0xffL & byteArray[6]) <<  8) |
			((0xffL & byteArray[7])      )
		);
	}
	public static void putLong(final long value, final byte[] output) {
		output[0] = (byte) (value >>> 56);
		output[1] = (byte) (value >>> 48);
		output[2] = (byte) (value >>> 40);
		output[3] = (byte) (value >>> 32);
		output[4] = (byte) (value >>> 24);
		output[5] = (byte) (value >>> 16);
		output[6] = (byte) (value >>>  8);
		output[7] = (byte) (value       );
	}
	public static void putLong(final long value, final byte[] output, final int outputOffset) {
		output[outputOffset    ] = (byte) (value >>> 56);
		output[outputOffset + 1] = (byte) (value >>> 48);
		output[outputOffset + 2] = (byte) (value >>> 40);
		output[outputOffset + 3] = (byte) (value >>> 32);
		output[outputOffset + 4] = (byte) (value >>> 24);
		output[outputOffset + 5] = (byte) (value >>> 16);
		output[outputOffset + 6] = (byte) (value >>>  8);
		output[outputOffset + 7] = (byte) (value       );
	}
	public static byte[] putLong(final long value) {
		return new byte[] {
			(byte) (value >>> 56),
			(byte) (value >>> 48),
			(byte) (value >>> 40),
			(byte) (value >>> 32),
			(byte) (value >>> 24),
			(byte) (value >>> 16),
			(byte) (value >>>  8),
			(byte) (value       )
		};
	}

	public static int getInt(final byte[] byteArray, final int offset) {
		return (
			((0xff & byteArray[offset    ]) << 24) |
			((0xff & byteArray[offset + 1]) << 16) |
			((0xff & byteArray[offset + 2]) <<  8) |
			((0xff & byteArray[offset + 3])      )
		);
	}
	public static int getInt(final byte[] byteArray) {
		return (
			((0xff & byteArray[0]) << 24) |
			((0xff & byteArray[1]) << 16) |
			((0xff & byteArray[2]) <<  8) |
			((0xff & byteArray[3])      )
		);
	}
	public static void putInt(final int value, final byte[] output) {
		output[0] = (byte) (value >>> 24);
		output[1] = (byte) (value >>> 16);
		output[2] = (byte) (value >>>  8);
		output[3] = (byte) (value       );
	}
	public static void putInt(final int value, final byte[] output, final int outputOffset) {
		output[outputOffset    ] = (byte) (value >>> 24);
		output[outputOffset + 1] = (byte) (value >>> 16);
		output[outputOffset + 2] = (byte) (value >>>  8);
		output[outputOffset + 3] = (byte) (value       );
	}
	public static byte[] putInt(final int value) {
		return new byte[] {
			(byte) (value >>> 24),
			(byte) (value >>> 16),
			(byte) (value >>>  8),
			(byte) (value       )
		};
	}

	public static short getShort(final byte[] byteArray, final int offset) {
		return (short) (
			((0xff & (byteArray[offset    ])) << 8) |
			((0xff & (byteArray[offset + 1]))     )
		);
	}
	public static short getShort(final byte[] byteArray) {
		return (short) (
			((0xff & (byteArray[0])) << 8) |
			((0xff & (byteArray[1]))     )
		);
	}
	public static void putShort(final short value, final byte[] output) {
		output[0] = (byte) (value >>> 8);
		output[1] = (byte) (value      );
	}
	public static void putShort(final short value, final byte[] output, final int outputOffset) {
		output[outputOffset    ] = (byte) (value >>> 8);
		output[outputOffset + 1] = (byte) (value      );
	}
	public static byte[] putShort(final short value) {
		return new byte[] {
			(byte) (value >>> 8),
			(byte) (value      )
		};
	}
}