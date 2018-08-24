package q2p.quickclick.conversion;

import q2p.quickclick.Log;
import q2p.quickclick.conversion.dithering.DitheringAlgorithm;
import q2p.quickclick.help.DisplayableException;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class VillaTest {
	public static void main(final String[] args) {
		Logger log = Logger.getLogger("clsLogger");
		log.setUseParentHandlers(false);
		log.setLevel(Level.ALL);
		log.addHandler(new Handler() {
			public void publish(LogRecord record) {
				System.out.println(record.getMessage());
			}
			public void flush() {
				System.out.flush();
			}
			public void close() throws SecurityException {}
		});
		Log.initLog(log);

		WorkStation workStation = new WorkStation();
		try {
			InputSource.importImageSequence(workStation, "fshc/", 0, -1, null);
		} catch(DisplayableException e) {
			e.printStackTrace();
			return;
		}
		workStation.addDitheringAlgorithm(DitheringAlgorithm.defaultDithering);
		workStation.addDitheringAlgorithm(DitheringAlgorithm.disabled);

		final ZoomControl zoomControl = new ZoomControl();
		zoomControl.fitImage(true);
		zoomControl.keepOriginalSize(true);
		zoomControl.setAmountOfZoomLevels(2); // 7
		zoomControl.stretchImage(true);
		zoomControl.setDeepestZoom(2); // 5
		workStation.addRenderTarget(new Resolution(1024, 512));
		workStation.addOutputFrameRate(new FrameRate(20, 1));

		workStation.doTheJob();
	}
}