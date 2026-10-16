package bypass.loader.check;

import java.util.List;

public interface CheckContext {
   CheckRequest getRequest();

   List<StageCheck> getChecks();

   List<VerdictProcessor> getProcessors();
}
