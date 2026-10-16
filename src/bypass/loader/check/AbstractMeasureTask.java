package bypass.loader.check;

public abstract class AbstractMeasureTask implements MeasureTask {
   private MeasureResult result;

   @Override
   public MeasureResult getResult() {
      return this.result;
   }

   public abstract void perform();

   @Override
   public void setResult(MeasureResult measureResult) {
      this.result = measureResult;
   }

   @Override
   public void validate() throws MeasureException {
   }
}
