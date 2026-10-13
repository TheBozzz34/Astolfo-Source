package net.minecraft.src;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;

public class PlayerConfigurations {
   private static Map mapConfigurations = null;
   private static final long timeReloadPlayerItemsMs = 82383755495733L;

   public static void renderPlayerItems(ModelBiped var0, AbstractClientPlayer var1, float var2, float var3) {
      PlayerConfiguration var7 = getPlayerConfiguration(var1);
      if (var7 != null) {
         var7.renderPlayerItems(var0, var1, var2, var3);
      }
   }

   public static synchronized PlayerConfiguration getPlayerConfiguration(AbstractClientPlayer var0) {
      String var4 = var0.getNameClear();
      if (var4 == null) {
         return null;
      }

      PlayerConfiguration var5 = (PlayerConfiguration)getMapConfigurations().get(var4);
      if (var5 == null) {
         var5 = new PlayerConfiguration();
         getMapConfigurations().put(var4, var5);
         PlayerConfigurationReceiver var6 = new PlayerConfigurationReceiver(var4);
         String var7 = "http://s.optifine.net/users/" + var4 + ".cfg";
         FileDownloadThread var8 = new FileDownloadThread(var7, var6);
         var8.start();
      }

      return var5;
   }

   public static synchronized void setPlayerConfiguration(String var0, PlayerConfiguration var1) {
      getMapConfigurations().put(var0, var1);
   }

   private static Map getMapConfigurations() {
      if (mapConfigurations == null) {
         mapConfigurations = new HashMap();
      }

      return mapConfigurations;
   }
}
