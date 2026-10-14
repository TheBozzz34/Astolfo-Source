package bypass.loader.check;

public final class MeasureException extends RuntimeException {
   public MeasureException(String string) {
      super(string);
   }

   public MeasureException(String string, Throwable throwable) {
      super(string, throwable);
   }
}
