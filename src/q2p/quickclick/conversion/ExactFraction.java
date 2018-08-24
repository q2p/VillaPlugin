package q2p.quickclick.conversion;

public class ExactFraction {
	public static final ExactFraction one = new ExactFraction(1, 1);

	private final int number;
	private final int divisor;
	public int getNumber() {
		return number;
	}
	public int getDivisor() {
		return number;
	}

	public ExactFraction(final String number) throws NumberFormatException {
		final int idx = number.indexOf('.');
		if(idx == -1) {
			this.number = Integer.parseInt(number);
			this.divisor = 1;
			return;
		}
		int num = Integer.parseInt(number.substring(0, idx));
		int dl = number.length() - idx - 1;
		int tdiv = 1;
		for(int i = dl; i != 0; i--)
			tdiv *= 10;
		num = num*tdiv + Integer.parseUnsignedInt(number.substring(idx + 1));
		final int gcd = gcd(num, tdiv);
		divisor = tdiv / gcd;
		this.number = num / gcd;
	}
	public ExactFraction(final int number, final int divisor) {
		assert divisor > 0;
		final int gcd = gcd(number, divisor);
		this.number = number / gcd;
		this.divisor = divisor / gcd;
	}

	public ExactFraction divide(final ExactFraction divisor) {
		final int num = this.number * divisor.divisor;
		final int div = this.divisor * divisor.number;
		final int gcd = gcd(num, div);
		return new ExactFraction(num / gcd, div / gcd);
	}

	// TODO: прверить отрицательные числа
	public static int gcd(int p, int q) {
		assert p != 0 && q != 0;
		while(q != 0) {
			int temp = q;
			q = p % q;
			p = temp;
		}
		return p;
	}
}
