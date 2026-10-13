package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerList {
   private static final Logger logger = LogManager.getLogger();
   private final Minecraft mc;
   private final List<ServerData> servers = Lists.newArrayList();
   private static final long a = 88542855831940L;
   private static final long e = 6960290456479465482L;

   public ServerList(Minecraft var1) {
      this.mc = var1;
      this.loadServerList();
   }

   public void loadServerList() {
      try {
         this.servers.clear();
         NBTTagCompound var4 = CompressedStreamTools.read(new File(this.mc.mcDataDir, "servers.dat"));
         if (var4 == null) {
            return;
         }

         NBTTagList var5 = var4.getTagList("servers", (int)e);

         for (int var6 = 0; var6 < var5.tagCount(); var6++) {
            this.servers.add(ServerData.getServerDataFromNBTCompound(var5.getCompoundTagAt(var6)));
         }
      } catch (Exception var7) {
         logger.error("Couldn't load server list", var7);
      }
   }

   public void saveServerList() {
      try {
         NBTTagList var4 = new NBTTagList();

         for (ServerData var6 : this.servers) {
            var4.appendTag(var6.getNBTCompound());
         }

         NBTTagCompound var8 = new NBTTagCompound();
         var8.setTag("servers", var4);
         CompressedStreamTools.safeWrite(var8, new File(this.mc.mcDataDir, "servers.dat"));
      } catch (Exception var7) {
         logger.error("Couldn't save server list", var7);
      }
   }

   public ServerData getServerData(int var1) {
      return this.servers.get(var1);
   }

   public void removeServerData(int var1) {
      this.servers.remove(var1);
   }

   public void addServerData(ServerData var1) {
      this.servers.add(var1);
   }

   public int countServers() {
      return this.servers.size();
   }

   public void swapServers(int var1, int var2) {
      ServerData var3 = this.getServerData(var1);
      this.servers.set(var1, this.getServerData(var2));
      this.servers.set(var2, var3);
      this.saveServerList();
   }

   public void func_147413_a(int var1, ServerData var2) {
      this.servers.set(var1, var2);
   }

   public static void func_147414_b(ServerData var0) {
      ServerList var4 = new ServerList(Minecraft.getMinecraft());
      var4.loadServerList();

      for (int var5 = 0; var5 < var4.countServers(); var5++) {
         ServerData var6 = var4.getServerData(var5);
         if (var6.serverName.equals(var0.serverName) && var6.serverIP.equals(var0.serverIP)) {
            var4.func_147413_a(var5, var0);
            break;
         }
      }

      var4.saveServerList();
   }
}
