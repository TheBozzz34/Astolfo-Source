package bypass.loader.check;

import bypass.manager.Loadable;

public class LoaderGuard implements Loadable {
   private final LoadStageList stageList;
   private final StageVerification verification;

   public LoaderGuard() {
      LoadStageListFactory loadStageListFactory = new LoadStageListFactory();
      this.stageList = new LoadStageList(loadStageListFactory.getStageContainer());
      this.verification = new StageVerification(this.stageList);
   }

   public LoadStageList getStageList() {
      return this.stageList;
   }

   public StageVerification getVerification() {
      return this.verification;
   }

   @Override
   public void load() {
      this.runTask(() -> {
         ThreadDeath threadDeath = this.verification.getVerifier().verifyStages();
         if (threadDeath != null) {
            throw threadDeath;
         }
      });
   }

   @Override
   public void save() {
   }

   public void runTask(Task task) {
      task.run();
   }
}
