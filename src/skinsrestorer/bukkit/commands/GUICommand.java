package skinsrestorer.bukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import skinsrestorer.bukkit.SkinsGUI;
import skinsrestorer.shared.storage.Locale;
import skinsrestorer.shared.utils.C;

public class GUICommand implements org.bukkit.command.CommandExecutor
{
  public GUICommand() {}
  
  public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args)
  {
    if (!(sender instanceof Player)) {
      sender.sendMessage(C.c(Locale.PLAYERS_ONLY));
      return true;
    }
    
    Player p = (Player)sender;
    
    if (!p.hasPermission("skinsrestorer.playercmds")) {
      p.sendMessage(Locale.PLAYER_HAS_NO_PERMISSION);
      return true;
    }
    SkinsGUI.getMenus().put(p.getName(), Integer.valueOf(0));
    p.openInventory(SkinsGUI.getGUI(0));
    p.sendMessage(C.c(Locale.MENU_OPEN));
    return false;
  }
}
