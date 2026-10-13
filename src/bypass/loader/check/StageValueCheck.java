package bypass.loader.check;

public class StageValueCheck extends AbstractStageCheck {
   private static final long a = 3237872057060L;

   @Override
   public CheckResult H(CheckRequest var1) {
      Object var6 = var1.N().getValue().get();
      return ValueCheckers.INSTANCE.Q().get(var6.getClass()).O().resolveObject(var6).o();
   }
}
