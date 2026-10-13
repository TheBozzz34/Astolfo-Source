package net.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import net.minecraft.util.JsonUtils;

public class TextureMetadataSectionSerializer extends BaseMetadataSectionSerializer<TextureMetadataSection> {
   private static final long a = 84658953806962L;

   public TextureMetadataSection deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      JsonObject var7 = var1.getAsJsonObject();
      boolean var8 = JsonUtils.getBoolean(var7, "blur", false);
      boolean var9 = JsonUtils.getBoolean(var7, "clamp", false);
      ArrayList var10 = Lists.newArrayList();
      if (var7.has("mipmaps")) {
         try {
            JsonArray var11 = var7.getAsJsonArray("mipmaps");

            for (int var12 = 0; var12 < var11.size(); var12++) {
               JsonElement var13 = var11.get(var12);
               if (var13.isJsonPrimitive()) {
                  try {
                     var10.add(var13.getAsInt());
                  } catch (NumberFormatException var15) {
                     throw new JsonParseException("Invalid texture->mipmap->" + var12 + ": expected number, was " + var13, var15);
                  }
               } else if (var13.isJsonObject()) {
                  throw new JsonParseException("Invalid texture->mipmap->" + var12 + ": expected number, was " + var13);
               }
            }
         } catch (ClassCastException var16) {
            throw new JsonParseException("Invalid texture->mipmaps: expected array, was " + var7.get("mipmaps"), var16);
         }
      }

      return new TextureMetadataSection(var8, var9, var10);
   }

   @Override
   public String getSectionName() {
      return "texture";
   }
}
