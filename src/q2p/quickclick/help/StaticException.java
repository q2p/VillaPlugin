package q2p.quickclick.help;

public class StaticException extends Throwable {
	public StaticException() {
		super(null, null, true, false); // TODO: Шо це enableSuppression?
	}
	public StaticException(final String message, final Throwable cause) {
		super(message, cause, true, false); // TODO: Шо це enableSuppression?
	}
}