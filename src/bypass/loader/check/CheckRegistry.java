package bypass.loader.check;

import java.util.List;

public interface CheckRegistry {
   Verdict u(CheckRequest checkRequest);

   boolean S(StageCheck stageCheck);

   boolean L(StageCheck stageCheck);

   List<StageCheck> P();

   boolean h(VerdictProcessor verdictProcessor);

   boolean P(VerdictProcessor verdictProcessor);

   List<VerdictProcessor> i();
}
