package q2p.quickclick.conversion;

import q2p.quickclick.MainSerializer;
import q2p.quickclick.Parsing;
import q2p.quickclick.help.DisplayableException;
import q2p.quickclick.help.NumberParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public final class FFProbe {
	// TODO:
	private static final String ffprobePath = Paths.get("E:/villaMCFolder/ffmpeg/bin/ffprobe.exe").toAbsolutePath().toString();

	private static final String[] alphaFormats = {
		"rgba", "argb",
		"bgra", "abgr",
		"gbra",
		"yuva", "ayuv", "ya"
	};

	public static FFProbeResult execute(final Path inputFile, final boolean askForFrameRateAndDuration) {
		final List<String> args = new LinkedList<>();

		args.add(ffprobePath);

		args.add("-v"); args.add("error");
		args.add("-select_streams"); args.add("v:0");
		args.add("-show_entries"); args.add("stream=width,height,pix_fmt"+(askForFrameRateAndDuration ? ",avg_frame_rate" : ""));
		if(askForFrameRateAndDuration) {
			args.add("-show_entries"); args.add("format=duration");
		}
		args.add("-of"); args.add("default=noprint_wrappers=1");
		args.add(inputFile.toAbsolutePath().toString());

		final StringBuilder sb = new StringBuilder();
		for(final String arg : args) {
			sb.append(arg);
			sb.append(' ');
		}

		final String startingArgs = sb.toString().trim();
		sb.setLength(0);

		final ProcessBuilder pb = new ProcessBuilder(args);
		pb.redirectInput(ProcessBuilder.Redirect.PIPE);
		pb.redirectError(ProcessBuilder.Redirect.PIPE);

		final Process p;
		try {
			p = pb.start();
			try {
				final BufferedReader inReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				final BufferedReader errReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				while(true) {
					final String inLine = inReader.readLine();
					final String errLine = errReader.readLine();
					if(inLine == null && errLine == null)
						break;
					if(inLine != null) {
						sb.append(inLine);
						sb.append('\n');
					}
					if(errLine != null) {
						sb.append(errLine);
						sb.append('\n');
					}
				}
			} catch(final Throwable ignore) {}
			try {
				p.waitFor();
			} catch(final InterruptedException ignore) {
				assert false;
			}
		} catch(final Throwable t) {
			return FFProbeResult.failure(t.getMessage(), startingArgs+'\n'+sb.toString());
		}

		String output = sb.toString().trim();

		String lines[] = new String[askForFrameRateAndDuration ? 5 : 3];

		final boolean failed = !Parsing.fillContainer(output, '\n', lines, String::trim);

		output = startingArgs+'\n'+output;

		if(failed)
			return FFProbeResult.failure("Format error", output);

		int pWidth = -1;
		int pHeight = -1;
		byte pAlpha = -1;
		FrameRate pFrameRate = null;
		int pDuration = -1;

		for(final String line : lines) {
			try {
				if(line.startsWith("width=")) {
					if(pWidth != -1)
						return FFProbeResult.failure("Format error", output);

					Long t = NumberParser.parse(line.substring(6), false, false, 1, Integer.MAX_VALUE);
					if(t == null)
						return FFProbeResult.failure("Format error", output);
					pWidth = t.intValue();
				} else if(line.startsWith("height=")) {
					if(pHeight != -1)
						return FFProbeResult.failure("Format error", output);

					Long t = NumberParser.parse(line.substring(7), false, false, 1, Integer.MAX_VALUE);
					if(t == null)
						return FFProbeResult.failure("Format error", output);
					pHeight = t.intValue();
				} else if(line.startsWith("avg_frame_rate=")) {
					if(!askForFrameRateAndDuration || pFrameRate != null)
						return FFProbeResult.failure("Format error", output);
					pFrameRate = FrameRate.getFrameRate(line.substring(15));
				} else if(line.startsWith("duration=")) {
					if(!askForFrameRateAndDuration || pDuration != -1)
						return FFProbeResult.failure("Format error", output);

					final String durationString = line.substring(9);
					if("N/A".equals(durationString)) {
						pDuration = -2;
					} else {
						pDuration = (int) (Double.parseDouble(durationString)*1000);
						if(pDuration < 0)
							return FFProbeResult.failure("Format error", output);
					}
				} else if(line.startsWith("pix_fmt=")) {
					if(pAlpha != -1)
						return FFProbeResult.failure("Format error", output);

					final String formatName = line.substring(8);
					for(String af : alphaFormats) {
						if(formatName.startsWith(af)) {
							pAlpha = 1;
							break;
						}
					}
					if(pAlpha == -1)
						pAlpha = 0;
				} else {
					return FFProbeResult.failure("Format error", output);
				}
			} catch(final NumberFormatException | DisplayableException e) {
				return FFProbeResult.failure("Format error", output);
			}
		}

		return new FFProbeResult(pWidth, pHeight, pAlpha == 1, pFrameRate, pDuration == -2 ? -1 : pDuration);
	}
}