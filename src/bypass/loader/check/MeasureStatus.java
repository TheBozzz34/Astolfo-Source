package bypass.loader.check;

public enum MeasureStatus {
   SUCCESS,
   FAILURE,
   INTERRUPTED;

   private static final MeasureStatus[] q = new MeasureStatus[]{SUCCESS, FAILURE, INTERRUPTED};
}
