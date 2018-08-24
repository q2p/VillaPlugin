package q2p.quickclick.base;

/**
 * Переводит числа с плавающией точкой в целые числа так, что их значений можно сравнивать.<br>
 * Основное применение: предоставление возможности сравненивать бинарные представления дробных чисел используя целочисленные типы полученные после конвертации.<br>
 * Пример:<br>
 * {@code Float.compare(a, b) == Integer.compare(floatToComparableInt(a), floatToComparableInt(b))}<br>
 * {@code Double.compare(a, b) == Long.compareUnsigned(doubleToComparableUnsignedLong(a), doubleToComparableUnsignedLong(b))}<br>
 * <b>Примечание:</b> При сравнениии надо обращать внимание не на модуль чисел, а на их знаки: {@code -1}, {@code 0}, {@code 1}.<br>
 * <b>Примечание:</b> Имплементация не проверяет является ли дробное значение {@code NaN}, данный случай следует рассматривать отдельно, если это необходимо.
 */
public final class FloatingPointTypePunning {
	/*

	Оптимизированно из следующего куска кода:

	long ua = Double.doubleToRawLongBits(a);
	long ub = Double.doubleToRawLongBits(b);

	if(Double.isNaN(a)) ua = Long.MAX_VALUE;
	if(Double.isNaN(b)) ub = Long.MAX_VALUE;

	// -0.0
	if(ua == signMask64) ua = 0;
	if(ub == signMask64) ub = 0;

	// Signs differ?
	if((ua < 0) != (ub < 0))
		return Long.compare(ua, ub);

	// If numbers are negative
	if(ua < 0) {
		ua = -ua;
		ub = -ub;
	}

	return Long.compare(ua, ub);

	*/
	public static long doubleToComparableLong(final double value) {
		final long ret = Double.doubleToRawLongBits(value);

		// Аномальный ноль (-0.0)
		if(ret == 0x8000000000000000L)
			return 0;

		// Если ret > 0, то оставить всё как есть
		if((ret & 0x8000000000000000L) == 0)
			return ret;

		// Смена величины и сохранение знака если ret < 0
		return -ret | 0x8000000000000000L;
	}
	public static int floatToComparableInt(final float value) {
		final int ret = Float.floatToRawIntBits(value);

		// Аномальный ноль (-0.0)
		if(ret == 0x80000000)
			return 0;

		// Если ret > 0, то оставить всё как есть
		if((ret & 0x80000000) == 0)
			return ret;

		// Смена величины и сохранение знака если ret < 0
		return -ret | 0x80000000;
	}

	// Оптимизированно из UnsignedMath.shiftSignRanges(doubleToComparableLong(value))
	public static long doubleToComparableUnsignedLong(final double value) {
		final long ret = Double.doubleToRawLongBits(value);

		// Аномальный ноль (-0.0)
		if(ret == 0)
			return 0x8000000000000000L;

		// Если ret > 0, то оставить всё как есть
		if((ret & 0x8000000000000000L) == 0)
			return ret | 0x8000000000000000L;

		// Смена величины и сохранение знака если ret < 0
		return -ret;
	}

	// Оптимизированно из UnsignedMath.shiftSignRanges(floatToComparableInt(value));
	public static int floatToComparableUnsignedInt(final float value) {
		final int ret = Float.floatToRawIntBits(value);

		// Аномальный ноль (-0.0)
		if(ret == 0)
			return 0x80000000;

		// Если ret > 0, то оставить всё как есть
		if((ret & 0x80000000) == 0)
			return ret | 0x80000000;

		// Смена величины и сохранение знака если ret < 0
		return -ret;
	}
}