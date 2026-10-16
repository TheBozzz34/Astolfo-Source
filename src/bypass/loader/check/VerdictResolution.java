package bypass.loader.check;

import bypass.loader.AbstractLoadStage;
import bypass.loader.LoadStage;
import java.util.List;
import java.util.Random;

public final class VerdictResolution {
   private final CheckRequest request;
   private final List<StageCheck> checks;
   private final List<VerdictProcessor> processors;
   public final CachingVerdictResolver resolver;

   public VerdictResolution(CachingVerdictResolver cachingVerdictResolver, CheckContext checkContext) {
      this.resolver = cachingVerdictResolver;
      this.request = checkContext.getRequest();
      this.checks = checkContext.getChecks();
      this.processors = checkContext.getProcessors();
   }

   private Verdict resolveRequestStage() {
      return this.resolveStage(this.request.getStage());
   }

   private Verdict resolveStage(LoadStage loadStage) {
      for (Object object : CachingVerdictResolver.getCachedChecks(this.resolver, loadStage, this.checks)) {
         StageCheck stageCheck = (StageCheck)object;
         CheckResult checkResult = stageCheck.check(this.request);
         switch (checkResult.getKind()) {
            case A:
               return this.processVerdict((AbstractLoadStage)loadStage, checkResult.getVerdict());
            case B:
               return null;
            case C:
               System.out.println(this.generateString(-229985452) + " " + this.generateString(-147909649));
               new IntegerCacheScrambler();
         }
      }

      return Verdict.N;
   }

   private Verdict processVerdict(AbstractLoadStage abstractLoadStage, Verdict verdict) {
      return this.resolver.processRelation(this.processors, this.request, verdict, abstractLoadStage);
   }

   public String generateString(int i) {
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

   public static Verdict resolve(VerdictResolution verdictResolution) {
      return verdictResolution.resolveRequestStage();
   }
}
