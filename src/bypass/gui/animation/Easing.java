package bypass.gui.animation;

import java.util.function.Function;

public enum Easing {
   LINEAR(double2 -> double2),
   CURVE(double2 -> double2 * double2),
   STEEP_CURVE(double2 -> double2 * double2 * double2),
   BEZIER_CURVE(double2 -> {
      double d = -1.0 + Math.sqrt(-double2 + 1.0);
      return d * d;
   }),
   INVERSE_CURVE(double2 -> -(double2 - 1.0) * (double2 - 1.0) + 1.0),
   INVERSE_STEEP_CURVE(double2 -> Math.pow(double2 - 1.0, 3.0) + 1.0),
   RUBBER(double2 -> -Math.sin(10.0 * double2) / (10.0 * double2) + 1.0),
   INERTIA(double2 -> Math.cbrt(double2 - 0.5) / 1.5874010519681994),
   INSTANT(double2 -> (double)Math.round(double2));

   private final Function<Double, Double> s;
   private static final Easing[] v = new Easing[]{LINEAR, CURVE, STEEP_CURVE, BEZIER_CURVE, INVERSE_CURVE, INVERSE_STEEP_CURVE, RUBBER, INERTIA, INSTANT};

   Easing(Function<Double, Double> function) {
      this.s = function;
   }

   public double r(double d) {
      return this.s.apply(d);
   }
}
