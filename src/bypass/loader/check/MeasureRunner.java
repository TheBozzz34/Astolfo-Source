package bypass.loader.check;

public final class MeasureRunner {
   public static void run(MeasureRequest measureRequest) {
      boolean bl = false;

      try {
         MeasureRegistry.INSTANCE.runMeasures(measureRequest.getGroupId());
      } catch (MeasureException measureException) {
         bl = true;
      }

      if (!bl) {
         measureRequest.setCompleted(true);
      }
   }
}
