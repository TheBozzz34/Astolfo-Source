package net.minecraft.src;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class PlayerItemParser {
   private static JsonParser jsonParser = new JsonParser();
   public static final String d = "type";
   public static final String g = "textureSize";
   public static final String i = "usePlayerTexture";
   public static final String E = "models";
   public static final String F = "id";
   public static final String R = "baseId";
   public static final String r = "type";
   public static final String W = "attachTo";
   public static final String A = "invertAxis";
   public static final String x = "mirrorTexture";
   public static final String z = "translate";
   public static final String y = "rotate";
   public static final String t = "scale";
   public static final String b = "boxes";
   public static final String k = "sprites";
   public static final String o = "submodel";
   public static final String V = "submodels";
   public static final String m = "textureOffset";
   public static final String K = "coordinates";
   public static final String w = "sizeAdd";
   public static final String u = "PlayerItem";
   public static final String S = "ModelBox";
   private static final long a = 20314234190079L;

   public static PlayerItemModel parseItemModel(JsonObject var0) {
      String var4 = Json.getString(var0, "type");
      if (!Config.equals(var4, "PlayerItem")) {
         throw new JsonParseException("Unknown model type: " + var4);
      }

      int[] var5 = Json.parseIntArray(var0.get("textureSize"), 2);
      checkNull(var5, "Missing texture size");
      Dimension var6 = new Dimension(var5[0], var5[1]);
      boolean var7 = Json.getBoolean(var0, "usePlayerTexture", false);
      JsonArray var8 = (JsonArray)var0.get("models");
      checkNull(var8, "Missing elements");
      HashMap var9 = new HashMap();
      ArrayList var10 = new ArrayList();
      new ArrayList();

      for (int var11 = 0; var11 < var8.size(); var11++) {
         JsonObject var12 = (JsonObject)var8.get(var11);
         String var13 = Json.getString(var12, "baseId");
         if (var13 != null) {
            JsonObject var14 = (JsonObject)var9.get(var13);
            if (var14 == null) {
               Config.warn("BaseID not found: " + var13);
               continue;
            }

            for (Entry var16 : var14.entrySet()) {
               if (!var12.has((String)var16.getKey())) {
                  var12.add((String)var16.getKey(), (JsonElement)var16.getValue());
               }
            }
         }

         String var18 = Json.getString(var12, "id");
         if (var18 != null) {
            if (!var9.containsKey(var18)) {
               var9.put(var18, var12);
            } else {
               Config.warn("Duplicate model ID: " + var18);
            }
         }

         PlayerItemRenderer var19 = parseItemRenderer(var12, var6);
         if (var19 != null) {
            var10.add(var19);
         }
      }

      PlayerItemRenderer[] var17 = (net.minecraft.src.PlayerItemRenderer[])(var10.toArray(new PlayerItemRenderer[var10.size()]));
      return new PlayerItemModel(var6, var7, var17);
   }

   private static void checkNull(Object var0, String var1) {
      if (var0 == null) {
         throw new JsonParseException(var1);
      }
   }

   private static int parseAttachModel(String var0) {
      if (var0 == null) {
         return 0;
      }

      if (var0.equals("body")) {
         return 0;
      }

      if (var0.equals("head")) {
         return 1;
      }

      if (var0.equals("leftArm")) {
         return 2;
      }

      if (var0.equals("rightArm")) {
         return 3;
      }

      if (var0.equals("leftLeg")) {
         return 4;
      }

      if (var0.equals("rightLeg")) {
         return 5;
      }

      if (var0.equals("cape")) {
         return 6;
      }

      Config.warn("Unknown attachModel: " + var0);
      return 0;
   }

   private static PlayerItemRenderer parseItemRenderer(JsonObject var0, Dimension var1) {
      String var5 = Json.getString(var0, "type");
      if (!Config.equals(var5, "ModelBox")) {
         Config.warn("Unknown model type: " + var5);
         return null;
      } else {
         String var6 = Json.getString(var0, "attachTo");
         int var7 = parseAttachModel(var6);
         float var8 = Json.getFloat(var0, "scale", 1.0F);
         ModelPlayerItem var9 = new ModelPlayerItem();
         var9.textureWidth = var1.width;
         var9.textureHeight = var1.height;
         ModelRenderer var10 = F(var0, var9);
         return new PlayerItemRenderer(var7, var8, var10);
      }
   }

   private static ModelRenderer F(JsonObject var0, ModelBase var1) {
      ModelRenderer var5 = new ModelRenderer(var1);
      String var6 = Json.getString(var0, "invertAxis", "").toLowerCase();
      boolean var7 = var6.contains("x");
      boolean var8 = var6.contains("y");
      boolean var9 = var6.contains("z");
      float[] var10 = Json.parseFloatArray(var0.get("translate"), 3, new float[3]);
      if (var7) {
         var10[0] = -var10[0];
      }

      if (var8) {
         var10[1] = -var10[1];
      }

      if (var9) {
         var10[2] = -var10[2];
      }

      float[] var11 = Json.parseFloatArray(var0.get("rotate"), 3, new float[3]);

      for (int var12 = 0; var12 < var11.length; var12++) {
         var11[var12] = var11[var12] / 180.0F * (float) Math.PI;
      }

      if (var7) {
         var11[0] = -var11[0];
      }

      if (var8) {
         var11[1] = -var11[1];
      }

      if (var9) {
         var11[2] = -var11[2];
      }

      var5.setRotationPoint(var10[0], var10[1], var10[2]);
      var5.rotateAngleX = var11[0];
      var5.rotateAngleY = var11[1];
      var5.rotateAngleZ = var11[2];
      String var22 = Json.getString(var0, "mirrorTexture", "").toLowerCase();
      boolean var13 = var22.contains("u");
      boolean var14 = var22.contains("v");
      if (var13) {
         var5.mirror = true;
      }

      if (var14) {
         var5.mirrorV = true;
      }

      JsonArray var15 = var0.getAsJsonArray("boxes");
      if (var15 != null) {
         for (int var16 = 0; var16 < var15.size(); var16++) {
            JsonObject var17 = var15.get(var16).getAsJsonObject();
            int[] var18 = Json.parseIntArray(var17.get("textureOffset"), 2);
            if (var18 == null) {
               throw new JsonParseException("Texture offset not specified");
            }

            float[] var19 = Json.parseFloatArray(var17.get("coordinates"), 6);
            if (var19 == null) {
               throw new JsonParseException("Coordinates not specified");
            }

            if (var7) {
               var19[0] = -var19[0] - var19[3];
            }

            if (var8) {
               var19[1] = -var19[1] - var19[4];
            }

            if (var9) {
               var19[2] = -var19[2] - var19[5];
            }

            float var20 = Json.getFloat(var17, "sizeAdd", 0.0F);
            var5.setTextureOffset(var18[0], var18[1]);
            var5.addBox(var19[0], var19[1], var19[2], (int)var19[3], (int)var19[4], (int)var19[5], var20);
         }
      }

      JsonArray var23 = var0.getAsJsonArray("sprites");
      if (var23 != null) {
         for (int var24 = 0; var24 < var23.size(); var24++) {
            JsonObject var26 = var23.get(var24).getAsJsonObject();
            int[] var29 = Json.parseIntArray(var26.get("textureOffset"), 2);
            if (var29 == null) {
               throw new JsonParseException("Texture offset not specified");
            }

            float[] var31 = Json.parseFloatArray(var26.get("coordinates"), 6);
            if (var31 == null) {
               throw new JsonParseException("Coordinates not specified");
            }

            if (var7) {
               var31[0] = -var31[0] - var31[3];
            }

            if (var8) {
               var31[1] = -var31[1] - var31[4];
            }

            if (var9) {
               var31[2] = -var31[2] - var31[5];
            }

            float var21 = Json.getFloat(var26, "sizeAdd", 0.0F);
            var5.setTextureOffset(var29[0], var29[1]);
            var5.addSprite(var31[0], var31[1], var31[2], (int)var31[3], (int)var31[4], (int)var31[5], var21);
         }
      }

      JsonObject var25 = (JsonObject)var0.get("submodel");
      if (var25 != null) {
         ModelRenderer var27 = F(var25, var1);
         var5.addChild(var27);
      }

      JsonArray var28 = (JsonArray)var0.get("submodels");
      if (var28 != null) {
         for (int var30 = 0; var30 < var28.size(); var30++) {
            JsonObject var32 = (JsonObject)var28.get(var30);
            ModelRenderer var33 = F(var32, var1);
            var5.addChild(var33);
         }
      }

      return var5;
   }
}
