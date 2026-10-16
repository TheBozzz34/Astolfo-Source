package bypass.loader;

public abstract class StageAction {
   private final LoadStage stage;

   public StageAction(LoadStage loadStage) {
      this.stage = loadStage;
   }

   public abstract void run();

   public LoadStage getStage() {
      return this.stage;
   }
}
