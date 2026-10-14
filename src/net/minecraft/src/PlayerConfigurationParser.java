package net.minecraft.src;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class PlayerConfigurationParser {
   private String player = null;
   public static final String CONFIG_ITEMS = "items";
   public static final String ITEM_TYPE = "type";
   public static final String ITEM_ACTIVE = "active";

   public PlayerConfigurationParser(String player) {
      this.player = player;
   }

   public PlayerConfiguration parsePlayerConfiguration(JsonElement je) {
      if (je == null) {
         throw new JsonParseException("JSON object is null, player: " + this.player);
      }

      JsonObject jsonObject = (JsonObject)je;
      PlayerConfiguration playerConfiguration = new PlayerConfiguration();
      JsonArray jsonArray = (JsonArray)jsonObject.get("items");
      if (jsonArray != null) {
         for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject2 = (JsonObject)jsonArray.get(i);
            boolean bl = Json.getBoolean(jsonObject2, "active", true);
            if (bl) {
               String string = Json.getString(jsonObject2, "type");
               if (string == null) {
                  Config.warn("Item type is null, player: " + this.player);
               } else {
                  String string2 = Json.getString(jsonObject2, "model");
                  if (string2 == null) {
                     string2 = "items/" + string + "/model.cfg";
                  }

                  PlayerItemModel playerItemModel = this.downloadModel(string2);
                  if (playerItemModel != null) {
                     if (!playerItemModel.isUsePlayerTexture()) {
                        String string3 = Json.getString(jsonObject2, "texture");
                        if (string3 == null) {
                           string3 = "items/" + string + "/users/" + this.player + ".png";
                        }

                        BufferedImage bufferedImage = this.downloadTextureImage(string3);
                        if (bufferedImage == null) {
                           continue;
                        }

                        playerItemModel.setTextureImage(bufferedImage);
                        ResourceLocation resourceLocation = new ResourceLocation("optifine.net", string3);
                        playerItemModel.setTextureLocation(resourceLocation);
                     }

                     playerConfiguration.addPlayerItemModel(playerItemModel);
                  }
               }
            }
         }
      }

      return playerConfiguration;
   }

   private BufferedImage downloadTextureImage(String texturePath) {
      String string = "http://s.optifine.net/" + texturePath;

      try {
         byte[] bytes = HttpPipeline.get(string, Minecraft.getMinecraft().getProxy());
         return ImageIO.read(new ByteArrayInputStream(bytes));
      } catch (IOException ioException) {
         Config.warn("Error loading item texture " + texturePath + ": " + ioException.getClass().getName() + ": " + ioException.getMessage());
         return null;
      }
   }

   private PlayerItemModel downloadModel(String modelPath) {
      String string = "http://s.optifine.net/" + modelPath;

      try {
         byte[] bytes = HttpPipeline.get(string, Minecraft.getMinecraft().getProxy());
         String string2 = new String(bytes, "ASCII");
         JsonParser jsonParser = new JsonParser();
         JsonObject jsonObject = (JsonObject)jsonParser.parse(string2);
         new PlayerItemParser();
         return PlayerItemParser.parseItemModel(jsonObject);
      } catch (Exception exception) {
         Config.warn("Error loading item model " + modelPath + ": " + exception.getClass().getName() + ": " + exception.getMessage());
         return null;
      }
   }
}
