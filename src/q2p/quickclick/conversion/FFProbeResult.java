package q2p.quickclick.conversion;

public final class FFProbeResult {
	private final Resolution resolution;
	public Resolution getResolution() {
		assert !hasFailed();
		return resolution;
	}
	private final boolean hasAlpha;
	public final boolean hasAlpha() {
		assert !hasFailed();
		return hasAlpha;
	}
	private final FrameRate frameRate;
	public final FrameRate getFrameRate() {
		assert frameRate != null;
		return frameRate;
	}
	private final int duration;
	public final int getDuration() {
		assert duration != -1;
		return duration;
	}
	public final boolean hasDuration() {
		assert !hasFailed();
		return duration >= 0;
	}
	private final String failureComment;
	public final String getFailureComment() {
		assert hasFailed();
		return failureComment;
	}
	private final String consoleOutput;
	public final String getConsoleOutput() {
		assert hasFailed();
		return consoleOutput;
	}
	public final boolean hasFailed() {
		return consoleOutput != null;
	}

	public FFProbeResult(final int width, final int height, final boolean hasAlpha, final FrameRate frameRate, final int duration) {
		assert width > 0 && height > 0;
		assert frameRate == null ? duration == -1 : duration >= -1;

		this.resolution = new Resolution(width, height);
		this.hasAlpha = hasAlpha;
		this.frameRate = frameRate;
		this.duration = duration;
		failureComment = null;
		consoleOutput = null;
	}

	private FFProbeResult(final String failureComment, final String consoleOutput) {
		assert consoleOutput != null;
		this.failureComment = failureComment;
		this.consoleOutput = consoleOutput;
		resolution = null;
		hasAlpha = false;
		frameRate = null;
		duration = -1;
	}

	public static FFProbeResult failure(final String failureComment, final String consoleOutput) {
		return new FFProbeResult(failureComment, consoleOutput);
	}
}