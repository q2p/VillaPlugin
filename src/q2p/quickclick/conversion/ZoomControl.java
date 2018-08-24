package q2p.quickclick.conversion;

import java.util.Collections;
import java.util.LinkedList;

public final class ZoomControl {
	// TODO: пофиксить все места, где resolution может быть меньше 1 пикселя
	// TODO: удалять разрешения, если они повторяются в списке
	private boolean zoomFit = true;
	public void fitImage(final boolean fitImage) {
		zoomFit = fitImage;
	}
	public boolean fitImage() {
		return zoomFit;
	}
	private boolean zoomOrig = true;
	public void keepOriginalSize(final boolean keepOriginalSize) {
		zoomOrig = keepOriginalSize;
	}
	public boolean keepOriginalSize() {
		return zoomOrig;
	}
	private boolean zoomStretch = true;
	public void stretchImage(final boolean stretchImage) {
		zoomStretch = stretchImage;
	}
	public boolean stretchImage() {
		return zoomStretch;
	}
	private float deepestZoom = 3.0f;
	public float getDeepestZoom() {
		return deepestZoom;
	}
	public void setDeepestZoom(final float deepestZoom) {
		assert deepestZoom >= 1;
		this.deepestZoom = deepestZoom;
	}
	private boolean zoomCutOverflow = false;
	private int zoomSteps = 9;
	public float getAmountOfZoomLevels() {
		return zoomSteps;
	}
	public void setAmountOfZoomLevels(final int zoomLevels) {
		assert zoomSteps > 0;
		this.zoomSteps = zoomLevels;
	}
	private boolean nearestFiltering;
	private Float backgroundBlur = null;
	private int minZoomScalePercentage = 120;

	public ZoomableRenderTarget[] getListOfZoomableRenderTargets(final Resolution inputResolution, final Resolution outputResolution) {
		final LinkedList<Resolution> resolutions = getListOfResolutions(inputResolution, outputResolution);
		final ZoomableRenderTarget[] out = new ZoomableRenderTarget[resolutions.size()];

		for(int i = out.length - 1; i != -1; i--)
			out[i] = new ZoomableRenderTarget(inputResolution, resolutions.removeLast(), outputResolution, backgroundBlur);

		return out;
	}

	private LinkedList<Resolution> getListOfResolutions(final Resolution inputResolution, final Resolution outputResolution) {
		boolean outputWider = outputResolution.compareAspects(inputResolution) > 1;

		final Resolution stretchResolution = scaleResolution(inputResolution, outputResolution, outputWider);
		final Resolution fitResolution = scaleResolution(inputResolution, outputResolution, !outputWider);
		boolean usedZoomFit = zoomFit;
		boolean usedZoomOrig = zoomOrig;
		boolean usedZoomStretch = zoomStretch;

		if(usedZoomFit && usedZoomOrig && fitResolution.same(inputResolution))
			usedZoomFit = false;

		if(
			usedZoomStretch && (
				(usedZoomOrig && stretchResolution.same(inputResolution)) ||
				(usedZoomFit  && stretchResolution.same( fitResolution))
			)
		) usedZoomStretch = false;


		LinkedList<Resolution> zoomLevels = new LinkedList<>();
		if(usedZoomFit)
			zoomLevels.add(fitResolution);
		if(usedZoomOrig)
			zoomLevels.add(inputResolution);
		if(usedZoomStretch)
			zoomLevels.add(stretchResolution);

		Collections.sort(zoomLevels);

		Resolution smallestImage = zoomLevels.getFirst();
		Resolution biggestImage = zoomLevels.getLast();

		Resolution userDefinedBiggestImage = new Resolution(Math.round(fitResolution.getWidth()*deepestZoom), Math.round(fitResolution.getHeight()*deepestZoom));

		if(userDefinedBiggestImage.compareTo(biggestImage) > 0) {
			zoomLevels.addLast(userDefinedBiggestImage);
			biggestImage = userDefinedBiggestImage;
		}

		double smallestZoom = smallestImage.getZoomOf(inputResolution);

		int usedZoomSteps = Math.max(zoomSteps, zoomLevels.size());

		int factorPercentage = findBestZoomFactorPercentage(smallestZoom, biggestImage.getZoomOf(inputResolution), usedZoomSteps, minZoomScalePercentage);

		LinkedList<Resolution> scales = scaleImage(usedZoomSteps,factorPercentage/100.0d, inputResolution, smallestImage, biggestImage);

		//noinspection unchecked
		purgeSizes((LinkedList<Resolution>) zoomLevels.clone(), scales);

		final int smallestSpace = smallestImage.space();
		final int biggestSpace = biggestImage.space();

		scales.removeIf(resolution -> resolution.space() <= smallestSpace || resolution.space() >= biggestSpace);

		while(!zoomLevels.isEmpty())
			scales.addLast(zoomLevels.removeFirst());

		Collections.sort(scales);

		return scales;
	}

	private void purgeSizes(final LinkedList<Resolution> keySteps, final LinkedList<Resolution> otherSteps) {
		while(!keySteps.isEmpty()) {
			int minDiff = Integer.MAX_VALUE;
			Resolution keyMatch = null;
			Resolution stepMatch = null;
			for(Resolution keyStep : keySteps) {
				for(Resolution step : otherSteps) {
					int diff = Math.abs(keyStep.space() - step.space());
					if(diff < minDiff) {
						minDiff = diff;
						keyMatch = keyStep;
						stepMatch = step;
					}
				}
			}

			keySteps.remove(keyMatch);
			otherSteps.remove(stepMatch);
		}
	}

	private static LinkedList<Resolution> scaleImage(final int zoomSteps, final double zoomFactor, Resolution origResolution, Resolution smallestImage, Resolution biggestImage) {
		final LinkedList<Resolution> ret = new LinkedList<>();

		ret.addLast(smallestImage);

		double zoom = smallestImage.getZoomOf(origResolution)*zoomFactor;

		for(int i = zoomSteps - 2; i != 0; i--) {
			int w = Math.max(1, (int) (Math.round(origResolution.getWidth() *zoom)+0.5));
			int h = Math.max(1, (int) (Math.round(origResolution.getHeight()*zoom)+0.5));
			ret.addLast(new Resolution(w, h));
			zoom *= zoomFactor;
		}

		ret.addLast(biggestImage);

		return ret;
	}

	private static int findBestZoomFactorPercentage(double smallestZoomFactor, double biggestZoomFactor, int zoomSteps, int minZoomScalePrecentage) {
		assert minZoomScalePrecentage > 100;
		int L = minZoomScalePrecentage;
		int R = (int)Math.ceil((biggestZoomFactor * 100 / smallestZoomFactor)+2);
		if(L >= R)
			return L;

		double lDiff = checkZoom(smallestZoomFactor, biggestZoomFactor, zoomSteps, (float)L/100.0f);
		double rDiff = checkZoom(smallestZoomFactor, biggestZoomFactor, zoomSteps, (float)R/100.0f);

		while(true) {
			if(L+1 == R) {
				if(Math.abs(rDiff) < Math.abs(lDiff))
					return R;
				else
					return L;
			}
			final int m = (L + R) / 2;
			double diff = checkZoom(smallestZoomFactor, biggestZoomFactor, zoomSteps, (float)m/100.0f);
			if(diff == 0)
				return m;

			if(diff < 0) {
				L = m;
				lDiff = diff;
			} else {
				R = m;
				rDiff = diff;
			}
		}
	}

	private static double checkZoom(double smallestZoomFactor, double biggestZoomFactor, int zoomSteps, float zoomFactor) {
		double tZoom = smallestZoomFactor;
		for(int i = zoomSteps; i != 0; i--)
			tZoom *= zoomFactor;
		return tZoom - biggestZoomFactor;
	}

	public static Resolution scaleResolution(final Resolution in, final Resolution out, final boolean alignWithWidth) {
		final int w, h;
		if(alignWithWidth) {
			w = out.getWidth();
			h = in.getHeight() * out.getWidth() / in.getWidth();
		} else {
			h = out.getHeight();
			w = in.getWidth() * out.getHeight() / in.getHeight();
		}
		return new Resolution(w, h);
	}
}