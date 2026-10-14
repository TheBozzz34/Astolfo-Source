package net.minecraft.client.resources.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

public class FontMetadataSectionSerializer extends BaseMetadataSectionSerializer<FontMetadataSection> {
   public FontMetadataSection deserialize(JsonElement jsonElement, Type type2, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      float[] floats = new float[256];
      float[] floats2 = new float[256];
      float[] floats3 = new float[256];
      float f = 1.0F;
      float g = 0.0F;
      float h = 0.0F;
      if (jsonObject.has("characters")) {
         if (!jsonObject.get("characters").isJsonObject()) {
            throw new JsonParseException("Invalid font->characters: expected object, was " + jsonObject.get("characters"));
         }

         JsonObject jsonObject2 = jsonObject.getAsJsonObject("characters");
         if (jsonObject2.has("default")) {
            if (!jsonObject2.get("default").isJsonObject()) {
               throw new JsonParseException("Invalid font->characters->default: expected object, was " + jsonObject2.get("default"));
            }

            JsonObject jsonObject3 = jsonObject2.getAsJsonObject("default");
            f = JsonUtils.getFloat(jsonObject3, "width", f);
            Validate.inclusiveBetween(0.0, Float.MAX_VALUE, f, "Invalid default width");
            g = JsonUtils.getFloat(jsonObject3, "spacing", g);
            Validate.inclusiveBetween(0.0, Float.MAX_VALUE, g, "Invalid default spacing");
            h = JsonUtils.getFloat(jsonObject3, "left", g);
            Validate.inclusiveBetween(0.0, Float.MAX_VALUE, h, "Invalid default left");
         }

         for (int i = 0; i < 256; i++) {
            JsonElement jsonElement2 = jsonObject2.get(Integer.toString(i));
            float f2 = f;
            float f3 = g;
            float f4 = h;
            if (jsonElement2 != null) {
               JsonObject jsonObject4 = JsonUtils.getJsonObject(jsonElement2, "characters[" + i + "]");
               f2 = JsonUtils.getFloat(jsonObject4, "width", f);
               Validate.inclusiveBetween(0.0, Float.MAX_VALUE, f2, "Invalid width");
               f3 = JsonUtils.getFloat(jsonObject4, "spacing", g);
               Validate.inclusiveBetween(0.0, Float.MAX_VALUE, f3, "Invalid spacing");
               f4 = JsonUtils.getFloat(jsonObject4, "left", h);
               Validate.inclusiveBetween(0.0, Float.MAX_VALUE, f4, "Invalid left");
            }

            floats[i] = f2;
            floats2[i] = f3;
            floats3[i] = f4;
         }
      }

      return new FontMetadataSection(floats, floats3, floats2);
   }

   @Override
   public String getSectionName() {
      return "font";
   }
}
