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

   public ServerList(Minecraft mcIn) {
      this.mc = mcIn;
      this.loadServerList();
   }

   public void loadServerList() {
      try {
         this.servers.clear();
         NBTTagCompound nbtTagCompound = CompressedStreamTools.read(new File(this.mc.mcDataDir, "servers.dat"));
         if (nbtTagCompound == null) {
            return;
         }

         NBTTagList nbtTagList = nbtTagCompound.getTagList("servers", 10);

         for (int i = 0; i < nbtTagList.tagCount(); i++) {
            this.servers.add(ServerData.getServerDataFromNBTCompound(nbtTagList.getCompoundTagAt(i)));
         }
      } catch (Exception exception) {
         logger.error("Couldn't load server list", exception);
      }
   }

   public void saveServerList() {
      try {
         NBTTagList nbtTagList = new NBTTagList();

         for (ServerData serverData : this.servers) {
            nbtTagList.appendTag(serverData.getNBTCompound());
         }

         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         nbtTagCompound.setTag("servers", nbtTagList);
         CompressedStreamTools.safeWrite(nbtTagCompound, new File(this.mc.mcDataDir, "servers.dat"));
      } catch (Exception exception) {
         logger.error("Couldn't save server list", exception);
      }
   }

   public ServerData getServerData(int index) {
      return this.servers.get(index);
   }

   public void removeServerData(int index) {
      this.servers.remove(index);
   }

   public void addServerData(ServerData server) {
      this.servers.add(server);
   }

   public int countServers() {
      return this.servers.size();
   }

   public void swapServers(int i, int j) {
      ServerData serverData = this.getServerData(i);
      this.servers.set(i, this.getServerData(j));
      this.servers.set(j, serverData);
      this.saveServerList();
   }

   public void func_147413_a(int index, ServerData server) {
      this.servers.set(index, server);
   }

   public static void func_147414_b(ServerData serverData) {
      ServerList serverList = new ServerList(Minecraft.getMinecraft());
      serverList.loadServerList();

      for (int i = 0; i < serverList.countServers(); i++) {
         ServerData serverData2 = serverList.getServerData(i);
         if (serverData2.serverName.equals(serverData.serverName) && serverData2.serverIP.equals(serverData.serverIP)) {
            serverList.func_147413_a(i, serverData);
            break;
         }
      }

      serverList.saveServerList();
   }
}
