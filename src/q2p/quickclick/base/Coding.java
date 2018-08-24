package q2p.quickclick.base;

import java.nio.*;
import java.nio.charset.*;

public final class Coding {
	@Deprecated
	public static byte[] toUTF(final String string) {
		return string.getBytes(StandardCharsets.UTF_8);
	}
	@Deprecated
	public static String fromUTF(final byte[] buffer, final int offset, final int length) {
		return decodeFrom(ByteBuffer.wrap(buffer, offset, length), StandardCharsets.UTF_8);
	}
	@Deprecated
	public static String fromUTF(final ByteBuffer buffer) {
		return decodeFrom(buffer, StandardCharsets.UTF_8);
	}
	@Deprecated
	public static String fromASCII(final byte[] buffer, final int offset, final int length) {
		return decodeFrom(ByteBuffer.wrap(buffer, offset, length), StandardCharsets.US_ASCII);
	}
	@Deprecated
	public static String fromASCII(final ByteBuffer buffer) {
		return decodeFrom(buffer, StandardCharsets.US_ASCII);
	}
	@Deprecated
	private static String decodeFrom(final ByteBuffer buffer, final Charset charset) {
		final CharsetDecoder decoder = getDecoder(charset, CodingErrorAction.REPLACE);
		
		try {
			return decoder.decode(buffer).toString();
		} catch(final CharacterCodingException ignore) {
			return null;
		}
	}
	
	public static CharsetDecoder getDecoder(final Charset charset, final CodingErrorAction action) {
		assert charset != null && action != null;
		final CharsetDecoder decoder = charset.newDecoder();
		assert decoder != null;
		decoder.onUnmappableCharacter(action);
		decoder.onMalformedInput(action);
		return decoder;
	}
	public static CharsetEncoder getEncoder(final Charset charset, final CodingErrorAction action) {
		assert charset != null && action != null;
		final CharsetEncoder encoder = charset.newEncoder();
		assert encoder != null;
		encoder.onUnmappableCharacter(action);
		encoder.onMalformedInput(action);
		return encoder;
	}
	
	/**
	 * Возвращает максимальное количество байт занимаемое строкой из {@code characters} символов в кодировке UTF-8
	 * @param characters Количество символов.
	 * @return Количество байт.
	 */
	public static int maxUTF_8(final int characters) {
		return 4 * characters;
	}
}
