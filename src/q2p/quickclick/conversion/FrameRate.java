package q2p.quickclick.conversion;

import q2p.quickclick.help.DisplayableException;

public final class FrameRate {
	private int framesPerPeriod;
	public final int getFramesPerPeriod() {
		return framesPerPeriod;
	}
	private int periodDuration;
	public final int getPeriodDuration() {
		return periodDuration;
	}

	public FrameRate(final int framesPerPeriod, final int periodDuration) {
		assert framesPerPeriod >= 0 && periodDuration >= 0;

		if(framesPerPeriod == 0 || periodDuration == 0) {
			this.framesPerPeriod = 0;
			this.periodDuration = 0;
		} else {
			ExactFraction fps = new ExactFraction(framesPerPeriod, periodDuration);
			this.framesPerPeriod = fps.getNumber();
			this.periodDuration = fps.getDivisor();
		}
	}

	public FrameRate(final ExactFraction framesPerPeriod, final ExactFraction periodDuration) {
		assert framesPerPeriod.getNumber() >= 0 && periodDuration.getNumber() >= 0;

		if(framesPerPeriod.getNumber() == 0 || periodDuration.getNumber() == 0) {
			this.framesPerPeriod = 0;
			this.periodDuration = 0;
		} else {
			final ExactFraction fps = framesPerPeriod.divide(periodDuration);
			this.framesPerPeriod = fps.getNumber();
			this.periodDuration = fps.getDivisor();
		}
	}

	public static final FrameRate defaultFrameRate = new FrameRate(10, 1);

	public static FrameRate getFrameRate(String frameRate) throws DisplayableException {
		int idx = frameRate.indexOf('/');
		final ExactFraction fpp;
		final ExactFraction pd;
		try {
			if(idx == -1) {
				fpp = new ExactFraction(frameRate);
				pd = ExactFraction.one;
			} else {
				fpp = new ExactFraction(frameRate.substring(0, idx));
				pd = new ExactFraction(frameRate.substring(idx+1));
			}
		} catch(final NumberFormatException e) {
			throw new DisplayableException("Не допустимый frame rate: "+frameRate, null);
		}
		if(fpp.getNumber() < 0 || pd.getNumber() < 0)
			throw new DisplayableException("Не допустимый frame rate: "+frameRate, null);

		return new FrameRate(fpp, pd);
	}

	public static FrameRate min(final FrameRate f1, final FrameRate f2) {
		if(f1.framesPerPeriod*f2.periodDuration<f2.framesPerPeriod*f1.periodDuration)
			return f1;
		return f2;
	}

	public boolean same(final FrameRate frameRate) {
		return this.framesPerPeriod == frameRate.framesPerPeriod && this.periodDuration == frameRate.periodDuration;
	}
}