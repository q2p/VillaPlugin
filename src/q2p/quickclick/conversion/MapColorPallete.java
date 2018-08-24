package q2p.quickclick.conversion;

import org.bukkit.map.MapPalette;
import q2p.quickclick.Abort;
import q2p.quickclick.MainSerializer;

import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class MapColorPallete implements ColorPallete {
	public static final ColorPallete instance = new MapColorPallete();
	private MapColorPallete() {}

	private static final byte[] tableFromFull = new byte[256*256*256];
	private static final int[] tableToFullInteger = new int[256];
	private static final short[][] tableToFullRGB = new short[3][256];
	static {
		final Path cachePath = MainSerializer.resourcesPath().resolve("map_rgb_cache");

		try(FileChannel fc = FileChannel.open(cachePath, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
			ByteBuffer bb = ByteBuffer.wrap(tableFromFull);
			if(fc.size() == tableFromFull.length) {
				while(bb.hasRemaining())
					fc.read(bb);
			} else {
				System.out.println("Started building RGB cache");
				for(short r = 0; r != 256; r++) {
					if(r % 4 == 0)
						System.out.println("Done for " + (r*100/256)+"%");
					int rIdx = r << 16;
					EditableColor.setR(r);
					for(short g = 0; g != 256; g++) {
						int gIdx = rIdx | (g << 8);
						EditableColor.setG(g);
						for(short b = 0; b != 256; b++) {
							EditableColor.setB(b);
							byte rawId = MapPalette.matchColor(EditableColor.instance);
							tableFromFull[gIdx|b] = rawId;
						}
					}
				}
				while(bb.hasRemaining())
					fc.write(bb);
				fc.force(true);
				System.out.println("Finished building RGB cache");
			}
		} catch(final Throwable t) {
			Abort.message("Не удалось создать / прочитать кэш цветов", t);
		}
		for(short id = 0; id != 256; id++) {
			try {
				Color c = MapPalette.getColor((byte)id);
				tableToFullInteger[id] = c.getRGB();
				tableToFullRGB[0][id] = (short) c.getRed();
				tableToFullRGB[1][id] = (short) c.getGreen();
				tableToFullRGB[2][id] = (short) c.getBlue();
			} catch(final Throwable ignore) {}
		}
	}

	public void idToRGB(final byte id, final short[] output) {
		final int off = 0xFF & id;
		output[0] = tableToFullRGB[0][off];
		output[1] = tableToFullRGB[1][off];
		output[2] = tableToFullRGB[2][off];
	}

	public void idToRGB(final byte id, final byte[] output) {
		final int off = 0xFF & id;
		output[0] = (byte) tableToFullRGB[0][off];
		output[1] = (byte) tableToFullRGB[1][off];
		output[2] = (byte) tableToFullRGB[2][off];
	}

	public byte RGBtoId(final int r, final int g, final int b) {
		assert r >= 0 && r <= 255;
		assert g >= 0 && g <= 255;
		assert b >= 0 && b <= 255;

		return tableFromFull[r << 16 | g << 8 | b];
	}
}