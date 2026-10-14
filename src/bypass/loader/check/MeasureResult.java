package bypass.loader.check;

public final class MeasureResult {
   private MeasureStatus R;
   private String v;

   public MeasureResult(MeasureStatus measureStatus, String string) {
      this.R = measureStatus;
      this.v = string;
   }

   public static final MeasureResult R() {
      return new MeasureResult(MeasureStatus.SUCCESS, "");
   }

   public static final MeasureResult a(String string) {
      return new MeasureResult(MeasureStatus.FAILURE, string);
   }

   public static final MeasureResult B(String string) {
      return new MeasureResult(MeasureStatus.INTERRUPTED, string);
   }

   public MeasureStatus G() {
      return this.R;
   }

   public String w() {
      return this.v;
   }
}
