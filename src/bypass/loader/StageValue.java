package bypass.loader;

public abstract class StageValue {
   private LoadStage x;
   private static final long a = 68914876055380L;

   public StageValue(LoadStage loadStage) {
      this.x = loadStage;
   }

   public abstract Object get();
}
