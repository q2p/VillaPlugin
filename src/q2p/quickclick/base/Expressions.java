package q2p.quickclick.base;

public final class Expressions {
	public static <T> T decline(final int amount, final T one, final T two, final T five) {
		if (amount > 10 && ((amount % 100) / 10) == 1)
			return five;

		switch (amount % 10) {
			case 1:
				return one;
			case 2:
			case 3:
			case 4:
				return two;
			default: // 0, 5-9
				return five;
		}
	}
}