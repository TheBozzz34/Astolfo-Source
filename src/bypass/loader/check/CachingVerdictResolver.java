package bypass.loader.check;

import bypass.loader.LoadStage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CachingVerdictResolver extends AbstractVerdictResolver {
   private final Map<Class<? extends LoadStage>, List<StageCheck>> f = new ConcurrentHashMap<>();

   @Override
   public Verdict v(CheckContext var1) {
      return VerdictResolution.M(new VerdictResolution(this, var1));
   }

   private List<StageCheck> findCachedResolvers(LoadStage var1, List<StageCheck> var2) {
      Class var3 = var1.getClass();
      return this.f.computeIfAbsent(var3, var1x -> new ArrayList<>(var2));
   }

   public static List s(CachingVerdictResolver var0, LoadStage var1, List var2) {
      return var0.findCachedResolvers(var1, var2);
   }
}
