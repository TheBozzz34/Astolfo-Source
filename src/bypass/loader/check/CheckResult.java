package bypass.loader.check;

public interface CheckResult {
   default Verdict getVerdict() {
      throw new UnsupportedOperationException();
   }

   ResultKind getKind();

   static CheckResult ofVerdict(Verdict verdict) {
      return VerdictResult.create(verdict);
   }

   static CheckResult undecided() {
      return UndecidedResult.INSTANCE;
   }
}
