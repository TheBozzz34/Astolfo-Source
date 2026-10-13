package net.minecraft.client.multiplayer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class ServerData {
   public String serverName;
   public String serverIP;
   public String populationInfo;
   public String serverMOTD;
   public long pingToServer;
   public int version = 47;
   public String gameVersion = "1.8.8";
   public boolean field_78841_f;
   public String playerList;
   private ServerData.ServerResourceMode resourceMode = ServerData.ServerResourceMode.PROMPT;
   private String serverIcon;
   private boolean lanServer;
   public static String I;
   private static final long c = 93143827063279L;

   public ServerData(String var1, String var2, boolean var3) {
      this.serverName = var1;
      this.serverIP = var2;
      this.lanServer = var3;
   }

   public NBTTagCompound getNBTCompound() {
      NBTTagCompound var4 = new NBTTagCompound();
      var4.setString("name", this.serverName);
      var4.setString("ip", this.serverIP);
      if (this.serverIcon != null) {
         var4.setString("icon", this.serverIcon);
      }

      if (this.resourceMode == ServerData.ServerResourceMode.ENABLED) {
         var4.setBoolean("acceptTextures", true);
      } else if (this.resourceMode == ServerData.ServerResourceMode.DISABLED) {
         var4.setBoolean("acceptTextures", false);
      }

      return var4;
   }

   public ServerData.ServerResourceMode getResourceMode() {
      return this.resourceMode;
   }

   public void setResourceMode(ServerData.ServerResourceMode var1) {
      this.resourceMode = var1;
   }

   public static ServerData getServerDataFromNBTCompound(NBTTagCompound var0) {
      ServerData var4 = new ServerData(var0.getString("name"), var0.getString("ip"), false);
      if (var0.hasKey("icon", 8)) {
         var4.setBase64EncodedIconData(var0.getString("icon"));
      }

      if (var0.hasKey("acceptTextures", 1)) {
         if (var0.getBoolean("acceptTextures")) {
            var4.setResourceMode(ServerData.ServerResourceMode.ENABLED);
         } else {
            var4.setResourceMode(ServerData.ServerResourceMode.DISABLED);
         }
      } else {
         var4.setResourceMode(ServerData.ServerResourceMode.PROMPT);
      }

      return var4;
   }

   public String getBase64EncodedIconData() {
      return this.serverIcon;
   }

   public void setBase64EncodedIconData(String var1) {
      this.serverIcon = var1;
   }

   public boolean isOnLAN() {
      return this.lanServer;
   }

   public void copyFrom(ServerData var1) {
      this.serverIP = var1.serverIP;
      this.serverName = var1.serverName;
      this.setResourceMode(var1.getResourceMode());
      this.serverIcon = var1.serverIcon;
      this.lanServer = var1.lanServer;
   }

   public enum ServerResourceMode {
      ENABLED("enabled"),
      DISABLED("disabled"),
      PROMPT("prompt");

      private final IChatComponent motd;
      private static final long a = 64732221137578L;

      ServerResourceMode(String var3) {
         this.motd = new ChatComponentTranslation("addServer.resourcePack." + var3);
      }

      public IChatComponent getMotd() {
         return this.motd;
      }
   }
}
