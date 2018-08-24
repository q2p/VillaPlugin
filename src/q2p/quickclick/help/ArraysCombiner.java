package q2p.quickclick.help;

public final class ArraysCombiner {
	/** Создаёт новый массив с содержимым массивов */
	public static byte[] combine(final byte[] ... arrays) {
		int length = 0;
		for(final byte[] array : arrays)
			length += array.length;

		final byte[] fused = new byte[length];
		int offset = 0;
		for(final byte[] array : arrays) {
			System.arraycopy(array, 0, fused, offset, array.length);
			offset += array.length;
		}

		return fused;
	}

	/**
	 * Записывает байты в {@code destination}.<br>
	 * <b>Примечание:</b> {@code destination} должен иметь длину большую или равную количеству байт в {@code arrays}, иначе программа аварийно завершится.
	 * @return количество записаных байт в {@code destinations}
	 */
	public static int combine(final byte[] destination, final byte[] ... arrays) {
		int offset = 0;
		for(final byte[] array : arrays) {
			try {
				System.arraycopy(array, 0, destination, offset, array.length);
				offset += array.length;
			} catch(final IndexOutOfBoundsException e) {
				assert false;
			}
		}
		return offset;
	}
}