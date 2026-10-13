package bypass.gui.animation;

import java.util.function.Function;

public enum Easing {
   LINEAR(var0 -> var0),
   CURVE(var0 -> var0 * var0),
   STEEP_CURVE(var0 -> var0 * var0 * var0),
   BEZIER_CURVE(var0 -> {
      double var1 = -1.0 + Math.sqrt(-var0 + 1.0);
      return var1 * var1;
   }),
   INVERSE_CURVE(var0 -> -(var0 - 1.0) * (var0 - 1.0) + 1.0),
   INVERSE_STEEP_CURVE(var0 -> Math.pow(var0 - 1.0, 3.0) + 1.0),
   RUBBER(var0 -> -Math.sin(10.0 * var0) / (10.0 * var0) + 1.0),
   INERTIA(var0 -> Math.cbrt(var0 - 0.5) / 1.5874010519681994),
   INSTANT(var0 -> (double)Math.round(var0));

   private final Function<Double, Double> s;
   private static final Easing[] v = new Easing[]{LINEAR, CURVE, STEEP_CURVE, BEZIER_CURVE, INVERSE_CURVE, INVERSE_STEEP_CURVE, RUBBER, INERTIA, INSTANT};

   Easing(Function<Double, Double> var3) {
      this.s = var3;
   }

   public double r(double var1) {
      return this.s.apply(var1);
   }
}
