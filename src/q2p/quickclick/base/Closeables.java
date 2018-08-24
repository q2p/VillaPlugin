package q2p.quickclick.base;

public final class Closeables {
	public static void safeClose(final AutoCloseable closeable) {
		if(closeable != null) {
			try { closeable.close(); }
			catch(final Throwable ignore) {}
		}
	}
	public static void safeClose(final AutoCloseable ... closeable) {
		for(final AutoCloseable c : closeable)
			safeClose(c);
	}
}