package bypass.loader.check;

public final class MeasureRunner {
   private static final long a = 140374493704060L;

   public static void g(MeasureRequest var0) {
      boolean var4 = false;

      try {
         MeasureRegistry.INSTANCE.N(var0.Q());
      } catch (MeasureException var6) {
         var4 = true;
      }

      if (!var4) {
         var0.L(true);
      }
   }
}
