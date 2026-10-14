package bypass.loader;

public abstract class StageValue {
   private LoadStage x;

   public StageValue(LoadStage loadStage) {
      this.x = loadStage;
   }

   public abstract Object get();
}
