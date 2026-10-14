package bypass.loader.check;

public interface CheckResult {
   default Verdict u() {
      throw new UnsupportedOperationException();
   }

   ResultKind c();

   static CheckResult S(Verdict verdict) {
      return VerdictResult.a(verdict);
   }

   static CheckResult I() {
      return UndecidedResult.INSTANCE;
   }
}
