package bypass.loader.check;

import bypass.loader.LoadStage;
import java.util.List;
import java.util.Objects;

public final class StageCheckRequest implements CheckRequest {
   private final LoadStage stage;

   public StageCheckRequest(LoadStage loadStage) {
      this.stage = loadStage;
   }

   @Override
   public LoadStage getStage() {
      return this.stage;
   }

   @Override
   public CheckContext createContext(List<StageCheck> list, List<VerdictProcessor> list2) {
      Objects.requireNonNull(list, "https://i.imgur.com/UDP1Ycm.png");
      return new DefaultCheckContext(this, list, list2);
   }
}
