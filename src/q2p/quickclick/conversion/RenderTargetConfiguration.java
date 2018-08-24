package q2p.quickclick.conversion;

import q2p.quickclick.conversion.dithering.DitheringAlgorithm;

public final class RenderTargetConfiguration {
	private final DitheringAlgorithm[] ditheringAlgorithms;
	private final Resolution renderTarget;
	public final Resolution getRenderTarget() {
		return renderTarget;
	}
	private final ZoomableRenderTarget[] zoomLevels;
	public ZoomableRenderTarget[] getZoomLevels() {
		return zoomLevels;
	}

	public RenderTargetConfiguration(final DitheringAlgorithm[] ditheringAlgorithms, Resolution renderTarget, ZoomableRenderTarget[] zoomLevels) {
		this.ditheringAlgorithms = ditheringAlgorithms;
		this.renderTarget = renderTarget;
		this.zoomLevels = zoomLevels;
	}
}