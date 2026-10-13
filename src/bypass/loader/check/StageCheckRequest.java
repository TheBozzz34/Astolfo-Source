package bypass.loader.check;

import bypass.loader.LoadStage;
import java.util.List;
import java.util.Objects;

public final class StageCheckRequest implements CheckRequest {
   private final LoadStage i;
   private static final long a = 78814139392859L;

   public StageCheckRequest(LoadStage var1) {
      this.i = var1;
   }

   @Override
   public LoadStage N() {
      return this.i;
   }

   @Override
   public CheckContext Y(List<StageCheck> var1, List<VerdictProcessor> var2) {
      Objects.requireNonNull(var1, "https://i.imgur.com/UDP1Ycm.png");
      return new DefaultCheckContext(this, var1, var2);
   }
}
