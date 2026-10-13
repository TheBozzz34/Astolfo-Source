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
   private static final long a = 89943007046203L;

   public PlayerConfigurationParser(String var1) {
      this.player = var1;
   }

   public PlayerConfiguration parsePlayerConfiguration(JsonElement var1) {
      if (var1 == null) {
         throw new JsonParseException("JSON object is null, player: " + this.player);
      }

      JsonObject var5 = (JsonObject)var1;
      PlayerConfiguration var6 = new PlayerConfiguration();
      JsonArray var7 = (JsonArray)var5.get("items");
      if (var7 != null) {
         for (int var8 = 0; var8 < var7.size(); var8++) {
            JsonObject var9 = (JsonObject)var7.get(var8);
            boolean var10 = Json.getBoolean(var9, "active", true);
            if (var10) {
               String var11 = Json.getString(var9, "type");
               if (var11 == null) {
                  Config.warn("Item type is null, player: " + this.player);
               } else {
                  String var12 = Json.getString(var9, "model");
                  if (var12 == null) {
                     var12 = "items/" + var11 + "/model.cfg";
                  }

                  PlayerItemModel var13 = this.downloadModel(var12);
                  if (var13 != null) {
                     if (!var13.isUsePlayerTexture()) {
                        String var14 = Json.getString(var9, "texture");
                        if (var14 == null) {
                           var14 = "items/" + var11 + "/users/" + this.player + ".png";
                        }

                        BufferedImage var15 = this.downloadTextureImage(var14);
                        if (var15 == null) {
                           continue;
                        }

                        var13.setTextureImage(var15);
                        ResourceLocation var16 = new ResourceLocation("optifine.net", var14);
                        var13.setTextureLocation(var16);
                     }

                     var6.addPlayerItemModel(var13);
                  }
               }
            }
         }
      }

      return var6;
   }

   private BufferedImage downloadTextureImage(String var1) {
      String var4 = "http://s.optifine.net/" + var1;

      try {
         byte[] var5 = HttpPipeline.get(var4, Minecraft.getMinecraft().getProxy());
         return ImageIO.read(new ByteArrayInputStream(var5));
      } catch (IOException var7) {
         Config.warn("Error loading item texture " + var1 + ": " + var7.getClass().getName() + ": " + var7.getMessage());
         return null;
      }
   }

   private PlayerItemModel downloadModel(String var1) {
      String var4 = "http://s.optifine.net/" + var1;

      try {
         byte[] var5 = HttpPipeline.get(var4, Minecraft.getMinecraft().getProxy());
         String var6 = new String(var5, "ASCII");
         JsonParser var7 = new JsonParser();
         JsonObject var8 = (JsonObject)var7.parse(var6);
         new PlayerItemParser();
         return PlayerItemParser.parseItemModel(var8);
      } catch (Exception var11) {
         Config.warn("Error loading item model " + var1 + ": " + var11.getClass().getName() + ": " + var11.getMessage());
         return null;
      }
   }
}
