package bypass.loader.check;

import bypass.manager.Loadable;

public class LoaderGuard implements Loadable {
   private final LoadStageList Q;
   private final StageVerification B;

   public LoaderGuard() {
      LoadStageListFactory loadStageListFactory = new LoadStageListFactory();
      this.Q = new LoadStageList(loadStageListFactory.e());
      this.B = new StageVerification(this.Q);
   }

   public LoadStageList e() {
      return this.Q;
   }

   public StageVerification U() {
      return this.B;
   }

   @Override
   public void load() {
      this.U(() -> {
         ThreadDeath threadDeath = this.B.J().t();
         if (threadDeath != null) {
            throw threadDeath;
         }
      });
   }

   @Override
   public void a() {
   }

   public void U(Task task) {
      task.c();
   }
}
