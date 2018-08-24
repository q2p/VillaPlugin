package q2p.quickclick.conversion;

import q2p.quickclick.base.Assist;

public final class ZoomableRenderTarget {
	private final Resolution canvasResolution;
	public final Resolution getCanvasResolution() {
		return canvasResolution;
	}
	private final Position2D imageOffset;
	private final Resolution imageResolution;
	private final Resolution.ResizeTransformation imageTransformation;
	public final Resolution.ResizeTransformation getResizeImageTransformation() {
		return imageTransformation;
	}

	private static final int minChunkSize = 64;
	private final int chunkSize;
	public int getChunkSize() {
		return chunkSize;
	}

	public ZoomableRenderTarget(final Resolution originalResolution, final Resolution desiredImageResolution, final Resolution targetResolution, final Float backgroundBlur) {
		imageResolution  = desiredImageResolution;

		imageTransformation = Resolution.getTransformation(originalResolution, desiredImageResolution);

		if(backgroundBlur == null) {
			canvasResolution = desiredImageResolution;
			chunkSize = calculateChunkSize();
			imageOffset = new Position2D(0, 0);
			return;
		}

		canvasResolution = new Resolution(
			Math.max(desiredImageResolution.getWidth(),  targetResolution.getWidth()),
			Math.max(desiredImageResolution.getHeight(), targetResolution.getHeight())
		);
		chunkSize = calculateChunkSize();

		if(canvasResolution.same(imageResolution)) {
			imageOffset = new Position2D(0, 0);
			return;
		}

		imageOffset = new Position2D(
			(canvasResolution.getWidth()  - imageResolution.getWidth() ) / 2,
			(canvasResolution.getHeight() - imageResolution.getHeight()) / 2
		);
	}

	private int calculateChunkSize() {
		final int canvasBiggestSide = Math.max(canvasResolution.getWidth(), canvasResolution.getHeight());
		return Math.max(minChunkSize, Assist.perfectPositiveCeil(canvasBiggestSide, 256));
	}
}