package bypass.loader.check;

import bypass.manager.Loadable;

public class LoaderGuard implements Loadable {
   private final LoadStageList Q;
   private final StageVerification B;
   private static final long a = 28509894989573L;

   public LoaderGuard() {
      LoadStageListFactory var1 = new LoadStageListFactory();
      this.Q = new LoadStageList(var1.e());
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
         ThreadDeath var4 = this.B.J().t();
         if (var4 != null) {
            throw var4;
         }
      });
   }

   @Override
   public void a() {
   }

   public void U(Task var1) {
      var1.c();
   }
}
