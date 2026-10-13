package net.minecraft.server.integrated;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.ServerConfigurationManager;

public class IntegratedPlayerList extends ServerConfigurationManager {
   private NBTTagCompound hostPlayerData;
   private static final long a = 91003651894983L;
   private static final long q = -1594635543558225910L;

   public IntegratedPlayerList(IntegratedServer var1) {
      super(var1);
      this.setViewDistance((int)q);
   }

   @Override
   public void writePlayerData(EntityPlayerMP var1) {
      if (var1.getName().equals(this.getServerInstance().getServerOwner())) {
         this.hostPlayerData = new NBTTagCompound();
         var1.writeToNBT(this.hostPlayerData);
      }

      super.writePlayerData(var1);
   }

   @Override
   public String allowUserToConnect(SocketAddress var1, GameProfile var2) {
      return var2.getName().equalsIgnoreCase(this.getServerInstance().getServerOwner()) && this.getPlayerByUsername(var2.getName()) != null
         ? "That name is already taken."
         : super.allowUserToConnect(var1, var2);
   }

   public IntegratedServer getServerInstance() {
      return (IntegratedServer)super.getServerInstance();
   }

   @Override
   public NBTTagCompound getHostPlayerData() {
      return this.hostPlayerData;
   }
}
