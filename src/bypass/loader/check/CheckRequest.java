package bypass.loader.check;

import bypass.loader.LoadStage;
import java.util.List;
import java.util.Objects;

public interface CheckRequest {
   LoadStage N();

   CheckContext Y(List<StageCheck> list, List<VerdictProcessor> list2);

   static CheckRequest G(LoadStage loadStage) {
      Objects.requireNonNull(loadStage, "");
      return new StageCheckRequest(loadStage);
   }
}
