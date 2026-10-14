package net.minecraft.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

public class JsonUtils {
   public static boolean isString(JsonObject jsonObject, String string) {
      return !isJsonPrimitive(jsonObject, string) ? false : jsonObject.getAsJsonPrimitive(string).isString();
   }

   public static boolean isString(JsonElement jsonElement) {
      return !jsonElement.isJsonPrimitive() ? false : jsonElement.getAsJsonPrimitive().isString();
   }

   public static boolean isBoolean(JsonObject jsonObject, String string) {
      return !isJsonPrimitive(jsonObject, string) ? false : jsonObject.getAsJsonPrimitive(string).isBoolean();
   }

   public static boolean isJsonArray(JsonObject jsonObject, String string) {
      return !hasField(jsonObject, string) ? false : jsonObject.get(string).isJsonArray();
   }

   public static boolean isJsonPrimitive(JsonObject jsonObject, String string) {
      return !hasField(jsonObject, string) ? false : jsonObject.get(string).isJsonPrimitive();
   }

   public static boolean hasField(JsonObject jsonObject, String string) {
      return jsonObject == null ? false : jsonObject.get(string) != null;
   }

   public static String getString(JsonElement jsonElement, String string) {
      if (jsonElement.isJsonPrimitive()) {
         return jsonElement.getAsString();
      } else {
         throw new JsonSyntaxException("Expected " + string + " to be a string, was " + toString(jsonElement));
      }
   }

   public static String getString(JsonObject jsonObject, String string) {
      if (jsonObject.has(string)) {
         return getString(jsonObject.get(string), string);
      } else {
         throw new JsonSyntaxException("Missing " + string + ", expected to find a string");
      }
   }

   public static String getString(JsonObject jsonObject, String string, String string2) {
      return jsonObject.has(string) ? getString(jsonObject.get(string), string) : string2;
   }

   public static boolean getBoolean(JsonElement jsonElement, String string) {
      if (jsonElement.isJsonPrimitive()) {
         return jsonElement.getAsBoolean();
      } else {
         throw new JsonSyntaxException("Expected " + string + " to be a Boolean, was " + toString(jsonElement));
      }
   }

   public static boolean getBoolean(JsonObject jsonObject, String string) {
      if (jsonObject.has(string)) {
         return getBoolean(jsonObject.get(string), string);
      } else {
         throw new JsonSyntaxException("Missing " + string + ", expected to find a Boolean");
      }
   }

   public static boolean getBoolean(JsonObject jsonObject, String string, boolean bl) {
      return jsonObject.has(string) ? getBoolean(jsonObject.get(string), string) : bl;
   }

   public static float getFloat(JsonElement jsonElement, String string) {
      if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
         return jsonElement.getAsFloat();
      } else {
         throw new JsonSyntaxException("Expected " + string + " to be a Float, was " + toString(jsonElement));
      }
   }

   public static float getFloat(JsonObject jsonObject, String string) {
      if (jsonObject.has(string)) {
         return getFloat(jsonObject.get(string), string);
      } else {
         throw new JsonSyntaxException("Missing " + string + ", expected to find a Float");
      }
   }

   public static float getFloat(JsonObject jsonObject, String string, float f) {
      return jsonObject.has(string) ? getFloat(jsonObject.get(string), string) : f;
   }

   public static int getInt(JsonElement jsonElement, String string) {
      if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
         return jsonElement.getAsInt();
      } else {
         throw new JsonSyntaxException("Expected " + string + " to be a Int, was " + toString(jsonElement));
      }
   }

   public static int getInt(JsonObject jsonObject, String string) {
      if (jsonObject.has(string)) {
         return getInt(jsonObject.get(string), string);
      } else {
         throw new JsonSyntaxException("Missing " + string + ", expected to find a Int");
      }
   }

   public static int getInt(JsonObject jsonObject, String string, int i) {
      return jsonObject.has(string) ? getInt(jsonObject.get(string), string) : i;
   }

   public static JsonObject getJsonObject(JsonElement jsonElement, String string) {
      if (jsonElement.isJsonObject()) {
         return jsonElement.getAsJsonObject();
      } else {
         throw new JsonSyntaxException("Expected " + string + " to be a JsonObject, was " + toString(jsonElement));
      }
   }

   public static JsonObject getJsonObject(JsonObject base, String key) {
      if (base.has(key)) {
         return getJsonObject(base.get(key), key);
      } else {
         throw new JsonSyntaxException("Missing " + key + ", expected to find a JsonObject");
      }
   }

   public static JsonObject getJsonObject(JsonObject jsonObject, String string, JsonObject jsonObject2) {
      return jsonObject.has(string) ? getJsonObject(jsonObject.get(string), string) : jsonObject2;
   }

   public static JsonArray getJsonArray(JsonElement jsonElement, String string) {
      if (jsonElement.isJsonArray()) {
         return jsonElement.getAsJsonArray();
      } else {
         throw new JsonSyntaxException("Expected " + string + " to be a JsonArray, was " + toString(jsonElement));
      }
   }

   public static JsonArray getJsonArray(JsonObject jsonObject, String string) {
      if (jsonObject.has(string)) {
         return getJsonArray(jsonObject.get(string), string);
      } else {
         throw new JsonSyntaxException("Missing " + string + ", expected to find a JsonArray");
      }
   }

   public static JsonArray getJsonArray(JsonObject jsonObject, String string, JsonArray jsonArray) {
      return jsonObject.has(string) ? getJsonArray(jsonObject.get(string), string) : jsonArray;
   }

   public static String toString(JsonElement jsonElement) {
      String string = org.apache.commons.lang3.StringUtils.abbreviateMiddle(String.valueOf(jsonElement), "...", 10);
      if (jsonElement == null) {
         return "null (missing)";
      }

      if (jsonElement.isJsonNull()) {
         return "null (json)";
      }

      if (jsonElement.isJsonArray()) {
         return "an array (" + string + ")";
      }

      if (jsonElement.isJsonObject()) {
         return "an object (" + string + ")";
      }

      if (jsonElement.isJsonPrimitive()) {
         JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
         if (jsonPrimitive.isNumber()) {
            return "a number (" + string + ")";
         }

         if (jsonPrimitive.isBoolean()) {
            return "a boolean (" + string + ")";
         }
      }

      return string;
   }
}
