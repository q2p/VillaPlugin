package q2p.quickclick.help;

public class DisplayableException extends StaticException {
	private final String message;
	private final Throwable cause;

	public Throwable getCause() {
		return cause;
	}
	
	/** @return Читаемое сообщение, которое можно показать пользователю. */
	public String getMessage() {
		return message;
	}
	
	public DisplayableException(final String message, final Throwable cause) {
		this.message = message;
		this.cause = cause;
	}
}