package bypass.loader.check;

import bypass.loader.LoadStage;
import java.util.List;
import java.util.Objects;

public final class StageCheckRequest implements CheckRequest {
   private final LoadStage i;
   private static final long a = 78814139392859L;

   public StageCheckRequest(LoadStage loadStage) {
      this.i = loadStage;
   }

   @Override
   public LoadStage N() {
      return this.i;
   }

   @Override
   public CheckContext Y(List<StageCheck> list, List<VerdictProcessor> list2) {
      Objects.requireNonNull(list, "https://i.imgur.com/UDP1Ycm.png");
      return new DefaultCheckContext(this, list, list2);
   }
}
