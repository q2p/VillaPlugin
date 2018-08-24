package q2p.quickclick.conversion;

public final class Period {
	private final int startMilisec;
	private final int endMilisec;

	public final boolean isStillShot() {
		return endMilisec == -2;
	}

	public final int getStartMilisec() {
		return startMilisec;
	}

	public final boolean hasStartingPoint() {
		return startMilisec != -1;
	}

	public final int getEndMilisec() {
		return isStillShot() ? startMilisec : endMilisec;
	}

	public final boolean hasEndingLimit() {
		assert !isStillShot();
		return endMilisec != -1;
	}

	public Period(int startMilisec, int endMilisec) {
		this.startMilisec = startMilisec;
		this.endMilisec = endMilisec;
	}

	public static Period wholeThing() {
		return new Period(0, -1);
	}

	public static Period from(final int startMilisec) {
		assert startMilisec >= 0;
		return new Period(startMilisec, -1);
	}

	public static Period to(final int endMilisec) {
		assert endMilisec >= 0;
		return new Period(-1, endMilisec);
	}

	public static Period fromTo(final int startMilisec, final int endMilisec) {
		assert startMilisec >= 0 && endMilisec >= 0;
		return new Period(startMilisec, endMilisec);
	}

	public static Period stillShot(final int offsetMilisec) {
		assert offsetMilisec >= 0;
		return new Period(offsetMilisec, -2);
	}
}