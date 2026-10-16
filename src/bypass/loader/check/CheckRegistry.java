package bypass.loader.check;

import java.util.List;

public interface CheckRegistry {
   Verdict resolve(CheckRequest checkRequest);

   boolean addCheck(StageCheck stageCheck);

   boolean removeCheck(StageCheck stageCheck);

   List<StageCheck> getChecks();

   boolean addProcessor(VerdictProcessor verdictProcessor);

   boolean removeProcessor(VerdictProcessor verdictProcessor);

   List<VerdictProcessor> getProcessors();
}
