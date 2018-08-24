package q2p.quickclick.conversion;

import org.bukkit.command.CommandSender;
import q2p.quickclick.Assist;
import q2p.quickclick.Log;
import q2p.quickclick.MainSerializer;
import q2p.quickclick.conversion.dithering.DitheringAlgorithm;
import q2p.quickclick.help.DisplayableException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.TreeSet;

public final class WorkStation {
	private final TreeMap<CUID, InputSource> inputSources = new TreeMap<>();
	private final TreeSet<FrameRate> outputFrameRates = new TreeSet<>();

	private final ZoomControl zoomControl = new ZoomControl();

	private final TreeSet<Resolution> renderTargets = new TreeSet<>();

	private RGB staticBackgroundColor = new RGB(0, 0 , 0);
	private boolean preferStaticBackgroundColor = false;

	private final TreeMap<String, DitheringAlgorithm> ditheringAlgorithms = new TreeMap<>();

	public final void doTheJob() {
		DitheringAlgorithm[] ditheringAlgorithms = new DitheringAlgorithm[this.ditheringAlgorithms.size()];
		Iterator<DitheringAlgorithm> iterator = this.ditheringAlgorithms.values().iterator();
		for(int i = 0; iterator.hasNext(); i++)
			ditheringAlgorithms[i] = iterator.next();

		inputSources.forEach((cuid, inputSource) -> {
			final RenderTargetConfiguration[] renderTargetsAndZoomedResolutions = new RenderTargetConfiguration[renderTargets.size()];
			int i = 0;
			for(final Resolution renderTarget : renderTargets) {
				final int j = i++;
				renderTargetsAndZoomedResolutions[j] = new RenderTargetConfiguration(
					ditheringAlgorithms,
					renderTarget,
					zoomControl.getListOfZoomableRenderTargets(inputSource.resolution, renderTarget)
				);
			}

			inputSource.forEachPeriod(period -> {
				for(final FrameRate frameRate : outputFrameRates) {
					try {
						if(inputSource.isImageSequence()) {
							makeImageSequence(inputSource, period, frameRate, renderTargetsAndZoomedResolutions);
						} else {
							makeRawVideo(MainSerializer.resourcesPath().resolve("output/tout"), inputSource, period, frameRate, renderTargetsAndZoomedResolutions);
						}
					} catch(IOException | DisplayableException e) {
						// TODO:
						e.printStackTrace();
					}
				}
			});
		});
	}

	private static final int inputChunkDuration = 4;
	private static final int inputChunkFramesMax = 200;

	private void makeRawVideo(
		final Path output,
		final InputSource inputSource,
		final Period period,
		final FrameRate frameRate,
		final RenderTargetConfiguration[] renderTargetsAndZoomedResolutions
	) throws DisplayableException, IOException {
		final String ffmpegFrameRate = frameRate.getFramesPerPeriod()+"/"+frameRate.getPeriodDuration();

		final int chunkDuration = Math.max(inputChunkDuration, inputChunkFramesMax * frameRate.getPeriodDuration() / frameRate.getFramesPerPeriod())*1000;

		int ss = period.getStartMilisec();
		int framesLeft = Integer.MAX_VALUE;
		if(period.hasEndingLimit())
			framesLeft = Math.max(1, (period.getEndMilisec() - period.getStartMilisec()) * frameRate.getFramesPerPeriod() / (frameRate.getPeriodDuration() * 1000));

		SeekableFileSaver seekableFileSaver = new SeekableFileSaver(
			output,
			renderTargetsAndZoomedResolutions[0].getRenderTarget(),
			frameRate,
			1,
			renderTargetsAndZoomedResolutions[0].getZoomLevels(),
			preferStaticBackgroundColor,
			staticBackgroundColor,
			DitheringAlgorithm.defaultDithering,
			inputSource.resolution
		);
		while(true) {
			final TempIterableFilePattern iterableTempFiles = MainSerializer.getRandomTempIterable(".png");

			final LinkedList<String> args = new LinkedList<>();
			Assist.addToList(args, "-loglevel", "error"); // quiet, panic, fatal, error, warning, info

			if(inputSource.hasAlpha() != HasAlpha.Opaque) {
				Assist.addToList(args,
					"-f", "lavfi",
					"-i", "color=c=#"+this.staticBackgroundColor.hex+":s="+inputSource.resolution.getWidth()+"x"+inputSource.resolution.getHeight()
				);
			}

			Assist.addToList(args,
				"-ss", (ss/1000) + "." + (ss%1000),
				"-i", inputSource.getRawSourcePath().toString(),
				"-t", Integer.toString(chunkDuration),
				"-copyts"
			);

			if(inputSource.hasAlpha() != HasAlpha.Opaque)
				Assist.addToList(args, "-filter_complex", "overlay");

			Assist.addToList(args,
				"-threads", "1",
				"-r", ffmpegFrameRate,
				"-c:v", "png",
				"-field_order", "progressive",
				"-pix_fmt", "rgb24",
				"-an",
				"-y", iterableTempFiles.ffmpegPattern // TODO:
			);

			FFMpeg.execute(Log::consoleInfo, args); // TODO: поменять вывод

			Path imageSequence[] = InputSource.getSequence(iterableTempFiles.folder, iterableTempFiles.prefix, iterableTempFiles.suffix, 0, -1);

			if(imageSequence.length == 0)
				break;

			final int framesToRender = Math.min(framesLeft, imageSequence.length);

			for(int i = 0; i != framesToRender; i++)
				pushImage(imageSequence[i]);

			framesLeft -= framesToRender;

			if(framesLeft == 0)
				break;

			ss += chunkDuration;
		}
	}

	private void makeImageSequence(final InputSource inputSource, final Period period, final FrameRate frameRate, final RenderTargetConfiguration[] renderTargetsAndZoomedResolutions) throws IOException {
		final Path[] inputSequence = inputSource.getImageSequence();
		final FrameRate inputFrameRate = inputSource.getFrameRate();
		final FrameRate outputFramerate = FrameRate.min(frameRate, inputFrameRate);

		final int startingFrame = milisecToFrames(period.getStartMilisec(), inputFrameRate);
		final int endingFrame;

		if(period.isStillShot())
			endingFrame = startingFrame;
		else if(period.hasEndingLimit())
			endingFrame = milisecToFrames(period.getEndMilisec(), inputFrameRate);
		else
			endingFrame = inputSequence.length-1;

		if(inputFrameRate.same(outputFramerate)) {
			for(int i = startingFrame; i <= endingFrame; i++)
				pushImage(inputSequence[i]);
		} else {
			int ofid = 0;
			while(true) {
				/*
					ofid / orate = secs
					secs * inrate = ifps

					ofid * opd / ofpp = secs
				 	secs * ipp / ipd = ifps

				 	(ofid * opd * ipp) / (ofpp * ipd) = ifps
				*/
				final int inputFrame = startingFrame + (ofid * outputFramerate.getPeriodDuration() * inputFrameRate.getPeriodDuration()) / (outputFramerate.getFramesPerPeriod() * inputFrameRate.getPeriodDuration());
				if(inputFrame > endingFrame)
					break;

				pushImage(inputSequence[inputFrame]);
			}
		}
	}

	private int milisecToFrames(int millisec, FrameRate frameRate) {
		final int periodsSkipped = millisec / frameRate.getPeriodDuration();
		final int periodsRem     = millisec % frameRate.getPeriodDuration();
		return
			periodsSkipped*frameRate.getFramesPerPeriod() + // Skipping whole periods
			periodsRem    *frameRate.getFramesPerPeriod()/frameRate.getPeriodDuration(); // Skipping offset*fps = offset*fpp/pd
	}

	private void pushImage(final Path image) {
		for(final RenderTargetConfiguration renderTargetConfiguration : renderTargetsAndZoomedResolutions) {
			// TODO:
				/*final SeekableFileSaver seekableFile = new SeekableFileSaver(targetScreen, new FrameRate(0, 0), 1, zoomLevels);
				seekableFile.feedPNG(imagePath);
				seekableFile.finish();*/
		}
	}

	public void addSourceIfAbsent(final String name, final Path sourcePath) throws DisplayableException {
		// 0 в CUID означает, что это единственный файл
		CUID cuid = new CUID(0, sourcePath.toAbsolutePath().toString().replace('\\', '/'));
		if(!inputSources.containsKey(cuid))
			inputSources.put(cuid, new InputSource(name, sourcePath));
	}

	public void addImageSequenceIfAbsent(String name, Path parentDir, Path[] framesPaths, CommandSender commandSender) throws DisplayableException {
		if(framesPaths.length == 1)
			addSourceIfAbsent(name, framesPaths[0]);

		String temp = parentDir.toAbsolutePath().toString().replace('\\', '/');
		StringBuilder sb = new StringBuilder(temp.length());
		sb.append(temp.length());
		sb.append(' ');
		sb.append(temp);
		for(Path path : framesPaths) {
			temp = path.getFileName().toString().replace('\\', '/');
			sb.append(temp.length());
			sb.append(' ');
			sb.append(temp);
		}
		// 1 в CUID означает, что это набор файлов
		CUID cuid = new CUID(1, sb.toString());
		inputSources.computeIfAbsent(cuid, cuid1 -> {
			try {
				return new InputSource(name, framesPaths); // TODO: не пускать объект если такое название уже используется другим источником
			} catch (DisplayableException e) {
				commandSender.sendMessage(e.getMessage() + Log.formatThrowable(e.getCause()));
				return null;
			}
		});
	}

	public boolean addRenderTarget(final Resolution resolution) {
		return renderTargets.add(resolution);
	}

	public boolean addOutputFrameRate(final FrameRate frameRate) {
		return outputFrameRates.add(frameRate);
	}

	public void addDitheringAlgorithm(final DitheringAlgorithm ditheringAlgorithm) {
		ditheringAlgorithms.putIfAbsent(ditheringAlgorithm.name, ditheringAlgorithm);
	}
}