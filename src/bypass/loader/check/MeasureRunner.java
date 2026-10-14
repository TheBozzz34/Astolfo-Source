package bypass.loader.check;

public final class MeasureRunner {
   public static void g(MeasureRequest measureRequest) {
      boolean bl = false;

      try {
         MeasureRegistry.INSTANCE.N(measureRequest.Q());
      } catch (MeasureException measureException) {
         bl = true;
      }

      if (!bl) {
         measureRequest.L(true);
      }
   }
}
