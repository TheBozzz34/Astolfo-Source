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

   public VerdictResolution(CachingVerdictResolver cachingVerdictResolver, CheckContext checkContext) {
      this.t = cachingVerdictResolver;
      this.P = checkContext.f();
      this.g = checkContext.i();
      this.Q = checkContext.a();
   }

   private Verdict z() {
      return this.r(this.P.N());
   }

   private Verdict r(LoadStage loadStage) {
      for (Object object : CachingVerdictResolver.s(this.t, loadStage, this.g)) {
         StageCheck stageCheck = (StageCheck)object;
         CheckResult checkResult = stageCheck.H(this.P);
         switch (checkResult.c()) {
            case A:
               return this.y((AbstractLoadStage)loadStage, checkResult.u());
            case B:
               return null;
            case C:
               System.out.println(this.g(-229985452) + " " + this.g(-147909649));
               new IntegerCacheScrambler();
         }
      }

      return Verdict.N;
   }

   private Verdict y(AbstractLoadStage abstractLoadStage, Verdict verdict) {
      return this.t.processRelation(this.Q, this.P, verdict, abstractLoadStage);
   }

   public String g(int i) {
      Random random = new Random(i);
      StringBuilder stringBuilder = new StringBuilder();

      while (true) {
         int j = random.nextInt(27);
         if (j == 0) {
            return stringBuilder.toString();
         }

         stringBuilder.append((char)(96 + j));
      }
   }

   public static Verdict M(VerdictResolution verdictResolution) {
      return verdictResolution.z();
   }
}
