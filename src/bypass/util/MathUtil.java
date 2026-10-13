package bypass.util;

import java.text.DecimalFormat;
import net.minecraft.util.Vec3;

public final class MathUtil {
   public static final DecimalFormat ONE_DECIMAL = new DecimalFormat("#.#");
   private static final long a = 110026111294346L;

   public static double interpolate(double var0, double var2, float var4, double var5) {
      return var0 + (var2 - var0) * var4 - var5;
   }

   public static double randomInRange(double var0, double var2) {
      return Math.random() * (var2 - var0) + var0;
   }

   public static final Vec3 rotate(Vec3 var0, float var1, float var2) {
      double var7 = Math.toRadians(-1.0F * (var1 + 90.0F));
      double var9 = Math.toRadians(-var2);
      double var11 = Math.cos(var7);
      double var13 = Math.cos(var9);
      double var15 = Math.sin(var7);
      double var17 = Math.sin(var9);
      double var19 = var0.xCoord;
      double var21 = var0.yCoord;
      double var25 = var19 * var13 - var21 * var17;
      double var27 = var19 * var17 + var21 * var13;
      double var23 = var0.zCoord;
      var19 = var25;
      double var29 = var23 * var11 - var19 * var15;
      var25 = var23 * var15 + var19 * var11;
      return new Vec3(var25, var27, var29);
   }
}
