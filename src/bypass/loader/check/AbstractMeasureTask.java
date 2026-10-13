package bypass.loader.check;

public abstract class AbstractMeasureTask implements MeasureTask {
   private MeasureResult C;

   @Override
   public MeasureResult n() {
      return this.C;
   }

   public abstract void A();

   @Override
   public void J(MeasureResult var1) {
      this.C = var1;
   }

   @Override
   public void x() throws MeasureException {
   }
}
