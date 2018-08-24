package q2p.quickclick.conversion;

import q2p.quickclick.Assist;
import q2p.quickclick.Log;
import q2p.quickclick.MainSerializer;
import q2p.quickclick.conversion.dithering.DitheringAlgorithm;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.Random;

public class FrameSaver {
	public FrameSaver(
		final DitheringAlgorithm ditheringAlgorithm,
		final RGB backgroundColor,
		final Resolution originalResolution,
		final Path imagePath,
		final ZoomableRenderTarget[] zoomLevels
	) {
		Path origInputRGB = MainSerializer.getRandomTempFile("");

		Path[] zoomLevelsTempInputRGB = new Path[zoomLevels.length];

		for(int i = 0; i != zoomLevels.length; i++)
			zoomLevelsTempInputRGB[i] = MainSerializer.getRandomTempFile("");

		final LinkedList<String> args = new LinkedList<>();
		Assist.addToList(args, "-loglevel", "error"); // quiet, panic, fatal, error, warning, info

		if(backgroundColor != null) {
			Assist.addToList(args,
				"-f", "lavfi",
				"-i", "color=c=#"+backgroundColor.hex+":s="+originalResolution.getWidth()+"x"+originalResolution.getHeight()
			);
		}

		Assist.addToList(args, "-i", imagePath.toString());

		if(backgroundColor != null)
			Assist.addToList(args, "-filter_complex", "overlay");

		ffmpegImageToRGB(args, originalResolution, Resolution.ResizeTransformation.None, origInputRGB.toString());
		for(int i = 0; i != zoomLevels.length; i++)
			ffmpegImageToRGB(args, zoomLevels[i].getCanvasResolution(), zoomLevels[i].getResizeImageTransformation(), zoomLevelsTempInputRGB[i].toString());

		FFMpeg.execute(Log::consoleInfo, args); // TODO: поменять вывод

		byte[] imagePixelData = new byte[originalResolution.space() * 3];
		ByteBuffer buffer = ByteBuffer.wrap(imagePixelData);
		readRaw(origInputRGB, buffer);
		getDominantColor(imagePixelData, originalResolution);

		for(int i = 0; i != zoomLevels.length; i++) {
			Resolution resolution = zoomLevels[i].getCanvasResolution();
			imagePixelData = new byte[resolution.space() * 3];
			buffer = ByteBuffer.wrap(imagePixelData);
			readRaw(zoomLevelsTempInputRGB[i], buffer);

			ditheringAlgorithm.dither(resolution.getWidth(), resolution.getHeight(), imagePixelData, imagePixelData, MapColorPallete.instance);

			ColorsConverter.extract(resolution.getWidth(), resolution.getHeight(), imagePixelData, imagePixelData, MapColorPallete.instance);

			displayRGB(zoomLevelsTempInputRGB[i], resolution, ByteBuffer.wrap(imagePixelData));
		}

		exportResult(Paths.get("E:/aaa.id"), imagePixelData);
	}

	private void ffmpegImageToRGB(final LinkedList<String> args, final Resolution resizedImage, final Resolution.ResizeTransformation resizeTransformation, final String outPath) {
		Assist.addToList(args,
			"-vframes", "1",
			"-threads", "1",
			"-c:v", "rawvideo",
			"-f", "rawvideo",
			"-pix_fmt", "rgb24"
		);
		// args.add("-field_order"); args.add("progressive");
		if(resizeTransformation != Resolution.ResizeTransformation.None) {
			args.add("-sws_flags");
			if(resizeTransformation == Resolution.ResizeTransformation.Downscaling)
				args.add("accurate_rnd;lanczos;full_chroma_int");
			else
				args.add("accurate_rnd;bilinear;full_chroma_int");

			Assist.addToList(args,
				"-sws_dither", "none",
				"-s", resizedImage.getWidth()+"x"+resizedImage.getHeight()
			);
		}
		Assist.addToList(args,
			"-an",
			"-y", outPath
		);
	}

	private void getDominantColor(final byte[] imagePixelData, final Resolution resolution) {
		final int space = resolution.space();
		final int channelsAmount = 3*space;
		long r = 0;
		long g = 0;
		long b = 0;
		for(int i = 0; i != channelsAmount;) {
			r += 0xFF & imagePixelData[i++];
			g += 0xFF & imagePixelData[i++];
			b += 0xFF & imagePixelData[i++];
		}
		r /= space;
		g /= space;
		b /= space;

		for(int i = 0; i != 6000; i++) {
			imagePixelData[3*i  ] = (byte) r;
			imagePixelData[3*i+1] = (byte) g;
			imagePixelData[3*i+2] = (byte) b;
		}
	}

	public static void readRaw(final Path path, final ByteBuffer buffer) {
		try(final FileChannel fc = FileChannel.open(path, StandardOpenOption.READ)) {
			if(fc.size() != buffer.capacity()) {
				System.out.println("TODO: SIZE MISSMATCH");
				System.exit(1);
			}

			while(buffer.hasRemaining())
				fc.read(buffer);
		} catch(final Throwable t) {
			System.out.println("TODO");
			System.exit(1);
		}
	}

	private void exportResult(final Path out, final byte[] result) {
		final ByteBuffer bb = ByteBuffer.wrap(result);

		try(final FileChannel fc = FileChannel.open(out,StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
			while(bb.hasRemaining())
				fc.write(bb);
		} catch(final Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	private static void displayRGB(final Path inputRGB, final Resolution resolution, final ByteBuffer rgb) {
		try(final FileChannel fc = FileChannel.open(inputRGB, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
			while(rgb.hasRemaining())
				fc.write(rgb);
		} catch(final Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}

		FFMpeg.execute(Log::consoleInfo,
			"-loglevel", "warning", // quiet, panic, fatal, error, warning, info
			"-threads", "1",
			"-f", "rawvideo",
			"-pix_fmt", "rgb24",
			"-s:v", resolution.getWidth()+"x"+resolution.getHeight(),
			"-r", "1",
			"-i", inputRGB.toString(),
			"-c:v", "png",
			"-field_order", "progressive",
			"-pix_fmt", "rgb24",
			"-an",
			"-y",
			MainSerializer.resourcesPath().resolve("output").resolve("aaa"+new CUID(new Random().nextLong()).toBase64()+".png").toAbsolutePath().toString()
		);
	}
}