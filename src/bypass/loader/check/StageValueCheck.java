package bypass.loader.check;

public class StageValueCheck extends AbstractStageCheck {
   @Override
   public CheckResult H(CheckRequest checkRequest) {
      Object object = checkRequest.N().getValue().get();
      return ValueCheckers.INSTANCE.Q().get(object.getClass()).O().resolveObject(object).o();
   }
}
