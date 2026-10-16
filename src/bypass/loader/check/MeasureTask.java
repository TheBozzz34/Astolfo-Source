package bypass.loader.check;

public interface MeasureTask {
   MeasureResult getResult();

   void setResult(MeasureResult measureResult);

   void validate();
}
