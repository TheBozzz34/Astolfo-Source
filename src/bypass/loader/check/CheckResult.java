package bypass.loader.check;

public interface CheckResult {
   default Verdict u() {
      throw new UnsupportedOperationException();
   }

   ResultKind c();

   static CheckResult S(Verdict var0) {
      return VerdictResult.a(var0);
   }

   static CheckResult I() {
      return UndecidedResult.INSTANCE;
   }
}
