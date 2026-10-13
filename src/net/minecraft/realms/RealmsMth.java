package net.minecraft.realms;

import java.util.Random;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.StringUtils;

public class RealmsMth {
   public static float P(float var0) {
      return MathHelper.sin(var0);
   }

   public static double nextDouble(Random var0, double var1, double var3) {
      return MathHelper.getRandomDoubleInRange(var0, var1, var3);
   }

   public static int N(float var0) {
      return MathHelper.ceiling_float_int(var0);
   }

   public static int m(double var0) {
      return MathHelper.floor_double(var0);
   }

   public static int k(int var0, int var1) {
      return MathHelper.bucketInt(var0, var1);
   }

   public static float W(float var0) {
      return MathHelper.abs(var0);
   }

   public static int clamp(int var0, int var1, int var2) {
      return MathHelper.clamp_int(var0, var1, var2);
   }

   public static double L(double var0, double var2, double var4) {
      return MathHelper.denormalizeClamp(var0, var2, var4);
   }

   public static int O(double var0) {
      return MathHelper.ceiling_double_int(var0);
   }

   public static boolean isEmpty(String var0) {
      return StringUtils.isEmpty(var0);
   }

   public static long lfloor(double var0) {
      return MathHelper.floor_double_long(var0);
   }

   public static float sqrt(double var0) {
      return MathHelper.sqrt_double(var0);
   }

   public static double X(double var0, double var2, double var4) {
      return MathHelper.clamp_double(var0, var2, var4);
   }

   public static int getInt(String var0, int var1) {
      return MathHelper.parseIntWithDefault(var0, var1);
   }

   public static double getDouble(String var0, double var1) {
      return MathHelper.parseDoubleWithDefault(var0, var1);
   }

   public static int c(int var0) {
      return MathHelper.calculateLogBaseTwo(var0);
   }

   public static int h(double var0) {
      return MathHelper.func_154353_e(var0);
   }

   public static int s(int var0) {
      return MathHelper.roundUpToPowerOfTwo(var0);
   }

   public static float H(float var0) {
      return MathHelper.sqrt_float(var0);
   }

   public static float K(float var0) {
      return MathHelper.cos(var0);
   }

   public static int getInt(String var0, int var1, int var2) {
      return MathHelper.parseIntWithDefaultAndMax(var0, var1, var2);
   }

   public static int L(double var0) {
      return MathHelper.truncateDoubleToInt(var0);
   }

   public static double absMax(double var0, double var2) {
      return MathHelper.abs_max(var0, var2);
   }

   public static float nextFloat(Random var0, float var1, float var2) {
      return MathHelper.randomFloatClamp(var0, var1, var2);
   }

   public static double wrapDegrees(double var0) {
      return MathHelper.wrapAngleTo180_double(var0);
   }

   public static float G(float var0) {
      return MathHelper.wrapAngleTo180_float(var0);
   }

   public static float clamp(float var0, float var1, float var2) {
      return MathHelper.clamp_float(var0, var1, var2);
   }

   public static double getDouble(String var0, double var1, double var3) {
      return MathHelper.parseDoubleWithDefaultAndMax(var0, var1, var3);
   }

   public static int u(int var0, int var1) {
      return MathHelper.roundUp(var0, var1);
   }

   public static double average(long[] var0) {
      return MathHelper.average(var0);
   }

   public static int p(float var0) {
      return MathHelper.floor_float(var0);
   }

   public static int p(int var0) {
      return MathHelper.abs_int(var0);
   }

   public static int nextInt(Random var0, int var1, int var2) {
      return MathHelper.getRandomIntegerInRange(var0, var1, var2);
   }
}
