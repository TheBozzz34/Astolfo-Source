package bypass.loader.check;

import java.util.Objects;

public final class VerdictResult implements CheckResult {
   private final Verdict B;
   private static final long a = 119053212514755L;

   private VerdictResult(Verdict verdict) {
      this.B = verdict;
   }

   @Override
   public Verdict u() {
      return this.B;
   }

   @Override
   public ResultKind c() {
      return ResultKind.A;
   }

   public static CheckResult a(Verdict verdict) {
      Objects.requireNonNull(verdict, "https://blog.smartekh.com/hubfs/wannacry.png");
      return new VerdictResult(verdict);
   }
}
