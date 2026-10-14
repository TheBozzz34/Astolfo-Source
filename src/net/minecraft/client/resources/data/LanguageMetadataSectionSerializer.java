package net.minecraft.client.resources.data;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map.Entry;
import net.minecraft.client.resources.Language;
import net.minecraft.util.JsonUtils;

public class LanguageMetadataSectionSerializer extends BaseMetadataSectionSerializer<LanguageMetadataSection> {
   public LanguageMetadataSection deserialize(JsonElement jsonElement, Type type2, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      HashSet hashSet = Sets.newHashSet();

      for (Entry entry : jsonObject.entrySet()) {
         String string = (String)entry.getKey();
         JsonObject jsonObject2 = JsonUtils.getJsonObject((JsonElement)entry.getValue(), "language");
         String string2 = JsonUtils.getString(jsonObject2, "region");
         String string3 = JsonUtils.getString(jsonObject2, "name");
         boolean bl = JsonUtils.getBoolean(jsonObject2, "bidirectional", false);
         if (string2.isEmpty()) {
            throw new JsonParseException("Invalid language->'" + string + "'->region: empty value");
         }

         if (string3.isEmpty()) {
            throw new JsonParseException("Invalid language->'" + string + "'->name: empty value");
         }

         if (!hashSet.add(new Language(string, string2, string3, bl))) {
            throw new JsonParseException("Duplicate language->'" + string + "' defined");
         }
      }

      return new LanguageMetadataSection(hashSet);
   }

   @Override
   public String getSectionName() {
      return "language";
   }
}
