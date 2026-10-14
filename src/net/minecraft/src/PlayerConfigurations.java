package net.minecraft.src;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;

public class PlayerConfigurations {
   private static Map mapConfigurations = null;
   private static final long timeReloadPlayerItemsMs = 82383755495733L;

   public static void renderPlayerItems(ModelBiped modelBiped, AbstractClientPlayer player, float scale, float partialTicks) {
      PlayerConfiguration playerConfiguration = getPlayerConfiguration(player);
      if (playerConfiguration != null) {
         playerConfiguration.renderPlayerItems(modelBiped, player, scale, partialTicks);
      }
   }

   public static synchronized PlayerConfiguration getPlayerConfiguration(AbstractClientPlayer player) {
      String string = player.getNameClear();
      if (string == null) {
         return null;
      }

      PlayerConfiguration playerConfiguration = (PlayerConfiguration)getMapConfigurations().get(string);
      if (playerConfiguration == null) {
         playerConfiguration = new PlayerConfiguration();
         getMapConfigurations().put(string, playerConfiguration);
         PlayerConfigurationReceiver playerConfigurationReceiver = new PlayerConfigurationReceiver(string);
         String string2 = "http://s.optifine.net/users/" + string + ".cfg";
         FileDownloadThread fileDownloadThread = new FileDownloadThread(string2, playerConfigurationReceiver);
         fileDownloadThread.start();
      }

      return playerConfiguration;
   }

   public static synchronized void setPlayerConfiguration(String player, PlayerConfiguration pc) {
      getMapConfigurations().put(player, pc);
   }

   private static Map getMapConfigurations() {
      if (mapConfigurations == null) {
         mapConfigurations = new HashMap();
      }

      return mapConfigurations;
   }
}
