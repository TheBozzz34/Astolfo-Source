package bypass.loader.check;

import bypass.loader.AbstractLoadStage;
import java.util.List;

public abstract class AbstractVerdictResolver implements VerdictResolver {
   private static final long a = 76963206716186L;

   public final Verdict processRelation(List<VerdictProcessor> var1, CheckRequest var2, Verdict var3, AbstractLoadStage var4) {
      var4.setState(1);
      var4.getAction().run();
      return var1.isEmpty() ? var3 : var1.stream().map(var2x -> var2x.K(var2, var3)).findFirst().orElse(var3);
   }
}
