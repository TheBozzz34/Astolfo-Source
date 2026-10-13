package net.minecraft.client.resources.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonUtils;

public class PackMetadataSectionSerializer extends BaseMetadataSectionSerializer<PackMetadataSection> implements JsonSerializer<PackMetadataSection> {
   private static final long a = 79966011278257L;

   public PackMetadataSection deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      JsonObject var6 = var1.getAsJsonObject();
      IChatComponent var7 = var3.deserialize(var6.get("description"), IChatComponent.class);
      if (var7 == null) {
         throw new JsonParseException("Invalid/missing description!");
      }

      int var8 = JsonUtils.getInt(var6, "pack_format");
      return new PackMetadataSection(var7, var8);
   }

   public JsonElement serialize(PackMetadataSection var1, Type var2, JsonSerializationContext var3) {
      JsonObject var6 = new JsonObject();
      var6.addProperty("pack_format", var1.getPackFormat());
      var6.add("description", var3.serialize(var1.getPackDescription()));
      return var6;
   }

   @Override
   public String getSectionName() {
      return "pack";
   }
}
