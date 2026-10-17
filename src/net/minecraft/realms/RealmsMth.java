package net.minecraft.realms;

import java.util.Random;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.StringUtils;

public class RealmsMth {
   public static float sin(float f) {
      return MathHelper.sin(f);
   }

   public static double nextDouble(Random random2, double d, double e) {
      return MathHelper.getRandomDoubleInRange(random2, d, e);
   }

   public static int ceilFloat(float f) {
      return MathHelper.ceiling_float_int(f);
   }

   public static int floorDouble(double d) {
      return MathHelper.floor_double(d);
   }

   public static int intFloorDiv(int i, int j) {
      return MathHelper.bucketInt(i, j);
   }

   public static float absFloat(float f) {
      return MathHelper.abs(f);
   }

   public static int clamp(int i, int j, int k) {
      return MathHelper.clamp_int(i, j, k);
   }

   public static double clampedLerp(double d, double e, double d2) {
      return MathHelper.denormalizeClamp(d, e, d2);
   }

   public static int ceilDouble(double d) {
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

   public static double clampDouble(double d, double e, double d2) {
      return MathHelper.clamp_double(d, e, d2);
   }

   public static int getInt(String string, int i) {
      return MathHelper.parseIntWithDefault(string, i);
   }

   public static double getDouble(String string, double d) {
      return MathHelper.parseDoubleWithDefault(string, d);
   }

   public static int log2(int i) {
      return MathHelper.calculateLogBaseTwo(i);
   }

   public static int absFloor(double d) {
      return MathHelper.func_154353_e(d);
   }

   public static int smallestEncompassingPowerOfTwo(int i) {
      return MathHelper.roundUpToPowerOfTwo(i);
   }

   public static float sqrtFloat(float f) {
      return MathHelper.sqrt_float(f);
   }

   public static float cos(float f) {
      return MathHelper.cos(f);
   }

   public static int getInt(String string, int i, int j) {
      return MathHelper.parseIntWithDefaultAndMax(string, i, j);
   }

   public static int fastFloor(double d) {
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

   public static float wrapDegreesFloat(float f) {
      return MathHelper.wrapAngleTo180_float(f);
   }

   public static float clamp(float f, float g, float h) {
      return MathHelper.clamp_float(f, g, h);
   }

   public static double getDouble(String string, double d, double e) {
      return MathHelper.parseDoubleWithDefaultAndMax(string, d, e);
   }

   public static int roundUp(int i, int j) {
      return MathHelper.roundUp(i, j);
   }

   public static double average(long[] longs) {
      return MathHelper.average(longs);
   }

   public static int floorFloat(float f) {
      return MathHelper.floor_float(f);
   }

   public static int absInt(int i) {
      return MathHelper.abs_int(i);
   }

   public static int nextInt(Random random2, int i, int j) {
      return MathHelper.getRandomIntegerInRange(random2, i, j);
   }
}
