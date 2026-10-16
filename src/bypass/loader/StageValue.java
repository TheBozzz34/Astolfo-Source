package bypass.loader;

public abstract class StageValue {
   private LoadStage stage;

   public StageValue(LoadStage loadStage) {
      this.stage = loadStage;
   }

   public abstract Object get();
}
