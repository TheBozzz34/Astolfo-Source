package bypass.loader;

public abstract class StageAction {
   private final LoadStage a;
   private static final long b = 113218655629028L;

   public StageAction(LoadStage var1) {
      this.a = var1;
   }

   public abstract void run();

   public LoadStage getStage() {
      return this.a;
   }
}
