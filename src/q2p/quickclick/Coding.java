package q2p.quickclick;

public final class Coding {
	/**
	 * Возвращает максимальное количество байт занимаемое строкой из {@code characters} символов в кодировке UTF-8
	 * @param characters Количество символов.
	 * @return Количество байт.
	 */
	public static int maxUTF_8(final int characters) {
		return 4 * characters;
	}
}
