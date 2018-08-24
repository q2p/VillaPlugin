package q2p.quickclick;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public final class TitleSender {
	public static void sendTitle(final Player player, String text, boolean isSubTitle, final ChatColor color, int fadeInTicks, int holdTicks, int fadeOutTicks) {
		IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text + "\",color:" + color.name().toLowerCase() + "}");

		PacketPlayOutTitle title = new PacketPlayOutTitle(isSubTitle ? PacketPlayOutTitle.EnumTitleAction.SUBTITLE : PacketPlayOutTitle.EnumTitleAction.TITLE, chatTitle);
		PacketPlayOutTitle length = new PacketPlayOutTitle(fadeInTicks, holdTicks, fadeOutTicks);

		((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
	}
	public static void resetTitle(final Player player) {
		IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \" \",color:" + ChatColor.RESET.toString().toLowerCase() + "}");

		PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.RESET, chatTitle);
		PacketPlayOutTitle length = new PacketPlayOutTitle(0, 1, 0);

		((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
	}
}