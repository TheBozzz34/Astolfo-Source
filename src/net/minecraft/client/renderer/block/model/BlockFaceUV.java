package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;

public class BlockFaceUV {
   public float[] uvs;
   public final int rotation;

   public BlockFaceUV(float[] uvsIn, int rotationIn) {
      this.uvs = uvsIn;
      this.rotation = rotationIn;
   }

   public float func_178348_a(int i) {
      if (this.uvs == null) {
         throw new NullPointerException("uvs");
      }

      int j = this.func_178347_d(i);
      return j != 0 && j != 1 ? this.uvs[2] : this.uvs[0];
   }

   public float func_178346_b(int i) {
      if (this.uvs == null) {
         throw new NullPointerException("uvs");
      }

      int j = this.func_178347_d(i);
      return j != 0 && j != 3 ? this.uvs[3] : this.uvs[1];
   }

   private int func_178347_d(int i) {
      return (i + this.rotation / 90) % 4;
   }

   public int func_178345_c(int i) {
      return (i + (4 - this.rotation / 90)) % 4;
   }

   public void setUvs(float[] uvsIn) {
      if (this.uvs == null) {
         this.uvs = uvsIn;
      }
   }

   public static class Deserializer implements JsonDeserializer<BlockFaceUV> {
      public BlockFaceUV deserialize(JsonElement jsonElement, Type type2, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject jsonObject = jsonElement.getAsJsonObject();
         float[] floats = this.parseUV(jsonObject);
         int i = this.parseRotation(jsonObject);
         return new BlockFaceUV(floats, i);
      }

      public int parseRotation(JsonObject jsonObject) {
         int i = JsonUtils.getInt(jsonObject, "rotation", 0);
         if (i >= 0 && i % 90 == 0 && i / 90 <= 3) {
            return i;
         } else {
            throw new JsonParseException("Invalid rotation " + i + " found, only 0/90/180/270 allowed");
         }
      }

      private float[] parseUV(JsonObject jsonObject) {
         if (!jsonObject.has("uv")) {
            return null;
         }

         JsonArray jsonArray = JsonUtils.getJsonArray(jsonObject, "uv");
         if (jsonArray.size() != 4) {
            throw new JsonParseException("Expected 4 uv values, found: " + jsonArray.size());
         }

         float[] floats = new float[4];

         for (int i = 0; i < floats.length; i++) {
            floats[i] = JsonUtils.getFloat(jsonArray.get(i), "uv[" + i + "]");
         }

         return floats;
      }
   }
}
