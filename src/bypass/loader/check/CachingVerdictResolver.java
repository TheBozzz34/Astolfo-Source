package bypass.loader.check;

import bypass.loader.LoadStage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CachingVerdictResolver extends AbstractVerdictResolver {
   private final Map<Class<? extends LoadStage>, List<StageCheck>> f = new ConcurrentHashMap<>();

   @Override
   public Verdict v(CheckContext checkContext) {
      return VerdictResolution.M(new VerdictResolution(this, checkContext));
   }

   private List<StageCheck> findCachedResolvers(LoadStage loadStage, List<StageCheck> list) {
      Class class2 = loadStage.getClass();
      return this.f.computeIfAbsent(class2, class3 -> new ArrayList<>(list));
   }

   public static List s(CachingVerdictResolver cachingVerdictResolver, LoadStage loadStage, List list) {
      return cachingVerdictResolver.findCachedResolvers(loadStage, list);
   }
}
