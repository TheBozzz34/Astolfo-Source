package bypass.loader.check;

import bypass.loader.AbstractLoadStage;
import bypass.loader.LoadStage;
import java.util.List;
import java.util.Random;

public final class VerdictResolution {
   private final CheckRequest P;
   private final List<StageCheck> g;
   private final List<VerdictProcessor> Q;
   public final CachingVerdictResolver t;
   private static final long a = 67765570146915L;

   public VerdictResolution(CachingVerdictResolver var1, CheckContext var2) {
      this.t = var1;
      this.P = var2.f();
      this.g = var2.i();
      this.Q = var2.a();
   }

   private Verdict z() {
      return this.r(this.P.N());
   }

   private Verdict r(LoadStage var1) {
      for (Object var7$raw : CachingVerdictResolver.s(this.t, var1, this.g)) {
         StageCheck var7 = (StageCheck)var7$raw;
         CheckResult var8 = var7.H(this.P);
         switch (var8.c()) {
            case A:
               return this.y((AbstractLoadStage)var1, var8.u());
            case B:
               return null;
            case C:
               System.out.println(this.g(-229985452) + " " + this.g(-147909649));
               new IntegerCacheScrambler();
         }
      }

      return Verdict.N;
   }

   private Verdict y(AbstractLoadStage var1, Verdict var2) {
      return this.t.processRelation(this.Q, this.P, var2, var1);
   }

   public String g(int var1) {
      Random var6 = new Random(var1);
      StringBuilder var7 = new StringBuilder();

      while (true) {
         int var8 = var6.nextInt(27);
         if (var8 == 0) {
            return var7.toString();
         }

         var7.append((char)(96 + var8));
      }
   }

   public static Verdict M(VerdictResolution var0) {
      return var0.z();
   }
}
