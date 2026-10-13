package net.minecraft.src;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class Json {
   private static final long a = 41659433334232L;

   public static float getFloat(JsonObject var0, String var1, float var2) {
      JsonElement var6 = var0.get(var1);
      return var6 == null ? var2 : var6.getAsFloat();
   }

   public static boolean getBoolean(JsonObject var0, String var1, boolean var2) {
      JsonElement var6 = var0.get(var1);
      return var6 == null ? var2 : var6.getAsBoolean();
   }

   public static String getString(JsonObject var0, String var1) {
      return getString(var0, var1, (String)null);
   }

   public static String getString(JsonObject var0, String var1, String var2) {
      JsonElement var6 = var0.get(var1);
      return var6 == null ? var2 : var6.getAsString();
   }

   public static float[] parseFloatArray(JsonElement var0, int var1) {
      return parseFloatArray(var0, var1, (float[])null);
   }

   public static float[] parseFloatArray(JsonElement var0, int var1, float[] var2) {
      if (var0 == null) {
         return var2;
      }

      JsonArray var6 = var0.getAsJsonArray();
      if (var6.size() != var1) {
         throw new JsonParseException("Wrong array length: " + var6.size() + ", should be: " + var1 + ", array: " + var6);
      }

      float[] var7 = new float[var6.size()];

      for (int var8 = 0; var8 < var7.length; var8++) {
         var7[var8] = var6.get(var8).getAsFloat();
      }

      return var7;
   }

   public static int[] parseIntArray(JsonElement var0, int var1) {
      return parseIntArray(var0, var1, (int[])null);
   }

   public static int[] parseIntArray(JsonElement var0, int var1, int[] var2) {
      if (var0 == null) {
         return var2;
      }

      JsonArray var6 = var0.getAsJsonArray();
      if (var6.size() != var1) {
         throw new JsonParseException("Wrong array length: " + var6.size() + ", should be: " + var1 + ", array: " + var6);
      }

      int[] var7 = new int[var6.size()];

      for (int var8 = 0; var8 < var7.length; var8++) {
         var7[var8] = var6.get(var8).getAsInt();
      }

      return var7;
   }
}
