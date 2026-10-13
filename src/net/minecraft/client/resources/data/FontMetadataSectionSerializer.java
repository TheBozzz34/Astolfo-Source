package net.minecraft.client.resources.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

public class FontMetadataSectionSerializer extends BaseMetadataSectionSerializer<FontMetadataSection> {
   private static final long a = 926604860885L;

   public FontMetadataSection deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      JsonObject var7 = var1.getAsJsonObject();
      float[] var8 = new float[256];
      float[] var9 = new float[256];
      float[] var10 = new float[256];
      float var11 = 1.0F;
      float var12 = 0.0F;
      float var13 = 0.0F;
      if (var7.has("characters")) {
         if (!var7.get("characters").isJsonObject()) {
            throw new JsonParseException("Invalid font->characters: expected object, was " + var7.get("characters"));
         }

         JsonObject var14 = var7.getAsJsonObject("characters");
         if (var14.has("default")) {
            if (!var14.get("default").isJsonObject()) {
               throw new JsonParseException("Invalid font->characters->default: expected object, was " + var14.get("default"));
            }

            JsonObject var15 = var14.getAsJsonObject("default");
            var11 = JsonUtils.getFloat(var15, "width", var11);
            Validate.inclusiveBetween(0.0, Float.MAX_VALUE, var11, "Invalid default width");
            var12 = JsonUtils.getFloat(var15, "spacing", var12);
            Validate.inclusiveBetween(0.0, Float.MAX_VALUE, var12, "Invalid default spacing");
            var13 = JsonUtils.getFloat(var15, "left", var12);
            Validate.inclusiveBetween(0.0, Float.MAX_VALUE, var13, "Invalid default left");
         }

         for (int var21 = 0; var21 < 256; var21++) {
            JsonElement var16 = var14.get(Integer.toString(var21));
            float var17 = var11;
            float var18 = var12;
            float var19 = var13;
            if (var16 != null) {
               JsonObject var20 = JsonUtils.getJsonObject(var16, "characters[" + var21 + "]");
               var17 = JsonUtils.getFloat(var20, "width", var11);
               Validate.inclusiveBetween(0.0, Float.MAX_VALUE, var17, "Invalid width");
               var18 = JsonUtils.getFloat(var20, "spacing", var12);
               Validate.inclusiveBetween(0.0, Float.MAX_VALUE, var18, "Invalid spacing");
               var19 = JsonUtils.getFloat(var20, "left", var13);
               Validate.inclusiveBetween(0.0, Float.MAX_VALUE, var19, "Invalid left");
            }

            var8[var21] = var17;
            var9[var21] = var18;
            var10[var21] = var19;
         }
      }

      return new FontMetadataSection(var8, var10, var9);
   }

   @Override
   public String getSectionName() {
      return "font";
   }
}
