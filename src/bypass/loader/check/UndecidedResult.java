package bypass.loader.check;

public enum UndecidedResult implements CheckResult {
   INSTANCE;

   @Override
   public ResultKind c() {
      return ResultKind.C;
   }
}
