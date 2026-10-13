package bypass.loader.check;

public final class MeasureResult {
   private MeasureStatus R;
   private String v;
   private static final long a = 13144198824705L;

   public MeasureResult(MeasureStatus var1, String var2) {
      this.R = var1;
      this.v = var2;
   }

   public static final MeasureResult R() {
      return new MeasureResult(MeasureStatus.SUCCESS, "");
   }

   public static final MeasureResult a(String var0) {
      return new MeasureResult(MeasureStatus.FAILURE, var0);
   }

   public static final MeasureResult B(String var0) {
      return new MeasureResult(MeasureStatus.INTERRUPTED, var0);
   }

   public MeasureStatus G() {
      return this.R;
   }

   public String w() {
      return this.v;
   }
}
