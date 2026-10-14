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
   public AnimationMetadataSection deserialize(JsonElement jsonElement, Type type2, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      ArrayList arrayList = Lists.newArrayList();
      JsonObject jsonObject = JsonUtils.getJsonObject(jsonElement, "metadata section");
      int i = JsonUtils.getInt(jsonObject, "frametime", 1);
      if (i != 1) {
         Validate.inclusiveBetween(1L, 2147483647L, i, "Invalid default frame time");
      }

      if (jsonObject.has("frames")) {
         try {
            JsonArray jsonArray = JsonUtils.getJsonArray(jsonObject, "frames");

            for (int j = 0; j < jsonArray.size(); j++) {
               JsonElement jsonElement2 = jsonArray.get(j);
               AnimationFrame animationFrame = this.parseAnimationFrame(j, jsonElement2);
               if (animationFrame != null) {
                  arrayList.add(animationFrame);
               }
            }
         } catch (ClassCastException classCastException) {
            throw new JsonParseException("Invalid animation->frames: expected array, was " + jsonObject.get("frames"), classCastException);
         }
      }

      int k = JsonUtils.getInt(jsonObject, "width", -1);
      int l = JsonUtils.getInt(jsonObject, "height", -1);
      if (k != -1) {
         Validate.inclusiveBetween(1L, 2147483647L, k, "Invalid width");
      }

      if (l != -1) {
         Validate.inclusiveBetween(1L, 2147483647L, l, "Invalid height");
      }

      boolean bl = JsonUtils.getBoolean(jsonObject, "interpolate", false);
      return new AnimationMetadataSection(arrayList, k, l, i, bl);
   }

   private AnimationFrame parseAnimationFrame(int i, JsonElement jsonElement) {
      if (jsonElement.isJsonPrimitive()) {
         return new AnimationFrame(JsonUtils.getInt(jsonElement, "frames[" + i + "]"));
      }

      if (jsonElement.isJsonObject()) {
         JsonObject jsonObject = JsonUtils.getJsonObject(jsonElement, "frames[" + i + "]");
         int j = JsonUtils.getInt(jsonObject, "time", -1);
         if (jsonObject.has("time")) {
            Validate.inclusiveBetween(1L, 2147483647L, j, "Invalid frame time");
         }

         int k = JsonUtils.getInt(jsonObject, "index");
         Validate.inclusiveBetween(0L, 2147483647L, k, "Invalid frame index");
         return new AnimationFrame(k, j);
      } else {
         return null;
      }
   }

   public JsonElement serialize(AnimationMetadataSection animationMetadataSection, Type type2, JsonSerializationContext jsonSerializationContext) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("frametime", animationMetadataSection.getFrameTime());
      if (animationMetadataSection.getFrameWidth() != -1) {
         jsonObject.addProperty("width", animationMetadataSection.getFrameWidth());
      }

      if (animationMetadataSection.getFrameHeight() != -1) {
         jsonObject.addProperty("height", animationMetadataSection.getFrameHeight());
      }

      if (animationMetadataSection.getFrameCount() > 0) {
         JsonArray jsonArray = new JsonArray();

         for (int i = 0; i < animationMetadataSection.getFrameCount(); i++) {
            if (animationMetadataSection.frameHasTime(i)) {
               JsonObject jsonObject2 = new JsonObject();
               jsonObject2.addProperty("index", animationMetadataSection.getFrameIndex(i));
               jsonObject2.addProperty("time", animationMetadataSection.getFrameTimeSingle(i));
               jsonArray.add(jsonObject2);
            } else {
               jsonArray.add(new JsonPrimitive(animationMetadataSection.getFrameIndex(i)));
            }
         }

         jsonObject.add("frames", jsonArray);
      }

      return jsonObject;
   }

   @Override
   public String getSectionName() {
      return "animation";
   }
}
