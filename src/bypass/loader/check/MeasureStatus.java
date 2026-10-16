package bypass.loader.check;

public enum MeasureStatus {
   SUCCESS,
   FAILURE,
   INTERRUPTED;

   private static final MeasureStatus[] VALUES = new MeasureStatus[]{SUCCESS, FAILURE, INTERRUPTED};
}
