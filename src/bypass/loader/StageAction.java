package bypass.loader;

public abstract class StageAction {
   private final LoadStage a;

   public StageAction(LoadStage loadStage) {
      this.a = loadStage;
   }

   public abstract void run();

   public LoadStage getStage() {
      return this.a;
   }
}
