package net.minecraft.client.audio;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

public class SoundListSerializer implements JsonDeserializer<SoundList> {
   private static final long a = 134838759470693L;

   public SoundList deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      JsonObject var8 = JsonUtils.getJsonObject(var1, "entry");
      SoundList var9 = new SoundList();
      var9.setReplaceExisting(JsonUtils.getBoolean(var8, "replace", false));
      SoundCategory var10 = SoundCategory.getCategory(JsonUtils.getString(var8, "category", SoundCategory.MASTER.getCategoryName()));
      var9.setSoundCategory(var10);
      Validate.notNull(var10, "Invalid category");
      if (var8.has("sounds")) {
         JsonArray var11 = JsonUtils.getJsonArray(var8, "sounds");

         for (int var12 = 0; var12 < var11.size(); var12++) {
            JsonElement var13 = var11.get(var12);
            SoundList.SoundEntry var14 = new SoundList.SoundEntry();
            if (JsonUtils.isString(var13)) {
               var14.setSoundEntryName(JsonUtils.getString(var13, "sound"));
            } else {
               JsonObject var15 = JsonUtils.getJsonObject(var13, "sound");
               var14.setSoundEntryName(JsonUtils.getString(var15, "name"));
               if (var15.has("type")) {
                  SoundList.SoundEntry.Type var16 = SoundList.SoundEntry.Type.getType(JsonUtils.getString(var15, "type"));
                  Validate.notNull(var16, "Invalid type");
                  var14.setSoundEntryType(var16);
               }

               if (var15.has("volume")) {
                  float var17 = JsonUtils.getFloat(var15, "volume");
                  Validate.isTrue(var17 > 0.0F, "Invalid volume");
                  var14.setSoundEntryVolume(var17);
               }

               if (var15.has("pitch")) {
                  float var18 = JsonUtils.getFloat(var15, "pitch");
                  Validate.isTrue(var18 > 0.0F, "Invalid pitch");
                  var14.setSoundEntryPitch(var18);
               }

               if (var15.has("weight")) {
                  int var19 = JsonUtils.getInt(var15, "weight");
                  Validate.isTrue(var19 > 0, "Invalid weight");
                  var14.setSoundEntryWeight(var19);
               }

               if (var15.has("stream")) {
                  var14.setStreaming(JsonUtils.getBoolean(var15, "stream"));
               }
            }

            var9.getSoundList().add(var14);
         }
      }

      return var9;
   }
}
