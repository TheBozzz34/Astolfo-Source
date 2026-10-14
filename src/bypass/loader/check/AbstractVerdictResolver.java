package bypass.loader.check;

import bypass.loader.AbstractLoadStage;
import java.util.List;

public abstract class AbstractVerdictResolver implements VerdictResolver {
   public final Verdict processRelation(List<VerdictProcessor> list, CheckRequest checkRequest, Verdict verdict, AbstractLoadStage abstractLoadStage) {
      abstractLoadStage.setState(1);
      abstractLoadStage.getAction().run();
      return list.isEmpty() ? verdict : list.stream().map(verdictProcessor -> verdictProcessor.K(checkRequest, verdict)).findFirst().orElse(verdict);
   }
}
