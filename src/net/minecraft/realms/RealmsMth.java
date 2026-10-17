package net.minecraft.realms;

import java.util.Random;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.StringUtils;

public class RealmsMth {
   public static float P(float f) {
      return MathHelper.sin(f);
   }

   public static double nextDouble(Random random2, double d, double e) {
      return MathHelper.getRandomDoubleInRange(random2, d, e);
   }

   public static int N(float f) {
      return MathHelper.ceiling_float_int(f);
   }

   public static int m(double d) {
      return MathHelper.floor_double(d);
   }

   public static int k(int i, int j) {
      return MathHelper.bucketInt(i, j);
   }

   public static float W(float f) {
      return MathHelper.abs(f);
   }

   public static int clamp(int i, int j, int k) {
      return MathHelper.clamp_int(i, j, k);
   }

   public static double L(double d, double e, double d2) {
      return MathHelper.denormalizeClamp(d, e, d2);
   }

   public static int O(double d) {
      return MathHelper.ceiling_double_int(d);
   }

   public static boolean isEmpty(String string) {
      return StringUtils.isEmpty(string);
   }

   public static long lfloor(double d) {
      return MathHelper.floor_double_long(d);
   }

   public static float sqrt(double d) {
      return MathHelper.sqrt_double(d);
   }

   public static double X(double d, double e, double d2) {
      return MathHelper.clamp_double(d, e, d2);
   }

   public static int getInt(String string, int i) {
      return MathHelper.parseIntWithDefault(string, i);
   }

   public static double getDouble(String string, double d) {
      return MathHelper.parseDoubleWithDefault(string, d);
   }

   public static int c(int i) {
      return MathHelper.calculateLogBaseTwo(i);
   }

   public static int h(double d) {
      return MathHelper.func_154353_e(d);
   }

   public static int s(int i) {
      return MathHelper.roundUpToPowerOfTwo(i);
   }

   public static float H(float f) {
      return MathHelper.sqrt_float(f);
   }

   public static float K(float f) {
      return MathHelper.cos(f);
   }

   public static int getInt(String string, int i, int j) {
      return MathHelper.parseIntWithDefaultAndMax(string, i, j);
   }

   public static int L(double d) {
      return MathHelper.truncateDoubleToInt(d);
   }

   public static double absMax(double d, double e) {
      return MathHelper.abs_max(d, e);
   }

   public static float nextFloat(Random random2, float f, float g) {
      return MathHelper.randomFloatClamp(random2, f, g);
   }

   public static double wrapDegrees(double d) {
      return MathHelper.wrapAngleTo180_double(d);
   }

   public static float G(float f) {
      return MathHelper.wrapAngleTo180_float(f);
   }

   public static float clamp(float f, float g, float h) {
      return MathHelper.clamp_float(f, g, h);
   }

   public static double getDouble(String string, double d, double e) {
      return MathHelper.parseDoubleWithDefaultAndMax(string, d, e);
   }

   public static int u(int i, int j) {
      return MathHelper.roundUp(i, j);
   }

   public static double average(long[] longs) {
      return MathHelper.average(longs);
   }

   public static int p(float f) {
      return MathHelper.floor_float(f);
   }

   public static int p(int i) {
      return MathHelper.abs_int(i);
   }

   public static int nextInt(Random random2, int i, int j) {
      return MathHelper.getRandomIntegerInRange(random2, i, j);
   }
}
