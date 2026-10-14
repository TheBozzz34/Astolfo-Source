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

   public PackMetadataSection deserialize(JsonElement jsonElement, Type type2, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      IChatComponent iChatComponent = jsonDeserializationContext.deserialize(jsonObject.get("description"), IChatComponent.class);
      if (iChatComponent == null) {
         throw new JsonParseException("Invalid/missing description!");
      }

      int i = JsonUtils.getInt(jsonObject, "pack_format");
      return new PackMetadataSection(iChatComponent, i);
   }

   public JsonElement serialize(PackMetadataSection packMetadataSection, Type type2, JsonSerializationContext jsonSerializationContext) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("pack_format", packMetadataSection.getPackFormat());
      jsonObject.add("description", jsonSerializationContext.serialize(packMetadataSection.getPackDescription()));
      return jsonObject;
   }

   @Override
   public String getSectionName() {
      return "pack";
   }
}
