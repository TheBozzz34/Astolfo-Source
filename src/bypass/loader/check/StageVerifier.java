package bypass.loader.check;

import bypass.loader.LoadStage;

public class StageVerifier {
   private final LoadStageList stageList;
   private final DefaultCheckRegistry registry;

   public StageVerifier(LoadStageList loadStageList, DefaultCheckRegistry defaultCheckRegistry) {
      this.stageList = loadStageList;
      this.registry = defaultCheckRegistry;
   }

   public ThreadDeath verifyStages() {
      ThreadDeath threadDeath = null;

      for (LoadStage loadStage : this.stageList.getStageContainer().getOwnedContainer().getValue()) {
         Verdict verdict = this.registry.resolve(new StageCheckRequest(loadStage));
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
