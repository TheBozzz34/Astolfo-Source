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

   public SoundList deserialize(JsonElement jsonElement, Type type2, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      JsonObject jsonObject = JsonUtils.getJsonObject(jsonElement, "entry");
      SoundList soundList = new SoundList();
      soundList.setReplaceExisting(JsonUtils.getBoolean(jsonObject, "replace", false));
      SoundCategory soundCategory = SoundCategory.getCategory(JsonUtils.getString(jsonObject, "category", SoundCategory.MASTER.getCategoryName()));
      soundList.setSoundCategory(soundCategory);
      Validate.notNull(soundCategory, "Invalid category");
      if (jsonObject.has("sounds")) {
         JsonArray jsonArray = JsonUtils.getJsonArray(jsonObject, "sounds");

         for (int i = 0; i < jsonArray.size(); i++) {
            JsonElement jsonElement2 = jsonArray.get(i);
            SoundList.SoundEntry soundEntry = new SoundList.SoundEntry();
            if (JsonUtils.isString(jsonElement2)) {
               soundEntry.setSoundEntryName(JsonUtils.getString(jsonElement2, "sound"));
            } else {
               JsonObject jsonObject2 = JsonUtils.getJsonObject(jsonElement2, "sound");
               soundEntry.setSoundEntryName(JsonUtils.getString(jsonObject2, "name"));
               if (jsonObject2.has("type")) {
                  SoundList.SoundEntry.Type type3 = SoundList.SoundEntry.Type.getType(JsonUtils.getString(jsonObject2, "type"));
                  Validate.notNull(type3, "Invalid type");
                  soundEntry.setSoundEntryType(type3);
               }

               if (jsonObject2.has("volume")) {
                  float f = JsonUtils.getFloat(jsonObject2, "volume");
                  Validate.isTrue(f > 0.0F, "Invalid volume");
                  soundEntry.setSoundEntryVolume(f);
               }

               if (jsonObject2.has("pitch")) {
                  float g = JsonUtils.getFloat(jsonObject2, "pitch");
                  Validate.isTrue(g > 0.0F, "Invalid pitch");
                  soundEntry.setSoundEntryPitch(g);
               }

               if (jsonObject2.has("weight")) {
                  int j = JsonUtils.getInt(jsonObject2, "weight");
                  Validate.isTrue(j > 0, "Invalid weight");
                  soundEntry.setSoundEntryWeight(j);
               }

               if (jsonObject2.has("stream")) {
                  soundEntry.setStreaming(JsonUtils.getBoolean(jsonObject2, "stream"));
               }
            }

            soundList.getSoundList().add(soundEntry);
         }
      }

      return soundList;
   }
}
