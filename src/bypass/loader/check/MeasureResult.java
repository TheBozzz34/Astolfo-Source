package bypass.loader.check;

public final class MeasureResult {
   private MeasureStatus status;
   private String message;

   public MeasureResult(MeasureStatus measureStatus, String string) {
      this.status = measureStatus;
      this.message = string;
   }

   public static final MeasureResult success() {
      return new MeasureResult(MeasureStatus.SUCCESS, "");
   }

   public static final MeasureResult failure(String string) {
      return new MeasureResult(MeasureStatus.FAILURE, string);
   }

   public static final MeasureResult interrupted(String string) {
      return new MeasureResult(MeasureStatus.INTERRUPTED, string);
   }

   public MeasureStatus getStatus() {
      return this.status;
   }

   public String getMessage() {
      return this.message;
   }
}
