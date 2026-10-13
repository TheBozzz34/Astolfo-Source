package net.minecraft.src;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class PlayerConfigurationReceiver implements IFileDownloadListener {
   private String player = null;
   private static final long a = 133311146099872L;

   public PlayerConfigurationReceiver(String var1) {
      this.player = var1;
   }

   @Override
   public void fileDownloadFinished(String var1, byte[] var2, Throwable var3) {
      if (var2 != null) {
         try {
            String var7 = new String(var2, "ASCII");
            JsonParser var8 = new JsonParser();
            JsonElement var9 = var8.parse(var7);
            PlayerConfigurationParser var10 = new PlayerConfigurationParser(this.player);
            PlayerConfiguration var11 = var10.parsePlayerConfiguration(var9);
            if (var11 != null) {
               var11.setInitialized(true);
               PlayerConfigurations.setPlayerConfiguration(this.player, var11);
            }
         } catch (Exception var12) {
            Config.dbg("Error parsing configuration: " + var1 + ", " + var12.getClass().getName() + ": " + var12.getMessage());
         }
      }
   }
}
