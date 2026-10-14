package bypass.hypixel;

public interface HypixelLevelMath {
   double B = 10000.0;
   double o = 2500.0;
   double l = 1250.0;
   double i = -3.5;
   double r = 12.25;
   double P = 8.0E-4;
   long a = 52548371690585L;

   static double V(double d) {
      return d < 0.0 ? 1.0 : Math.floor(-2.5 + Math.sqrt(12.25 + 8.0E-4 * d));
   }

   static double c(double d) {
      return V(d) + D(d);
   }

   static double I(double d) {
      return d < 1.0 ? 10000.0 : 2500.0 * (d - 1.0) + 10000.0;
   }

   static double T(double d) {
      double e = Math.floor(d);
      double d2 = q(e);
      return d == e ? d2 : (q(e + 1.0) - d2) * (d % 1.0) + d2;
   }

   static double q(double d) {
      return (1250.0 * (d - 2.0) + 10000.0) * (d - 1.0);
   }

   static double D(double d) {
      double e = V(d);
      double d2 = T(e);
      return (d - d2) / (T(e + 1.0) - d2);
   }
}
