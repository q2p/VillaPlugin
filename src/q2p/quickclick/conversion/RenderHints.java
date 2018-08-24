package q2p.quickclick.conversion;

import q2p.quickclick.conversion.dithering.DitheringAlgorithm;

import java.util.Map;
import java.util.TreeMap;

public final class RenderHints {
	private static final Map<CUID, RenderHints> usedFiles = new TreeMap<>();

	public final CUID guid;
	public final DitheringAlgorithm ditheringAlgorithm;

	private RenderHints(final CUID guid, final DitheringAlgorithm ditheringAlgorithm) {
		this.guid = guid;
		this.ditheringAlgorithm = ditheringAlgorithm;
	}

	public static RenderHints create(final DitheringAlgorithm ditheringAlgorithm) {
		assert ditheringAlgorithm != null;

		final CUID guid = new CUID(ditheringAlgorithm.uid);

		if(usedFiles.containsKey(guid))
			return usedFiles.get(guid);

		final RenderHints ret = new RenderHints(guid, ditheringAlgorithm);
		usedFiles.put(guid, ret);

		return ret;
	}
}
