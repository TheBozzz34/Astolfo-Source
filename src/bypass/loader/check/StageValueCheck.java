package bypass.loader.check;

public class StageValueCheck extends AbstractStageCheck {
   @Override
   public CheckResult check(CheckRequest checkRequest) {
      Object object = checkRequest.getStage().getValue().get();
      return ValueCheckers.INSTANCE.getCheckers().get(object.getClass()).getChecker().resolveObject(object).getResult();
   }
}
