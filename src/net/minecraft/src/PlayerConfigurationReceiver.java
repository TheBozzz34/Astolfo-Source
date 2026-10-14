package net.minecraft.src;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class PlayerConfigurationReceiver implements IFileDownloadListener {
   private String player = null;

   public PlayerConfigurationReceiver(String player) {
      this.player = player;
   }

   @Override
   public void fileDownloadFinished(String url, byte[] bytes, Throwable exception) {
      if (bytes != null) {
         try {
            String string = new String(bytes, "ASCII");
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(string);
            PlayerConfigurationParser playerConfigurationParser = new PlayerConfigurationParser(this.player);
            PlayerConfiguration playerConfiguration = playerConfigurationParser.parsePlayerConfiguration(jsonElement);
            if (playerConfiguration != null) {
               playerConfiguration.setInitialized(true);
               PlayerConfigurations.setPlayerConfiguration(this.player, playerConfiguration);
            }
         } catch (Exception exception2) {
            Config.dbg("Error parsing configuration: " + url + ", " + exception2.getClass().getName() + ": " + exception2.getMessage());
         }
      }
   }
}
