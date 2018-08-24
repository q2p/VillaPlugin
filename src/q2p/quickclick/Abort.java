package q2p.quickclick;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;

public final class Abort {
	private static final String abortPrefix = "\n\n========= Abort =========\n";
	private static final String abortSuffix =   "\n=========================\n";
	public static void message(final String reason, final Throwable throwable) {
		try {
			final StringBuilder builder = new StringBuilder(abortPrefix);
			builder.append(reason);
			builder.append('\n');

			final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw, false);
			throwable.printStackTrace(pw);
			pw.flush();
			builder.append(sw.toString());
			builder.append(abortSuffix);
			log(builder.toString());
		} catch(final Throwable ignore) {
			System.exit(1);
		}
	}
	public static void message(final String reason) {
		log(abortPrefix + reason + abortSuffix);
	}
	private static void log(final String reason) {
		try(
			final FileChannel fc = FileChannel.open(
				Paths.get(
			"villa_abort_log_"+System.currentTimeMillis()+'_'+new Random().nextInt(Integer.MAX_VALUE)+".txt"
				).toAbsolutePath(),
				StandardOpenOption.READ,
				StandardOpenOption.WRITE,
				StandardOpenOption.CREATE
		)) {
			final ByteBuffer byteBuffer = ByteBuffer.wrap(reason.getBytes(StandardCharsets.UTF_8));
			while(byteBuffer.hasRemaining())
				fc.write(byteBuffer);
			fc.force(false);
			System.out.println(reason);
		} catch(final Throwable ignore) {}

		System.exit(1);
	}
	public static void critical() {
		System.exit(1);
	}
}