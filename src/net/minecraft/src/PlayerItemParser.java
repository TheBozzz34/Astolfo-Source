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
   public static final String ITEM_TYPE = "type";
   public static final String ITEM_TEXTURE_SIZE = "textureSize";
   public static final String ITEM_USE_PLAYER_TEXTURE = "usePlayerTexture";
   public static final String ITEM_MODELS = "models";
   public static final String MODEL_ID = "id";
   public static final String MODEL_BASE_ID = "baseId";
   public static final String MODEL_TYPE = "type";
   public static final String MODEL_ATTACH_TO = "attachTo";
   public static final String MODEL_INVERT_AXIS = "invertAxis";
   public static final String x = "mirrorTexture";
   public static final String z = "translate";
   public static final String y = "rotate";
   public static final String MODEL_SCALE = "scale";
   public static final String MODEL_BOXES = "boxes";
   public static final String MODEL_SPRITES = "sprites";
   public static final String MODEL_SUBMODEL = "submodel";
   public static final String MODEL_SUBMODELS = "submodels";
   public static final String BOX_TEXTURE_OFFSET = "textureOffset";
   public static final String BOX_COORDINATES = "coordinates";
   public static final String BOX_SIZE_ADD = "sizeAdd";
   public static final String ITEM_TYPE_MODEL = "PlayerItem";
   public static final String MODEL_TYPE_BOX = "ModelBox";

   public static PlayerItemModel parseItemModel(JsonObject obj) {
      String string = Json.getString(obj, "type");
      if (!Config.equals(string, "PlayerItem")) {
         throw new JsonParseException("Unknown model type: " + string);
      }

      int[] ints = Json.parseIntArray(obj.get("textureSize"), 2);
      checkNull(ints, "Missing texture size");
      Dimension dimension = new Dimension(ints[0], ints[1]);
      boolean bl = Json.getBoolean(obj, "usePlayerTexture", false);
      JsonArray jsonArray = (JsonArray)obj.get("models");
      checkNull(jsonArray, "Missing elements");
      HashMap hashMap = new HashMap();
      ArrayList arrayList = new ArrayList();
      new ArrayList();

      for (int i = 0; i < jsonArray.size(); i++) {
         JsonObject jsonObject = (JsonObject)jsonArray.get(i);
         String string2 = Json.getString(jsonObject, "baseId");
         if (string2 != null) {
            JsonObject jsonObject2 = (JsonObject)hashMap.get(string2);
            if (jsonObject2 == null) {
               Config.warn("BaseID not found: " + string2);
               continue;
            }

            for (Entry entry : jsonObject2.entrySet()) {
               if (!jsonObject.has((String)entry.getKey())) {
                  jsonObject.add((String)entry.getKey(), (JsonElement)entry.getValue());
               }
            }
         }

         String string3 = Json.getString(jsonObject, "id");
         if (string3 != null) {
            if (!hashMap.containsKey(string3)) {
               hashMap.put(string3, jsonObject);
            } else {
               Config.warn("Duplicate model ID: " + string3);
            }
         }

         PlayerItemRenderer playerItemRenderer = parseItemRenderer(jsonObject, dimension);
         if (playerItemRenderer != null) {
            arrayList.add(playerItemRenderer);
         }
      }

      PlayerItemRenderer[] playerItemRenderers = (net.minecraft.src.PlayerItemRenderer[])(arrayList.toArray(new PlayerItemRenderer[arrayList.size()]));
      return new PlayerItemModel(dimension, bl, playerItemRenderers);
   }

   private static void checkNull(Object obj, String msg) {
      if (obj == null) {
         throw new JsonParseException(msg);
      }
   }

   private static int parseAttachModel(String attachModelStr) {
      if (attachModelStr == null) {
         return 0;
      }

      if (attachModelStr.equals("body")) {
         return 0;
      }

      if (attachModelStr.equals("head")) {
         return 1;
      }

      if (attachModelStr.equals("leftArm")) {
         return 2;
      }

      if (attachModelStr.equals("rightArm")) {
         return 3;
      }

      if (attachModelStr.equals("leftLeg")) {
         return 4;
      }

      if (attachModelStr.equals("rightLeg")) {
         return 5;
      }

      if (attachModelStr.equals("cape")) {
         return 6;
      }

      Config.warn("Unknown attachModel: " + attachModelStr);
      return 0;
   }

   private static PlayerItemRenderer parseItemRenderer(JsonObject elem, Dimension textureDim) {
      String string = Json.getString(elem, "type");
      if (!Config.equals(string, "ModelBox")) {
         Config.warn("Unknown model type: " + string);
         return null;
      } else {
         String string2 = Json.getString(elem, "attachTo");
         int i = parseAttachModel(string2);
         float f = Json.getFloat(elem, "scale", 1.0F);
         ModelPlayerItem modelPlayerItem = new ModelPlayerItem();
         modelPlayerItem.textureWidth = textureDim.width;
         modelPlayerItem.textureHeight = textureDim.height;
         ModelRenderer modelRenderer = parseModelRenderer(elem, modelPlayerItem);
         return new PlayerItemRenderer(i, f, modelRenderer);
      }
   }

   private static ModelRenderer parseModelRenderer(JsonObject jsonObject, ModelBase modelBase) {
      ModelRenderer modelRenderer = new ModelRenderer(modelBase);
      String string = Json.getString(jsonObject, "invertAxis", "").toLowerCase();
      boolean bl = string.contains("x");
      boolean bl2 = string.contains("y");
      boolean bl3 = string.contains("z");
      float[] floats = Json.parseFloatArray(jsonObject.get("translate"), 3, new float[3]);
      if (bl) {
         floats[0] = -floats[0];
      }

      if (bl2) {
         floats[1] = -floats[1];
      }

      if (bl3) {
         floats[2] = -floats[2];
      }

      float[] floats2 = Json.parseFloatArray(jsonObject.get("rotate"), 3, new float[3]);

      for (int i = 0; i < floats2.length; i++) {
         floats2[i] = floats2[i] / 180.0F * (float) Math.PI;
      }

      if (bl) {
         floats2[0] = -floats2[0];
      }

      if (bl2) {
         floats2[1] = -floats2[1];
      }

      if (bl3) {
         floats2[2] = -floats2[2];
      }

      modelRenderer.setRotationPoint(floats[0], floats[1], floats[2]);
      modelRenderer.rotateAngleX = floats2[0];
      modelRenderer.rotateAngleY = floats2[1];
      modelRenderer.rotateAngleZ = floats2[2];
      String string2 = Json.getString(jsonObject, "mirrorTexture", "").toLowerCase();
      boolean bl4 = string2.contains("u");
      boolean bl5 = string2.contains("v");
      if (bl4) {
         modelRenderer.mirror = true;
      }

      if (bl5) {
         modelRenderer.mirrorV = true;
      }

      JsonArray jsonArray = jsonObject.getAsJsonArray("boxes");
      if (jsonArray != null) {
         for (int j = 0; j < jsonArray.size(); j++) {
            JsonObject jsonObject2 = jsonArray.get(j).getAsJsonObject();
            int[] ints = Json.parseIntArray(jsonObject2.get("textureOffset"), 2);
            if (ints == null) {
               throw new JsonParseException("Texture offset not specified");
            }

            float[] floats3 = Json.parseFloatArray(jsonObject2.get("coordinates"), 6);
            if (floats3 == null) {
               throw new JsonParseException("Coordinates not specified");
            }

            if (bl) {
               floats3[0] = -floats3[0] - floats3[3];
            }

            if (bl2) {
               floats3[1] = -floats3[1] - floats3[4];
            }

            if (bl3) {
               floats3[2] = -floats3[2] - floats3[5];
            }

            float f = Json.getFloat(jsonObject2, "sizeAdd", 0.0F);
            modelRenderer.setTextureOffset(ints[0], ints[1]);
            modelRenderer.addBox(floats3[0], floats3[1], floats3[2], (int)floats3[3], (int)floats3[4], (int)floats3[5], f);
         }
      }

      JsonArray jsonArray2 = jsonObject.getAsJsonArray("sprites");
      if (jsonArray2 != null) {
         for (int k = 0; k < jsonArray2.size(); k++) {
            JsonObject jsonObject3 = jsonArray2.get(k).getAsJsonObject();
            int[] ints2 = Json.parseIntArray(jsonObject3.get("textureOffset"), 2);
            if (ints2 == null) {
               throw new JsonParseException("Texture offset not specified");
            }

            float[] floats4 = Json.parseFloatArray(jsonObject3.get("coordinates"), 6);
            if (floats4 == null) {
               throw new JsonParseException("Coordinates not specified");
            }

            if (bl) {
               floats4[0] = -floats4[0] - floats4[3];
            }

            if (bl2) {
               floats4[1] = -floats4[1] - floats4[4];
            }

            if (bl3) {
               floats4[2] = -floats4[2] - floats4[5];
            }

            float g = Json.getFloat(jsonObject3, "sizeAdd", 0.0F);
            modelRenderer.setTextureOffset(ints2[0], ints2[1]);
            modelRenderer.addSprite(floats4[0], floats4[1], floats4[2], (int)floats4[3], (int)floats4[4], (int)floats4[5], g);
         }
      }

      JsonObject jsonObject4 = (JsonObject)jsonObject.get("submodel");
      if (jsonObject4 != null) {
         ModelRenderer modelRenderer2 = parseModelRenderer(jsonObject4, modelBase);
         modelRenderer.addChild(modelRenderer2);
      }

      JsonArray jsonArray3 = (JsonArray)jsonObject.get("submodels");
      if (jsonArray3 != null) {
         for (int l = 0; l < jsonArray3.size(); l++) {
            JsonObject jsonObject5 = (JsonObject)jsonArray3.get(l);
            ModelRenderer modelRenderer3 = parseModelRenderer(jsonObject5, modelBase);
            modelRenderer.addChild(modelRenderer3);
         }
      }

      return modelRenderer;
   }
}
