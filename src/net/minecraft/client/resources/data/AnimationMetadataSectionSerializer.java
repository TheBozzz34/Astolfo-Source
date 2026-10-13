package net.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

public class AnimationMetadataSectionSerializer
   extends BaseMetadataSectionSerializer<AnimationMetadataSection>
   implements JsonSerializer<AnimationMetadataSection> {
   private static final long a = 18009762105614L;

   public AnimationMetadataSection deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      ArrayList var8 = Lists.newArrayList();
      JsonObject var9 = JsonUtils.getJsonObject(var1, "metadata section");
      int var10 = JsonUtils.getInt(var9, "frametime", 1);
      if (var10 != 1) {
         Validate.inclusiveBetween(1L, 2147483647L, var10, "Invalid default frame time");
      }

      if (var9.has("frames")) {
         try {
            JsonArray var11 = JsonUtils.getJsonArray(var9, "frames");

            for (int var12 = 0; var12 < var11.size(); var12++) {
               JsonElement var13 = var11.get(var12);
               AnimationFrame var14 = this.parseAnimationFrame(var12, var13);
               if (var14 != null) {
                  var8.add(var14);
               }
            }
         } catch (ClassCastException var15) {
            throw new JsonParseException("Invalid animation->frames: expected array, was " + var9.get("frames"), var15);
         }
      }

      int var16 = JsonUtils.getInt(var9, "width", -1);
      int var17 = JsonUtils.getInt(var9, "height", -1);
      if (var16 != -1) {
         Validate.inclusiveBetween(1L, 2147483647L, var16, "Invalid width");
      }

      if (var17 != -1) {
         Validate.inclusiveBetween(1L, 2147483647L, var17, "Invalid height");
      }

      boolean var18 = JsonUtils.getBoolean(var9, "interpolate", false);
      return new AnimationMetadataSection(var8, var16, var17, var10, var18);
   }

   private AnimationFrame parseAnimationFrame(int var1, JsonElement var2) {
      if (var2.isJsonPrimitive()) {
         return new AnimationFrame(JsonUtils.getInt(var2, "frames[" + var1 + "]"));
      }

      if (var2.isJsonObject()) {
         JsonObject var6 = JsonUtils.getJsonObject(var2, "frames[" + var1 + "]");
         int var7 = JsonUtils.getInt(var6, "time", -1);
         if (var6.has("time")) {
            Validate.inclusiveBetween(1L, 2147483647L, var7, "Invalid frame time");
         }

         int var8 = JsonUtils.getInt(var6, "index");
         Validate.inclusiveBetween(0L, 2147483647L, var8, "Invalid frame index");
         return new AnimationFrame(var8, var7);
      } else {
         return null;
      }
   }

   public JsonElement serialize(AnimationMetadataSection var1, Type var2, JsonSerializationContext var3) {
      JsonObject var7 = new JsonObject();
      var7.addProperty("frametime", var1.getFrameTime());
      if (var1.getFrameWidth() != -1) {
         var7.addProperty("width", var1.getFrameWidth());
      }

      if (var1.getFrameHeight() != -1) {
         var7.addProperty("height", var1.getFrameHeight());
      }

      if (var1.getFrameCount() > 0) {
         JsonArray var8 = new JsonArray();

         for (int var9 = 0; var9 < var1.getFrameCount(); var9++) {
            if (var1.frameHasTime(var9)) {
               JsonObject var10 = new JsonObject();
               var10.addProperty("index", var1.getFrameIndex(var9));
               var10.addProperty("time", var1.getFrameTimeSingle(var9));
               var8.add(var10);
            } else {
               var8.add(new JsonPrimitive(var1.getFrameIndex(var9)));
            }
         }

         var7.add("frames", var8);
      }

      return var7;
   }

   @Override
   public String getSectionName() {
      return "animation";
   }
}
