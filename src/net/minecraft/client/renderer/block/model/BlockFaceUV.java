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
   private static final long a = 34955891176846L;

   public BlockFaceUV(float[] var1, int var2) {
      this.uvs = var1;
      this.rotation = var2;
   }

   public float func_178348_a(int var1) {
      if (this.uvs == null) {
         throw new NullPointerException("uvs");
      }

      int var5 = this.func_178347_d(var1);
      return var5 != 0 && var5 != 1 ? this.uvs[2] : this.uvs[0];
   }

   public float func_178346_b(int var1) {
      if (this.uvs == null) {
         throw new NullPointerException("uvs");
      }

      int var5 = this.func_178347_d(var1);
      return var5 != 0 && var5 != 3 ? this.uvs[3] : this.uvs[1];
   }

   private int func_178347_d(int var1) {
      return (var1 + this.rotation / 90) % 4;
   }

   public int func_178345_c(int var1) {
      return (var1 + (4 - this.rotation / 90)) % 4;
   }

   public void setUvs(float[] var1) {
      if (this.uvs == null) {
         this.uvs = var1;
      }
   }

   public static class Deserializer implements JsonDeserializer<BlockFaceUV> {
      private static final long a = 71142802366384L;

      public BlockFaceUV deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         float[] var5 = this.parseUV(var4);
         int var6 = this.parseRotation(var4);
         return new BlockFaceUV(var5, var6);
      }

      public int parseRotation(JsonObject var1) {
         int var5 = JsonUtils.getInt(var1, "rotation", 0);
         if (var5 >= 0 && var5 % 90 == 0 && var5 / 90 <= 3) {
            return var5;
         } else {
            throw new JsonParseException("Invalid rotation " + var5 + " found, only 0/90/180/270 allowed");
         }
      }

      private float[] parseUV(JsonObject var1) {
         if (!var1.has("uv")) {
            return null;
         }

         JsonArray var5 = JsonUtils.getJsonArray(var1, "uv");
         if (var5.size() != 4) {
            throw new JsonParseException("Expected 4 uv values, found: " + var5.size());
         }

         float[] var6 = new float[4];

         for (int var7 = 0; var7 < var6.length; var7++) {
            var6[var7] = JsonUtils.getFloat(var5.get(var7), "uv[" + var7 + "]");
         }

         return var6;
      }
   }
}
