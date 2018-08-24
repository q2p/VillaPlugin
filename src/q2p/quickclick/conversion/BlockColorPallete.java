package q2p.quickclick.conversion;

import org.bukkit.Material;
import org.bukkit.map.MapPalette;
import q2p.quickclick.Abort;
import q2p.quickclick.MainSerializer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class BlockColorPallete implements ColorPallete {
	public static final ColorPallete instance = new BlockColorPallete();
	private BlockColorPallete() {}

	private static final TextureWithID[] textures = {
		new TextureWithID(35,  0, "wool_colored_white.png"),
		new TextureWithID(35,  1, "wool_colored_orange.png"),
		new TextureWithID(35,  2, "wool_colored_magenta.png"),
		new TextureWithID(35,  3, "wool_colored_light_blue.png"),
		new TextureWithID(35,  4, "wool_colored_yellow.png"),
		new TextureWithID(35,  5, "wool_colored_lime.png"),
		new TextureWithID(35,  6, "wool_colored_pink.png"),
		new TextureWithID(35,  7, "wool_colored_gray.png"),
		new TextureWithID(35,  8, "wool_colored_silver.png"),
		new TextureWithID(35,  9, "wool_colored_cyan.png"),
		new TextureWithID(35, 10, "wool_colored_purple.png"),
		new TextureWithID(35, 11, "wool_colored_blue.png"),
		new TextureWithID(35, 12, "wool_colored_brown.png"),
		new TextureWithID(35, 13, "wool_colored_green.png"),
		new TextureWithID(35, 14, "wool_colored_red.png"),
		new TextureWithID(35, 15, "wool_colored_black.png"),

		new TextureWithID(159,  0, "hardened_clay_stained_white.png"),
		new TextureWithID(159,  1, "hardened_clay_stained_orange.png"),
		new TextureWithID(159,  2, "hardened_clay_stained_magenta.png"),
		new TextureWithID(159,  3, "hardened_clay_stained_light_blue.png"),
		new TextureWithID(159,  4, "hardened_clay_stained_yellow.png"),
		new TextureWithID(159,  5, "hardened_clay_stained_lime.png"),
		new TextureWithID(159,  6, "hardened_clay_stained_pink.png"),
		new TextureWithID(159,  7, "hardened_clay_stained_gray.png"),
		new TextureWithID(159,  8, "hardened_clay_stained_silver.png"),
		new TextureWithID(159,  9, "hardened_clay_stained_cyan.png"),
		new TextureWithID(159, 10, "hardened_clay_stained_purple.png"),
		new TextureWithID(159, 11, "hardened_clay_stained_blue.png"),
		new TextureWithID(159, 12, "hardened_clay_stained_brown.png"),
		new TextureWithID(159, 13, "hardened_clay_stained_green.png"),
		new TextureWithID(159, 14, "hardened_clay_stained_red.png"),
		new TextureWithID(159, 15, "hardened_clay_stained_black.png"),

		new TextureWithID(1, 0, "stone.png"),
		new TextureWithID(155, 0, "quartz_block_side.png"),
		//new TextureWithID(78, 7, "snow.png"),
		//new TextureWithID(152, 0, "redstone_block.png"),
	};

	private static final byte[] tableFromFull = new byte[256*256*256];
	private static final short[][] tableToFullRGB = new short[3][textures.length];
	static {
		final Path cachePath = MainSerializer.resourcesPath().resolve("block_rgb_cache");

		try(FileChannel fc = FileChannel.open(cachePath, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
			ByteBuffer tff = ByteBuffer.wrap(tableFromFull);
			ByteBuffer ttrgb = ByteBuffer.allocate(textures.length*3);
			if(fc.size() == tff.capacity() + ttrgb.capacity()) {
				while(ttrgb.hasRemaining())
					fc.read(ttrgb);
				while(tff.hasRemaining())
					fc.read(tff);
				ttrgb.clear();
				for(short id = 0; id != textures.length; id++) {
					tableToFullRGB[0][id] = (short) (0xFF & ttrgb.get());
					tableToFullRGB[1][id] = (short) (0xFF & ttrgb.get());
					tableToFullRGB[2][id] = (short) (0xFF & ttrgb.get());
				}
			} else {
				System.out.println("Started building RGB cache");

				final short[] outRGB = new short[3];
				for(short id = 0; id != textures.length; id++) {
					final TextureWithID tid = textures[id];
					System.out.println("Parsing image: " + tid.textureName);
					InputStream is = BlockColorPallete.class.getResourceAsStream("/textures/"+tid.textureName);
					BufferedImage bimg = ImageIO.read(is);
					is.close();
					final int[] buffer = bimg.getRGB(0, 0, bimg.getWidth(), bimg.getHeight(), null, 0, bimg.getWidth());

					getDominantColor(buffer, outRGB);

					tableToFullRGB[0][id] = outRGB[0];
					tableToFullRGB[1][id] = outRGB[1];
					tableToFullRGB[2][id] = outRGB[2];

					ttrgb.put((byte) outRGB[0]);
					ttrgb.put((byte) outRGB[1]);
					ttrgb.put((byte) outRGB[2]);
				}

				for(short r = 0; r != 256; r++) {
					int rIdx = r << 16;
					for(short g = 0; g != 256; g++) {
						if(g % 4 == 0)
							System.out.println("Done for " + (r*100/256)+"% "+g);
						int gIdx = rIdx | (g << 8);
						for(short b = 0; b != 256; b++) {
							tableFromFull[gIdx|b] = (byte) findClosest(r, g, b);
						}
					}
				}

				ttrgb.clear();
				while(ttrgb.hasRemaining())
					fc.write(ttrgb);
				while(tff.hasRemaining())
					fc.write(tff);
				fc.force(true);
				System.out.println("Finished building RGB cache");
			}
		} catch(final Throwable t) {
			Abort.message("Не удалось создать / прочитать кэш цветов", t);
		}
	}

	private static int findClosest(short r, short g, short b) {
		/*byte bestId = 0;
		int bestDist = Integer.MAX_VALUE;
		for(int i = textures.length - 1; i != -1; i--) {
			int dr = r-tableToFullRGB[0][i];
			int dg = g-tableToFullRGB[1][i];
			int db = b-tableToFullRGB[2][i];

			int dist = dr*dr + dg*dg + db*db;

			if(dist < bestDist) {
				bestDist = dist;
				bestId = (byte) i;
			}
		}*/


		int index = 0;
		double best = -1;

		for(int i = 4; i < textures.length; i++) {
			double distance = getDistance(tableToFullRGB[0][i], tableToFullRGB[1][i], tableToFullRGB[2][i], r, g, b);
			if(distance < best || best == -1) {
				best = distance;
				index = i;
			}
		}

		return index;
	}

	private static double getDistance(short r1, short g1, short b1, short r2, short g2, short b2) {
		double rmean = (r1 + r2) / 2.0;
		double r = r1 - r2;
		double g = g1 - g2;
		int b = b1 - b2;
		double weightR = 2 + rmean / 256.0;
		double weightG = 4.0;
		double weightB = 2 + (255 - rmean) / 256.0;
		return weightR * r * r + weightG * g * g + weightB * b * b;
	}

	private static void getDominantColor(final int[] imagePixelData, final short[] outRGB) {
		long r = 0;
		long g = 0;
		long b = 0;
		for(int i = imagePixelData.length-1; i != -1; i--) {
			final int pixel = imagePixelData[i];
			r += 0xFF & (pixel >>> 16);
			g += 0xFF & (pixel >>>  8);
			b += 0xFF & (pixel       );
		}
		outRGB[0] = (short) (0xFF & (r / imagePixelData.length));
		outRGB[1] = (short) (0xFF & (g / imagePixelData.length));
		outRGB[2] = (short) (0xFF & (b / imagePixelData.length));
	}

	public static void ridToData(byte[] rids, byte[] data) {
		for(int i = rids.length - 1; i != -1; i--)
			data[i] = textures[rids[i]].data;
	}

	public static void ridToAid(byte[] rids, byte[] aids) {
		for(int i = rids.length - 1; i != -1; i--)
			aids[i] = textures[rids[i]].id;
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

	private static class TextureWithID {
		private final byte id;
		private final byte data;
		private final String textureName;

		private TextureWithID(int id, int data, String textureName) {
			this.id = (byte) id;
			this.data = (byte) data;
			this.textureName = textureName;
		}
	}
}