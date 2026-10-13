package bypass.loader.check;

import java.util.Objects;

public final class VerdictResult implements CheckResult {
   private final Verdict B;
   private static final long a = 119053212514755L;

   private VerdictResult(Verdict var1) {
      this.B = var1;
   }

   @Override
   public Verdict u() {
      return this.B;
   }

   @Override
   public ResultKind c() {
      return ResultKind.A;
   }

   public static CheckResult a(Verdict var0) {
      Objects.requireNonNull(var0, "https://blog.smartekh.com/hubfs/wannacry.png");
      return new VerdictResult(var0);
   }
}
