package q2p.quickclick.conversion;

import q2p.quickclick.Log;
import q2p.quickclick.MainSerializer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.function.Consumer;

public final class FFMpeg {
	// TODO:
	private static final String ffmpegPath = Paths.get("E:/villaMCFolder/ffmpeg/bin/ffmpeg.exe").toAbsolutePath().toString();

	public static boolean execute(final Consumer<String> output, final String ... arguments) {
		final LinkedList<String> args = new LinkedList<>();

		for(final String arg : arguments)
			args.addLast(arg);

		return execute(output, args);
	}

	public static boolean execute(final Consumer<String> output, final LinkedList<String> arguments) {
		final StringBuilder sb = new StringBuilder();

		arguments.addFirst(ffmpegPath);
		sb.append(ffmpegPath);

		for(String arg : arguments) {
			sb.append(' ');
			sb.append(arg);
		}

		output.accept(sb.toString());

		final ProcessBuilder pb = new ProcessBuilder(arguments);
		pb.redirectOutput(ProcessBuilder.Redirect.PIPE);
		pb.redirectInput(ProcessBuilder.Redirect.PIPE);
		pb.redirectError(ProcessBuilder.Redirect.PIPE);

		final Process p;
		final long startTime = System.currentTimeMillis();
		try {
			p = pb.start();
			try {
				final BufferedReader inReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while(true) {
					final String line = inReader.readLine();
					if(line == null)
						break;
					output.accept(line);
				}
			} catch(final Throwable ignore) {}
			try {
				p.waitFor();
			} catch(final InterruptedException ignore) {
				assert false;
			}
		} catch(final Throwable t) {
			output.accept(Log.formatThrowable(t));
			return false;
		}

		output.accept("FFMPEG Time: " + (System.currentTimeMillis()-startTime));
		return true;
	}
}