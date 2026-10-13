package bypass.hypixel;

public interface HypixelLevelMath {
   double B = 10000.0;
   double o = 2500.0;
   double l = 1250.0;
   double i = -3.5;
   double r = 12.25;
   double P = 8.0E-4;
   long a = 52548371690585L;

   static double V(double var0) {
      return var0 < 0.0 ? 1.0 : Math.floor(-2.5 + Math.sqrt(12.25 + 8.0E-4 * var0));
   }

   static double c(double var0) {
      return V(var0) + D(var0);
   }

   static double I(double var0) {
      return var0 < 1.0 ? 10000.0 : 2500.0 * (var0 - 1.0) + 10000.0;
   }

   static double T(double var0) {
      double var5 = Math.floor(var0);
      double var7 = q(var5);
      return var0 == var5 ? var7 : (q(var5 + 1.0) - var7) * (var0 % 1.0) + var7;
   }

   static double q(double var0) {
      return (1250.0 * (var0 - 2.0) + 10000.0) * (var0 - 1.0);
   }

   static double D(double var0) {
      double var2 = V(var0);
      double var4 = T(var2);
      return (var0 - var4) / (T(var2 + 1.0) - var4);
   }
}
