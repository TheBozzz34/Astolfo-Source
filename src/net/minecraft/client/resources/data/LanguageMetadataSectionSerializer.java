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
   private static final long a = 33089105192220L;

   public LanguageMetadataSection deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      JsonObject var7 = var1.getAsJsonObject();
      HashSet var8 = Sets.newHashSet();

      for (Entry var10 : var7.entrySet()) {
         String var11 = (String)var10.getKey();
         JsonObject var12 = JsonUtils.getJsonObject((JsonElement)var10.getValue(), "language");
         String var13 = JsonUtils.getString(var12, "region");
         String var14 = JsonUtils.getString(var12, "name");
         boolean var15 = JsonUtils.getBoolean(var12, "bidirectional", false);
         if (var13.isEmpty()) {
            throw new JsonParseException("Invalid language->'" + var11 + "'->region: empty value");
         }

         if (var14.isEmpty()) {
            throw new JsonParseException("Invalid language->'" + var11 + "'->name: empty value");
         }

         if (!var8.add(new Language(var11, var13, var14, var15))) {
            throw new JsonParseException("Duplicate language->'" + var11 + "' defined");
         }
      }

      return new LanguageMetadataSection(var8);
   }

   @Override
   public String getSectionName() {
      return "language";
   }
}
