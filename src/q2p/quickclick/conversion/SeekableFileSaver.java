package q2p.quickclick.conversion;

import q2p.quickclick.base.Assist;
import q2p.quickclick.conversion.dithering.DitheringAlgorithm;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/*

int targetWidth
int targetHeight
int framesPerPeriod
int periodDurationMilliseconds
int totalFrames
int amountOfZoomLevels
int periodsPerInterleave
byte preferesStaticBackground

byte[62][62] previewIds

byte[8][8] staticBackgroundIds

[amountOfZoomLevels] {
	int zoomLevelWidth
	int zoomLevelHeight
	int chunkSize
}

long[amountOfZoomLevels][amountOfInterleaves+1] keyframeLocations // +1 для указания конца последнего interleave'а

[amountOfInterleaves] {
	[amountOfZoomLevels] {
		short treeArraySizeInBytes
		byte[] huffmanTree
		[framesInThisInterleave] {
			short chunksUpdated
			if(chunksUpdated > 0) {
				byte[8][8] huffmanBackgroundIds
				[chunksUpdated] {
					byte offsetX
					byte offsetY
					byte[] huffmanIds
				}
			}
		}
	}
}

*/

public final class SeekableFileSaver {
	private static final int outputBuffer = 8*1024;
	private final Path output;
	private final FileChannel fileChannel;
	public final FrameRate frameRate;
	public final int framesPerInterleave;
	final ZoomableRenderTarget[] zoomLevels;
	private final Resolution inputResolution;

	private ByteBuffer interleaveMap;

	private static final int backgroundResolution = 8;
	private static final int previewResolution = 62;

	private static final DitheringAlgorithm backgroundDithering = DitheringAlgorithm.defaultDithering;

	private int interleaveUsed = 0;
	private final FrameSaver[] interleave;

	private final DitheringAlgorithm ditheringAlgorithm;
	private final RGB backgroundColor;

	public SeekableFileSaver(
		final Path output,
		final Resolution targetScreen,
		final FrameRate frameRate,
		final int interleaveDuration,
		final ZoomableRenderTarget[] zoomLevels,
		final boolean preferesStaticBackground,
		final RGB staticBackground,
		final DitheringAlgorithm ditheringAlgorithm,
		final Resolution inputResolution
	) throws IOException {
		this.output = output;
		this.frameRate = frameRate;
		this.zoomLevels = zoomLevels;
		this.ditheringAlgorithm = ditheringAlgorithm;
		this.backgroundColor = staticBackground;
		this.inputResolution = inputResolution;

		final int periodsPerInterleave =  Assist.perfectPositiveCeil(interleaveDuration, frameRate.getPeriodDuration());
		framesPerInterleave = periodsPerInterleave * frameRate.getFramesPerPeriod();

		final int interleavesAmount = Assist.perfectPositiveCeil(/*framesAmount*/1, framesPerInterleave); // TODO:

		interleaveMap = ByteBuffer.allocate(zoomLevels.length*(interleavesAmount+1)*Long.BYTES);

		interleave = new FrameSaver[framesPerInterleave];

		fileChannel = FileChannel.open(output, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.READ, StandardOpenOption.WRITE);

		ByteBuffer temp = ByteBuffer.allocate(
			Integer.BYTES + Integer.BYTES + // target resolution
			Integer.BYTES + Integer.BYTES + // frame rate
			Integer.BYTES + // total frames
			Integer.BYTES + // amount of zoom levels
			Integer.BYTES + // periods per interleave
			1 + // preferes static background
			backgroundResolution * backgroundResolution + // background ids
			zoomLevels.length * (
				Integer.BYTES + Integer.BYTES + // zoom level resolution
				Integer.BYTES // chunk size
			)
		);
		temp.putInt(targetScreen.getWidth());
		temp.putInt(targetScreen.getHeight());
		temp.putInt(frameRate.getFramesPerPeriod());
		temp.putInt(frameRate.getPeriodDuration());
		temp.putInt(0); // TODO:
		temp.putInt(zoomLevels.length);
		temp.putInt(periodsPerInterleave);
		temp.put(preferesStaticBackground ? (byte)1 : (byte)0);
		byte[] backgroundCanvas = new byte[backgroundResolution*backgroundResolution*3];
		for(int i = 0; i != backgroundCanvas.length;) {
			backgroundCanvas[i++] = (byte) staticBackground.r;
			backgroundCanvas[i++] = (byte) staticBackground.g;
			backgroundCanvas[i++] = (byte) staticBackground.b;
		}
		backgroundDithering.dither(backgroundResolution, backgroundResolution, backgroundCanvas, backgroundCanvas, MapColorPallete.instance);
		temp.put(backgroundCanvas, 0, backgroundResolution*backgroundResolution);
		for(ZoomableRenderTarget zoomLevel : zoomLevels) {
			temp.putInt(zoomLevel.getCanvasResolution().getWidth());
			temp.putInt(zoomLevel.getCanvasResolution().getHeight());
			temp.putInt(zoomLevel.getChunkSize());
		}
	}

	public void feedRGB(final Frame[] zoomLevels) {
		FrameSaver frame = new FrameSaver(ditheringAlgorithm, backgroundColor, inputResolution, imagePath, zoomLevels);
		interleave[interleaveUsed++] = frame;
		if(interleaveUsed == framesPerInterleave) {
			// TODO: save
			interleaveUsed = 0;
		}
	}

	public void finish() {
		// TODO:
	}
}