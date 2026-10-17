package bypass.util;

import java.text.DecimalFormat;
import net.minecraft.util.Vec3;

public final class MathUtil {
   public static final DecimalFormat ONE_DECIMAL = new DecimalFormat("#.#");

   public static double interpolate(double d, double e, float f, double d2) {
      return d + (e - d) * f - d2;
   }

   public static double randomInRange(double d, double e) {
      return Math.random() * (e - d) + d;
   }

   public static final Vec3 rotate(Vec3 vec3, float f, float g) {
      double d = Math.toRadians(-1.0F * (f + 90.0F));
      double e = Math.toRadians(-g);
      double d2 = Math.cos(d);
      double d3 = Math.cos(e);
      double d4 = Math.sin(d);
      double d5 = Math.sin(e);
      double d6 = vec3.xCoord;
      double d7 = vec3.yCoord;
      double d8 = d6 * d3 - d7 * d5;
      double d9 = d6 * d5 + d7 * d3;
      double d10 = vec3.zCoord;
      d6 = d8;
      double d11 = d10 * d2 - d6 * d4;
      d8 = d10 * d4 + d6 * d2;
      return new Vec3(d8, d9, d11);
   }
}
