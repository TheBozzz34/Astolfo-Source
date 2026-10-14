package bypass.loader.check;

import bypass.loader.LoadStage;

public class StageVerifier {
   private final LoadStageList S;
   private final DefaultCheckRegistry C;
   private static final long a = 27229906820923L;

   public StageVerifier(LoadStageList loadStageList, DefaultCheckRegistry defaultCheckRegistry) {
      this.S = loadStageList;
      this.C = defaultCheckRegistry;
   }

   public ThreadDeath t() {
      ThreadDeath threadDeath = null;

      for (LoadStage loadStage : this.S.a().L().getValue()) {
         Verdict verdict = this.C.u(new StageCheckRequest(loadStage));
         switch (verdict) {
            case H:
               threadDeath = null;
               break;
            default:
               threadDeath = new ThreadDeath();
               threadDeath.setStackTrace(new StackTraceElement[0]);
               return threadDeath;
         }
      }

      return threadDeath;
   }
}
