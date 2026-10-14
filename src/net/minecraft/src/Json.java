package net.minecraft.src;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class Json {
   public static float getFloat(JsonObject obj, String field, float def) {
      JsonElement jsonElement = obj.get(field);
      return jsonElement == null ? def : jsonElement.getAsFloat();
   }

   public static boolean getBoolean(JsonObject obj, String field, boolean def) {
      JsonElement jsonElement = obj.get(field);
      return jsonElement == null ? def : jsonElement.getAsBoolean();
   }

   public static String getString(JsonObject jsonObj, String field) {
      return getString(jsonObj, field, (String)null);
   }

   public static String getString(JsonObject jsonObj, String field, String def) {
      JsonElement jsonElement = jsonObj.get(field);
      return jsonElement == null ? def : jsonElement.getAsString();
   }

   public static float[] parseFloatArray(JsonElement jsonElement, int lenIn) {
      return parseFloatArray(jsonElement, lenIn, (float[])null);
   }

   public static float[] parseFloatArray(JsonElement jsonElement, int lenIn, float[] def) {
      if (jsonElement == null) {
         return def;
      }

      JsonArray jsonArray = jsonElement.getAsJsonArray();
      if (jsonArray.size() != lenIn) {
         throw new JsonParseException("Wrong array length: " + jsonArray.size() + ", should be: " + lenIn + ", array: " + jsonArray);
      }

      float[] floats = new float[jsonArray.size()];

      for (int i = 0; i < floats.length; i++) {
         floats[i] = jsonArray.get(i).getAsFloat();
      }

      return floats;
   }

   public static int[] parseIntArray(JsonElement jsonElement, int lenIn) {
      return parseIntArray(jsonElement, lenIn, (int[])null);
   }

   public static int[] parseIntArray(JsonElement jsonElement, int lenIn, int[] def) {
      if (jsonElement == null) {
         return def;
      }

      JsonArray jsonArray = jsonElement.getAsJsonArray();
      if (jsonArray.size() != lenIn) {
         throw new JsonParseException("Wrong array length: " + jsonArray.size() + ", should be: " + lenIn + ", array: " + jsonArray);
      }

      int[] ints = new int[jsonArray.size()];

      for (int i = 0; i < ints.length; i++) {
         ints[i] = jsonArray.get(i).getAsInt();
      }

      return ints;
   }
}
