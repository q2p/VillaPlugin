package skinsrestorer.bukkit.skinfactory;

import org.bukkit.entity.Player;

public class SkinFactory_v1_7_R4 extends SkinFactory { public SkinFactory_v1_7_R4() {}
  
  public void updateSkin(Player p) { net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo removeInfo;
    net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy removeEntity;
    net.minecraft.server.v1_7_R4.PacketPlayOutNamedEntitySpawn addNamed;
    net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo addInfo;
    net.minecraft.server.v1_7_R4.PacketPlayOutRespawn respawn;
    net.minecraft.server.v1_7_R4.PacketPlayOutPosition pos;
    net.minecraft.server.v1_7_R4.PacketPlayOutEntityEquipment itemhand;
    net.minecraft.server.v1_7_R4.PacketPlayOutEntityEquipment helmet;
    net.minecraft.server.v1_7_R4.PacketPlayOutEntityEquipment chestplate;
    net.minecraft.server.v1_7_R4.PacketPlayOutEntityEquipment leggings;
    net.minecraft.server.v1_7_R4.PacketPlayOutEntityEquipment boots; net.minecraft.server.v1_7_R4.PacketPlayOutHeldItemSlot slot; try { if (!p.isOnline()) {
        return;
      }
      org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer cp = (org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer)p;
      net.minecraft.server.v1_7_R4.EntityPlayer ep = cp.getHandle();
      int entId = ep.getId();
      org.bukkit.Location l = p.getLocation();
      
      removeInfo = net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo.removePlayer(ep);
      
      removeEntity = new net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy(new int[] { entId });
      
      addNamed = new net.minecraft.server.v1_7_R4.PacketPlayOutNamedEntitySpawn(ep);
      
      addInfo = net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo.addPlayer(ep);
      


      respawn = new net.minecraft.server.v1_7_R4.PacketPlayOutRespawn(getWorlddimension, getWorlddifficulty, getWorldworldData.getType(), net.minecraft.server.v1_7_R4.EnumGamemode.getById(p.getGameMode().getValue()));
      

      pos = new net.minecraft.server.v1_7_R4.PacketPlayOutPosition(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch(), false);
      

      itemhand = new net.minecraft.server.v1_7_R4.PacketPlayOutEntityEquipment(entId, 0, org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack.asNMSCopy(p.getItemInHand()));
      

      helmet = new net.minecraft.server.v1_7_R4.PacketPlayOutEntityEquipment(entId, 4, org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack.asNMSCopy(p.getInventory().getHelmet()));
      

      chestplate = new net.minecraft.server.v1_7_R4.PacketPlayOutEntityEquipment(entId, 3, org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack.asNMSCopy(p.getInventory().getChestplate()));
      

      leggings = new net.minecraft.server.v1_7_R4.PacketPlayOutEntityEquipment(entId, 2, org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack.asNMSCopy(p.getInventory().getLeggings()));
      

      boots = new net.minecraft.server.v1_7_R4.PacketPlayOutEntityEquipment(entId, 1, org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack.asNMSCopy(p.getInventory().getBoots()));
      
      slot = new net.minecraft.server.v1_7_R4.PacketPlayOutHeldItemSlot(p.getInventory().getHeldItemSlot());
      
      for (Player pOnline : org.bukkit.Bukkit.getServer().getOnlinePlayers()) {
        final org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer craftOnline = (org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer)pOnline;
        net.minecraft.server.v1_7_R4.PlayerConnection con = getHandleplayerConnection;
        if (pOnline.equals(p)) {
          con.sendPacket(removeInfo);
          con.sendPacket(addInfo);
          con.sendPacket(respawn);
          con.sendPacket(pos);
          con.sendPacket(slot);
          craftOnline.updateScaledHealth();
          craftOnline.getHandle().triggerHealthUpdate();
          craftOnline.updateInventory();
          if (pOnline.isOp())
          {
            pOnline.setOp(false);
            pOnline.setOp(true);
          }
          org.bukkit.Bukkit.getScheduler().runTask(skinsrestorer.bukkit.SkinsRestorer.getInstance(), new Runnable()
          {
            public void run()
            {
              craftOnline.getHandle().updateAbilities();

            }
            


          });


        }
        else if ((pOnline.canSee(p)) && (pOnline.getWorld().equals(p.getWorld()))) {
          con.sendPacket(removeEntity);
          con.sendPacket(removeInfo);
          con.sendPacket(addInfo);
          con.sendPacket(addNamed);
          con.sendPacket(itemhand);
          con.sendPacket(helmet);
          con.sendPacket(chestplate);
          con.sendPacket(leggings);
          con.sendPacket(boots);
        }
        else {
          con.sendPacket(removeInfo);
          con.sendPacket(addInfo);
        }
      }
    }
    catch (Exception localException) {}
  }
}
