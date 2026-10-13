package bypass.loader.check;

import bypass.loader.LoadStage;
import java.util.List;
import java.util.Objects;

public interface CheckRequest {
   LoadStage N();

   CheckContext Y(List<StageCheck> var1, List<VerdictProcessor> var2);

   static CheckRequest G(LoadStage var0) {
      Objects.requireNonNull(var0, "");
      return new StageCheckRequest(var0);
   }
}
