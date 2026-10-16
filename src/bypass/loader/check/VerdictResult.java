package bypass.loader.check;

import java.util.Objects;

public final class VerdictResult implements CheckResult {
   private final Verdict verdict;

   private VerdictResult(Verdict verdict) {
      this.verdict = verdict;
   }

   @Override
   public Verdict getVerdict() {
      return this.verdict;
   }

   @Override
   public ResultKind getKind() {
      return ResultKind.A;
   }

   public static CheckResult create(Verdict verdict) {
      Objects.requireNonNull(verdict, "https://blog.smartekh.com/hubfs/wannacry.png");
      return new VerdictResult(verdict);
   }
}
